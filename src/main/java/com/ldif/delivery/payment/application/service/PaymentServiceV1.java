package com.ldif.delivery.payment.application.service;

import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.payment.domain.entity.PaymentEntity;
import com.ldif.delivery.payment.domain.entity.PaymentStatus;
import com.ldif.delivery.payment.domain.repository.PaymentRepository;
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
}