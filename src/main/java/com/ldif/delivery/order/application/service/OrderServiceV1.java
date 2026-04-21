package com.ldif.delivery.order.application.service;

import com.ldif.delivery.order.domain.entity.OrderEntity;
import com.ldif.delivery.order.domain.entity.OrderItemEntity;
import com.ldif.delivery.order.domain.entity.OrderStatus;
import com.ldif.delivery.order.domain.entity.OrderType;
import com.ldif.delivery.order.domain.repository.OrderItemRepository;
import com.ldif.delivery.order.domain.repository.OrderRepository;
import com.ldif.delivery.order.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceV1 {

    private  final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    // Menu와 Store Repository도 주입 필요 (작업 완료되는 대로 추가 예정)

    private static final int CANCEL_LIMIT_MINUTES = 5;

    // ───────────────────────────────────────────────────────────
    // 주문 생성 (CUSTOMER)
    // ───────────────────────────────────────────────────────────
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest req, String customerId) {
        // 1. OrderItem 생성 (단가는 메뉴 서비스에서 조회 / 임시로 0 처리 통합 후 교체 예정
        // Map<UUID, Integer> priceMap = menuPort.getPrices(menuIds);
        List<OrderItemEntity> items = req.items().stream()
                .map(i -> OrderItemEntity.of(i.menuId(), i.quantity(), 0))
                .toList();

        int totalPrice = items.stream()
                .mapToInt(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        OrderType orderType = OrderType.valueOf(req.orderType());

        OrderEntity order = OrderEntity.create(
                customerId,
                req.storeId(),
                req.addressId(),
                orderType,
                req.request(),
                items,
                totalPrice
        );

        return OrderResponse.from(orderRepository.save(order));
    }

    // ───────────────────────────────────────────────────────────
    // 주문 목록 조회
    // ───────────────────────────────────────────────────────────
    public PageResponse<OrderResponse> getOrder(
            String requesterId,
            String requesterRole,
            UUID storeId,
            OrderStatus status,
            Pageable pageable
    ) {

        String customerIdFilter = switch (requesterRole) {
            case "CUSTOMER" -> requesterId;
            case "OWNER"    -> null;
            default         -> null;
        };

        Page<OrderEntity> page = orderRepository.searchOrders(
                customerIdFilter, storeId, status, pageable);

        return PageResponse.from(page.map(OrderResponse::from));
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

        // OWNER: 본인 가게 주문인지 검증 (실제 구현 시 storePort.isOwner() 호출)
        if ("OWNER".equals(requesterRole)) {
            validateStoreOwnership(order, requesterId);
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

        // CUSTOMER: 본인 주문 + 5분 이내 검사
        validateOwnership(order, requesterId);

        LocalDateTime deadline = order.getCreatedAt().plusMinutes(CANCEL_LIMIT_MINUTES);
        if (LocalDateTime.now().isAfter(deadline)) {
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
        if (!order.getCustomerId().equals(requesterId)) {
            throw new SecurityException("본인의 주문만 접근할 수 있습니다.");
        }
    }

    // 권한 확인
    private void  validateReadAccess(OrderEntity order, String requesterId, String role) {
        if ("MANAGER".equals(role) || "MASTER".equals(role)) return;
        if ("CUSTOMER".equals(role)) {
            validateOwnership(order, requesterId);
            return;
        }
    }

    // 가게 점주 확인
    private void validateStoreOwnership(OrderEntity order, String ownerId) {
        // storePort.isOwnerOf(order.getStoreId(), ownerId) — 통합 시 구현
        // 임시 패스스루
    }
}
