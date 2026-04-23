package com.ldif.delivery.order.domain.repository;

import com.ldif.delivery.order.domain.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {

    List<OrderItemEntity> findAllByOrder_OrderId(UUID orderId);
}
