package com.ldif.delivery.order.domain.repository;

import com.ldif.delivery.order.domain.entity.OrderEntity;
import com.ldif.delivery.order.domain.entity.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID>, OrderRepositoryCustom {

    // 소프트 삭제된 건 제외한 단건 조회 (getOrder)
    @Query("SELECT o FROM OrderEntity o WHERE o.orderId = :orderId AND o.deletedAt IS NULL")
    Optional<OrderEntity> findActiveById(@Param("orderId") UUID orderId);

    // 비관적 락 (changeStatus, cancelOrder, updateOrder, deleteOrder)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrderEntity o WHERE o.orderId = :orderId AND o.deletedAt IS NULL")
    Optional<OrderEntity> findActiveByIdWithLock(@Param("orderId") UUID orderId);

    // 중복 주문 방지 (createOrder)
    boolean existsByCustomer_UsernameAndStore_StoreIdAndStatus(String username, UUID storeId, OrderStatus status);
}