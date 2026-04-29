package com.ldif.delivery.payment.application.service;

import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.order.domain.entity.OrderEntity;
import com.ldif.delivery.order.domain.entity.OrderStatus;
import com.ldif.delivery.order.domain.repository.OrderRepository;
import com.ldif.delivery.payment.domain.entity.PaymentEntity;
import com.ldif.delivery.payment.domain.entity.PaymentStatus;
import com.ldif.delivery.payment.domain.repository.PaymentRepository;
import com.ldif.delivery.payment.presentation.dto.PaymentRequest;
import com.ldif.delivery.payment.presentation.dto.PaymentResponse;
import com.ldif.delivery.payment.presentation.dto.PaymentStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceV1 {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPayments(String status, int page, int size, String sort, UserDetailsImpl loginUser) {

        Sort.Direction direction = sort.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        List<Integer> allowedSize = Arrays.asList(10, 30, 50);
        int setSize = allowedSize.contains(size) ? size : 10;

        Pageable pageable = PageRequest.of(page, setSize, Sort.by(direction, "createdAt"));

        Page<PaymentEntity> payments;

        if (status == null || status.isBlank()) {
            payments = paymentRepository.findAllByDeletedAtIsNull(pageable);
        } else {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            payments = paymentRepository.findByStatusAndDeletedAtIsNull(paymentStatus, pageable);
        }

        return payments.map(PaymentResponse::new);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId, UserDetailsImpl loginUser) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        if (payment.getDeletedAt() != null) {
            throw new IllegalStateException("삭제된 결제입니다.");
        }

        return new PaymentResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentStatusResponse getPaymentStatus(UUID paymentId, UserDetailsImpl loginUser) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        if (payment.getDeletedAt() != null) {
            throw new IllegalStateException("삭제된 결제입니다.");
        }

        return new PaymentStatusResponse(payment);
    }

    @Transactional
    public void cancelPayment(UUID paymentId, UserDetailsImpl loginUser) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        if (payment.getDeletedAt() != null) {
            throw new IllegalStateException("삭제된 결제입니다.");
        }

        if (payment.isCancelled()) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }

        if (payment.isCompleted()
                && payment.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("결제 후 5분이 지나 취소할 수 없습니다.");
        }

        payment.cancel();
    }

    // ───────────────────────────────────────────────────────────
    // 결제 생성 (CUSTOMER 본인)
    // ───────────────────────────────────────────────────────────
    @Transactional
    public PaymentResponse createPayment(UUID orderId,
                                         PaymentRequest req,
                                         UserDetailsImpl loginUser) {

        // 1. 주문 조회 (소프트 삭제 제외)
        OrderEntity order = orderRepository.findActiveById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 2. 본인 주문 검증
        if (!order.getCustomer().getUsername().equals(loginUser.getUsername())) {
            throw new SecurityException("본인의 주문만 결제할 수 있습니다.");
        }

        // 3. PENDING 상태만 결제 가능
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태의 주문만 결제할 수 있습니다.");
        }

        // 4. 중복 결제 검증
        boolean alreadyPaid = paymentRepository.existsByOrder_OrderId(orderId);
        if (alreadyPaid) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }

        // 5. 결제 금액 검증
        if (!req.getAmount().equals(order.getTotalPrice())) {
            throw new IllegalStateException(
                    "결제 금액이 주문 금액과 일치하지 않습니다. " +
                            "주문 금액: " + order.getTotalPrice() +
                            ", 결제 금액: " + req.getAmount());
        }

        // 6. 결제 생성 — PaymentEntity 생성자 (OrderEntity, Integer)
        PaymentEntity payment = new PaymentEntity(order, req.getAmount());

        // 7. 결제 저장
        PaymentEntity savedPayment = paymentRepository.save(payment);

        // 8. 결제 완료 처리 및 주문 상태 ACCEPTED로 변경
        savedPayment.complete();
        order.changeStatus(OrderStatus.ACCEPTED);

        return new PaymentResponse(savedPayment);
    }
}