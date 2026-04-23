package com.ldif.delivery.order.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_order_item")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_item_id", updatable = false, nullable = false)
    private UUID orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "menu_id", nullable = false)
    private UUID menuId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;


    // ───────────────────────────────────────────────────────────
    // 생성 팩토리
    // ───────────────────────────────────────────────────────────
    public  static OrderItemEntity of(UUID menuId, Integer quantity, Integer unitPrice) {
        return OrderItemEntity.builder()
                .menuId(menuId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .build();
    }

    // OrderEntity.create()에서 양방향 관계 설정 시 호출
    void assignOrder(OrderEntity order) {
        this.order = order;
    }
}
