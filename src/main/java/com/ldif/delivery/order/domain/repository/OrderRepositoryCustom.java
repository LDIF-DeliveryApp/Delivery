package com.ldif.delivery.order.domain.repository;

import com.ldif.delivery.order.domain.entity.OrderEntity;
import com.ldif.delivery.order.domain.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderRepositoryCustom {

    Page<OrderEntity> searchOrders(
            String customerId,
            UUID storeId,
            OrderStatus status,
            Pageable pageable
    );
}
