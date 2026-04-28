package com.ldif.delivery.payment.presentation.dto;

import com.ldif.delivery.payment.domain.entity.PaymentEntity;
import com.ldif.delivery.payment.domain.entity.PaymentMethod;
import com.ldif.delivery.payment.domain.entity.PaymentStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PaymentResponse {

    private final UUID paymentId;
    private final UUID orderId;
    private final PaymentMethod paymentMethod;
    private final PaymentStatus status;
    private final Integer amount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PaymentResponse(PaymentEntity payment) {
        this.paymentId = payment.getPaymentId();
        this.orderId = payment.getOrderId();
        this.paymentMethod = payment.getPaymentMethod();
        this.status = payment.getStatus();
        this.amount = payment.getAmount();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();
    }
}