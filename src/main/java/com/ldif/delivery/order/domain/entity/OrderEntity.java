package com.ldif.delivery.order.domain.entity;

import com.ldif.delivery.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    // FK → p_user.username
    @Column(name = "customer_id", nullable = false, length = 50)    //명세서 10 -> 50
    private String customerId;

    // FK → p_store.store_id
    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    // FK → p_address.address_id
    @Column(name = "address_id")
    private UUID addressId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 20)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "request", columnDefinition = "TEXT")
    private String request;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    // ───────────────────────────────────────────────────────────
    // 비즈니스 메서드
    // ───────────────────────────────────────────────────────────

    public static OrderEntity create(
            String customerId, UUID storeId, UUID addressId,
            OrderType orderType, String request,
            List<OrderItemEntity> items, Integer totalPrice
    ) {
        OrderEntity order = OrderEntity.builder()
                .customerId(customerId)
                .storeId(storeId)
                .addressId(addressId)
                .orderType(orderType)
                .status(OrderStatus.PENDING)
                .request(request)
                .totalPrice(totalPrice)
                .build();
        items.forEach(item -> item.assignOrder(order));
        order.orderItems.addAll(items);
        return order;
    }

    public void updateRequest(String request) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("PENDING(접수 대기) 상태일 때만 수정 가능합니다.");
        }
        this.request = request;
    }

    public void updateRequestByMaster(String request) {
        this.request = request;
    }

    public void changeStatus(OrderStatus nextStatus) {
        if (!this.status.canTransitionTo(nextStatus)) {
            throw new IllegalStateException( "'" + this.status + "' 상태에서 '" + nextStatus + "'로 변경할 수 없습니다.");
        }
        this.status = nextStatus;
    }
}