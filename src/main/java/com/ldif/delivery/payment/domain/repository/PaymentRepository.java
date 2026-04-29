package com.ldif.delivery.payment.domain.repository;

import com.ldif.delivery.payment.domain.entity.PaymentEntity;
import com.ldif.delivery.payment.domain.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    Page<PaymentEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Page<PaymentEntity> findByStatusAndDeletedAtIsNull(PaymentStatus status, Pageable pageable);

    //  중복 결제 검증
    boolean existsByOrder_OrderId(UUID orderId);
}
