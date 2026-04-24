package com.ldif.delivery.payment.application.service;

import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.payment.domain.entity.PaymentEntity;
import com.ldif.delivery.payment.domain.repository.PaymentRepository;
import com.ldif.delivery.payment.presentation.dto.PaymentResponse;
import com.ldif.delivery.payment.presentation.dto.PaymentStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceV1 {

    private final PaymentRepository paymentRepository;

    // 결제 목록 조회
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPayments() {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getDeletedAt() == null)
                .map(PaymentResponse::new)
                .toList();
    }

    // 결제 상세 조회
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId, UserDetailsImpl loginUser) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        if (payment.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 결제입니다.");
        }

        return new PaymentResponse(payment);
    }

    // 결제 상태 조회
    @Transactional(readOnly = true)
    public PaymentStatusResponse getPaymentStatus(UUID paymentId, UserDetailsImpl loginUser) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        if (payment.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 결제입니다.");
        }

        return new PaymentStatusResponse(payment);
    }

    // 결제 취소
    @Transactional
    public void cancelPayment(UUID paymentId, UserDetailsImpl loginUser) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        if (payment.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 결제입니다.");
        }

        if ("CANCELLED".equals(payment.getStatus())) {
            throw new IllegalArgumentException("이미 취소된 결제입니다.");
        }

        if ("COMPLETED".equals(payment.getStatus())) {
            throw new IllegalArgumentException("완료된 결제는 취소할 수 없습니다.");
        }

        payment.cancel();
    }
}