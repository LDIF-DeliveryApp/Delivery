package com.ldif.delivery.order.application.service;

import com.ldif.delivery.global.infrastructure.presentation.dto.PageResponseDto;
import com.ldif.delivery.menu.domain.entity.MenuEntity;
import com.ldif.delivery.menu.domain.repository.MenuRepository;
import com.ldif.delivery.order.domain.entity.OrderEntity;
import com.ldif.delivery.order.domain.entity.OrderItemEntity;
import com.ldif.delivery.order.domain.entity.OrderStatus;
import com.ldif.delivery.order.domain.entity.OrderType;
import com.ldif.delivery.order.domain.repository.OrderRepository;
import com.ldif.delivery.order.presentation.dto.*;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.store.domain.repository.StoreRepository;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceV1 {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    private static final int CANCEL_LIMIT_MINUTES = 5;

    // ───────────────────────────────────────────────────────────
    // 주문 생성 (CUSTOMER)
    // ───────────────────────────────────────────────────────────
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest req, String customerId) {

        //1. 주문자 조회
        UserEntity customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 사용자입니다. customerId=" + customerId));

        // 2. 가게 운영 상태 검증
        StoreEntity store = storeRepository.findById(req.storeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 가게입니다. storeId=" + req.storeId()));

        if (store.isHidden()) {
            throw new IllegalArgumentException("현재 운영 중이지 않은 가게입니다.");
        }

        // 3. 배송지 조회 ( 선택값, null허용)
//        AddressEntity address = null;
//        if (req.addressId() != null) {
//            address = addressRepository.findById(req.addressId())
//                    .orElseThrow(() -> new IllegalArgumentException(
//                            "존재하지 않는 배송지입니다. addressId=" + req.addressId()));
//        }

        // 4. 메뉴 일괄 조회
        Set<UUID> menuIds = req.items().stream()
                .map(OrderCreateRequest.OrderItemRequest::menuId)
                .collect(Collectors.toSet());

        List<MenuEntity> menus = menuRepository.findAllById(menuIds);

        // 5. 존재하지 않는 메뉴 검증
        Set<UUID> foundIds = menus.stream()
                .map(MenuEntity::getMenuId)
                .collect(Collectors.toSet());

        menuIds.forEach(id -> {
            if (!foundIds.contains(id)) {
                throw new IllegalArgumentException("존재하지 않는 메뉴입니다. menuId=" + id);
            }
        });

        // 6. 메뉴가 해당 가게 소속인지 검증
        menus.forEach(menu -> {
            if (!menu.getStoreId().equals(req.storeId())) {
                throw new IllegalArgumentException(
                        "해당 가게의 메뉴가 아닙니다. menuId=" + menu.getMenuId());
            }
        });

        // 7. 메뉴 주문 가능 상태 검증 (삭제, 숨김)
        menus.forEach(menu -> {
            // 7-1. 삭제 여부
            if (menu.getDeletedAt() != null) {
                throw new IllegalArgumentException(
                        "삭제된 메뉴입니다. menuId=" + menu.getMenuId());
            }

            // 7-2. 숨김 여부
            if (Boolean.TRUE.equals(menu.getIsHidden())) {
                throw new IllegalArgumentException(
                        "현재 주문할 수 없는 메뉴입니다. menuId=" + menu.getMenuId());
            }
        });

        // 8. 단가 스냅샷 맵 구성
        Map<UUID, MenuEntity> menuMap = menus.stream()
                .collect(Collectors.toMap(MenuEntity::getMenuId, m -> m));

        // 9. OrderItem 생성
        List<OrderItemEntity> items = req.items().stream()
                .map(i -> {
                    MenuEntity menu = menuMap.get(i.menuId());
                    return OrderItemEntity.of(
                            menu,
                            i.quantity(),
                            menu.getPrice()
                    );
                })
                .toList();

        int totalPrice = items.stream()
                .mapToInt(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        // 10. Order 생성
        OrderEntity order = OrderEntity.create(
                customer,
                store,
                req.addressId(),    // address로 변경 예정
                OrderType.valueOf(req.orderType()),
                req.request(),
                items,
                totalPrice
        );

        return OrderResponse.from(orderRepository.save(order));
    }

    // ───────────────────────────────────────────────────────────
    // 주문 목록 조회
    // ───────────────────────────────────────────────────────────
    public PageResponseDto<OrderResponse> getOrder(
            String requesterId,
            String requesterRole,
            UUID storeId,
            OrderStatus status,
            Pageable pageable
    ) {

        String customerIdFilter = null;
        UUID storeIdFilter = storeId;

        switch (requesterRole) {
            case "CUSTOMER" -> customerIdFilter = requesterId;

            case "OWNER" -> {
                if (storeIdFilter != null) {
                    // OWNER가 storeId를 명시한 경우 본인 소유 가게인지 검증
                    validateStoreOwner(storeIdFilter, requesterId);
                }
            }
            // MANAGER, MASTER : 전체 조회
        }

        Page<OrderEntity> page = orderRepository.searchOrders(
                customerIdFilter, storeIdFilter, status, pageable);

        return new PageResponseDto<>(page.map(OrderResponse::from));
    }

    // ───────────────────────────────────────────────────────────
    // 주문 상세 조회
    // ───────────────────────────────────────────────────────────
    public OrderResponse getOrder(UUID orderId, String requesterId, String requesterRole) {

        OrderEntity order = findActiveOrder(orderId);
        validateReadAccess(order, requesterId, requesterRole);
        return OrderResponse.from(order);
    }

    // ───────────────────────────────────────────────────────────
    // 주문 수정 – 요청사항 (CUSTOMER / PENDING)
    // ───────────────────────────────────────────────────────────
    @Transactional
    public OrderResponse updateOrder(UUID orderId, OrderUpdateRequest req,
                                     String requesterId, String requesterRole) {

        OrderEntity order = findActiveOrder(orderId);

        if ("MASTER".equals(requesterRole)) {
            order.updateRequestByMaster(req.request());
        } else {
            // CUSTOMER: 본인 주문인지 확인
            validateOwnership(order, requesterId);
            order.updateRequest(req.request()); // PENDING 아니면 내부에서 예외
        }

        return OrderResponse.from(order);
    }

    // ───────────────────────────────────────────────────────────
    // 주문 상태 변경 (OWNER/MANAGER/MASTER)
    // ───────────────────────────────────────────────────────────
    @Transactional
    public OrderResponse changeStatus(UUID orderId, OrderStatusRequest req,
                                      String requesterId, String requesterRole) {

        OrderEntity order = findActiveOrder(orderId);

        if ("OWNER".equals(requesterRole)) {
            validateStoreOwner(order.getStore().getStoreId(), requesterId);
        }

        order.changeStatus(req.status());
        return OrderResponse.from(order);
    }

    // ───────────────────────────────────────────────────────────
    // 주문 취소 (CUSTOMER: 5분 이내 / MASTER)
    // ───────────────────────────────────────────────────────────
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String requesterId, String requesterRole) {
        OrderEntity order = findActiveOrder(orderId);

        if ("MASTER".equals(requesterRole)) {
            order.changeStatus(OrderStatus.CANCELED);
            return OrderResponse.from(order);
        }

        // CUSTOMER: 본인 주문인지 확인
        validateOwnership(order, requesterId);

        // 5분 이내 취소 가능 여부 확인
        if (LocalDateTime.now().isAfter(
                order.getCreatedAt().plusMinutes(CANCEL_LIMIT_MINUTES))) {
            throw  new IllegalStateException("주문 생성 후 5분이 경과하여 취소할 수 없습니다.");
        }

        order.changeStatus(OrderStatus.CANCELED);
        return OrderResponse.from(order);
    }

    // ───────────────────────────────────────────────────────────
    // 주문 삭제 (소프트, MASTER)
    // ───────────────────────────────────────────────────────────
    @Transactional
    public void  deleteOrder(UUID orderId, String masterUsername) {

        OrderEntity order = findActiveOrder(orderId);
        order.softDelete(masterUsername);
    }


    // 주문 존재 여부 확인
    private OrderEntity findActiveOrder(UUID orderId) {
        return orderRepository.findActiveById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 주문입니다."));
    }

    // 주문 접근 가능자 확인
    private void validateOwnership(OrderEntity order, String requesterId) {
        if (!order.getCustomer().getUsername().equals(requesterId)) {
            throw new SecurityException("본인의 주문만 접근할 수 있습니다.");
        }
    }

    // 가게 점주 확인
    private void validateStoreOwner(UUID storeId, String ownerId) {
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        //StoreEntity에 OwnerId 생기면 활성화할 예정
//        if (!store.getOwnerId.equals(ownerId)) {
//            throw new SecurityException("해당 가게의 주문에 접근할 권한이 없습니다.");
//        }
    }

    // 권한 확인
    private void validateReadAccess(OrderEntity order, String requesterId, String role) {
        if ("MANAGER".equals(role) || "MASTER".equals(role)) return;
        if ("CUSTOMER".equals(role)) {
            validateOwnership(order, requesterId);
            return;
        }
        if ("OWNER".equals(role)) {
            validateStoreOwner(order.getStore().getStoreId(), requesterId);
        }
    }
}