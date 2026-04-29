package com.ldif.delivery.payment.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequest {

    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 0원보다 커야 합니다.")
    private Integer amount;

    @NotBlank(message = "결제 수단은 필수입니다.")
    private String paymentMethod;
}
