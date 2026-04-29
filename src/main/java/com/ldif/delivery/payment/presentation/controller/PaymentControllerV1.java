package com.ldif.delivery.payment.presentation.controller;

import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.payment.application.service.PaymentServiceV1;
import com.ldif.delivery.payment.presentation.dto.PaymentRequest;
import com.ldif.delivery.payment.presentation.dto.PaymentResponse;
import com.ldif.delivery.payment.presentation.dto.PaymentStatusResponse;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PaymentControllerV1 {

    private final PaymentServiceV1 paymentServiceV1;

    // 결제 목록 조회 - 관리자/매니저만
    @GetMapping("/payments")
    @Secured({
            UserRoleEnum.Authority.MASTER,
            UserRoleEnum.Authority.MANAGER
    })
    public ResponseEntity<CommonResponse<Page<PaymentResponse>>> getPayments(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @AuthenticationPrincipal UserDetailsImpl loginUser
    ) {
        Page<PaymentResponse> payments = paymentServiceV1.getPayments(status, page, size, sort, loginUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", payments));
    }

    // 결제 상세 조회 - 고객/관리자/매니저
    @GetMapping("/payments/{paymentId}")
    @Secured({
            UserRoleEnum.Authority.CUSTOMER,
            UserRoleEnum.Authority.MANAGER,
            UserRoleEnum.Authority.MASTER
    })
    public ResponseEntity<CommonResponse<PaymentResponse>> getPayment(
            @PathVariable UUID paymentId,
            @AuthenticationPrincipal UserDetailsImpl loginUser
    ) {
        PaymentResponse payment = paymentServiceV1.getPayment(paymentId, loginUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", payment));
    }

    // 결제 상태 조회 - 고객/관리자/매니저
    @GetMapping("/payments/{paymentId}/status")
    @Secured({
            UserRoleEnum.Authority.CUSTOMER,
            UserRoleEnum.Authority.MANAGER,
            UserRoleEnum.Authority.MASTER
    })
    public ResponseEntity<CommonResponse<PaymentStatusResponse>> getPaymentStatus(
            @PathVariable UUID paymentId,
            @AuthenticationPrincipal UserDetailsImpl loginUser
    ) {
        PaymentStatusResponse paymentStatus = paymentServiceV1.getPaymentStatus(paymentId, loginUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", paymentStatus));
    }

    // 결제 취소 - 고객/관리자/매니저
    @PatchMapping("/payments/{paymentId}/cancel")
    @Secured({
            UserRoleEnum.Authority.CUSTOMER,
            UserRoleEnum.Authority.MANAGER,
            UserRoleEnum.Authority.MASTER
    })
    public ResponseEntity<CommonResponse<Void>> cancelPayment(
            @PathVariable UUID paymentId,
            @AuthenticationPrincipal UserDetailsImpl loginUser
    ) {
        paymentServiceV1.cancelPayment(paymentId, loginUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", null));
    }

    // 결제 생성 - CUSTOMER(본인)
    @PostMapping("/orders/{orderId}/payments")
    @Secured(UserRoleEnum.Authority.CUSTOMER)
    public ResponseEntity<CommonResponse<PaymentResponse>> createPayment(
            @PathVariable UUID orderId,
            @Valid @RequestBody PaymentRequest req,
            @AuthenticationPrincipal UserDetailsImpl loginUser
    ) {
        PaymentResponse payment = paymentServiceV1.createPayment(orderId, req, loginUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(HttpStatus.CREATED.value(), "결제가 완료되었습니다.", payment));
    }
}