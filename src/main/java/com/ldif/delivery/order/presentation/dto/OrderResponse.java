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
                    item.getMenu().getMenuId(),
                    item.getQuantity(),
                    item.getUnitPrice()
            );
        }
    }

    public static OrderResponse from(OrderEntity order) {

        return new OrderResponse(
                order.getOrderId(),
                order.getCustomer().getUsername(),
                order.getStore().getStoreId(),
                order.getAddressId(),
//                order.getAddress() != null ? order.getAddress().getAddressId() : null,
                order.getOrderType().name(),
                order.getStatus().name(),
                order.getTotalPrice(),
                order.getRequest(),
                order.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .toList(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}