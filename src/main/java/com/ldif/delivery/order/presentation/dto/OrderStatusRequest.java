package com.ldif.delivery.order.presentation.dto;

import com.ldif.delivery.order.domain.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusRequest(

        @NotNull(message = "변경할 상태는 필수입니다.")
        OrderStatus status
) {}
