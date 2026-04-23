package com.ldif.delivery.order.presentation.dto;

import com.ldif.delivery.order.domain.entity.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record OrderCreateRequest(
        @NotNull(message = "가게 ID는 필수입니다.")
        UUID storeId,

        UUID addressId,

        @NotNull(message = "주문 유형은 필수입니다.")
        String orderType,

        @Size(max = 500, message = "요청사항은 500자 이내여야 합니다.")
        String request,

        @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다.")
        @Valid
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotNull(message = "메뉴 ID는 필수입니다.")
            UUID menuId,

            @NotNull(message = "주문 수량은 필수입니다.")
            @Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다.")
            Integer quantity
    ){}
}
