package com.ldif.delivery.order.presentation.dto;

import com.ldif.delivery.order.domain.entity.OrderEntity;
import com.ldif.delivery.order.domain.entity.OrderItemEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        String customerId,
        UUID storeId,
        UUID addressId,
        String orderType,
        String status,
        Integer totalPrice,
        String request,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record OrderItemResponse(
            UUID orderItemId,
            UUID menuId,
            Integer quantity,
            Integer unitPrice
    ) {
        public static OrderItemResponse from(OrderItemEntity item) {
            return new OrderItemResponse(
                    item.getOrderItemId(),
                    item.getMenuId(),
                    item.getQuantity(),
                    item.getUnitPrice()
            );
        }
    }

    public static OrderResponse from(OrderEntity order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        return new OrderResponse(
                order.getOrderId(),
                order.getCustomerId(),
                order.getStoreId(),
                order.getAddressId(),
                order.getOrderType().name(),
                order.getStatus().name(),
                order.getTotalPrice(),
                order.getRequest(),
                itemResponses,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}