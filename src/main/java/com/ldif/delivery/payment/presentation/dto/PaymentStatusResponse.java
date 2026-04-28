package com.ldif.delivery.payment.presentation.dto;

import com.ldif.delivery.payment.domain.entity.PaymentEntity;
import com.ldif.delivery.payment.domain.entity.PaymentStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentStatusResponse {

    private final UUID paymentId;
    private final PaymentStatus status;

    public PaymentStatusResponse(PaymentEntity payment) {
        this.paymentId = payment.getPaymentId();
        this.status = payment.getStatus();
    }
}