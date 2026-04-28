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

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20, nullable = false)
    private PaymentMethod paymentMethod = PaymentMethod.CARD;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    public PaymentEntity(OrderEntity order, Integer amount) {
        if (order == null) {
            throw new IllegalArgumentException("주문 정보는 필수입니다.");
        }

        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("결제 금액은 0 이상이어야 합니다.");
        }

        this.order = order;
        this.amount = amount;
        this.paymentMethod = PaymentMethod.CARD;
        this.status = PaymentStatus.PENDING;
    }

    public void complete() {
        if (this.status == PaymentStatus.CANCELLED) {
            throw new IllegalStateException("취소된 결제는 완료 처리할 수 없습니다.");
        }

        if (this.status == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 결제입니다.");
        }

        this.status = PaymentStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status == PaymentStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }

        this.status = PaymentStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return this.status == PaymentStatus.CANCELLED;
    }

    public UUID getOrderId() {
        return this.order.getOrderId();
    }
}