package com.ldif.delivery.payment.domain.entity;

import com.ldif.delivery.global.infrastructure.entity.BaseEntity;
import com.ldif.delivery.order.domain.entity.OrderEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", updatable = false, nullable = false)
    private UUID paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private OrderEntity order;

    @Column(name = "payment_method", length = 20, nullable = false)
    private String paymentMethod = "CARD";

    @Column(name = "status", length = 20, nullable = false)
    private String status = "PENDING";

    @Column(name = "amount", nullable = false)
    private Integer amount;

    public PaymentEntity(OrderEntity order, Integer amount) {
        this.order = order;
        this.amount = amount;
        this.paymentMethod = "CARD";
        this.status = "PENDING";
    }

    public void complete() {
        this.status = "COMPLETED";
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(this.status);
    }

    public UUID getOrderId() {
        return order.getOrderId();
    }
}