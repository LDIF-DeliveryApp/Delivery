package com.ldif.delivery.order.domain.entity;

import com.ldif.delivery.menu.domain.entity.MenuEntity;
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

    // FK → p_order.order_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    // FK → p_menu.menu_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuEntity menu;

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
    public  static OrderItemEntity of(MenuEntity menu, Integer quantity, Integer unitPrice) {
        return OrderItemEntity.builder()
                .menu(menu)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .build();
    }

    // OrderEntity.create()에서 양방향 관계 설정 시 호출
    void assignOrder(OrderEntity order) {
        this.order = order;
    }
}