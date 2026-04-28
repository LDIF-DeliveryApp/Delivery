package com.ldif.delivery.payment.application.service;

import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.payment.domain.entity.PaymentEntity;
import com.ldif.delivery.payment.domain.repository.PaymentRepository;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import com.ldif.delivery.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceV1Test {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceV1 paymentService;

    private UserDetailsImpl mockLoginUser() {
        return mock(UserDetailsImpl.class);
    }

    @Test
    @DisplayName("결제 조회 실패 - 결제를 찾을 수 없음")
    void getPayment_fails_whenPaymentNotFound() {
        // given
        UUID paymentId = UUID.randomUUID();
        UserDetailsImpl loginUser = mockLoginUser();

        given(paymentRepository.findById(paymentId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPayment(paymentId, loginUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("결제 상태 조회 실패 - 결제를 찾을 수 없음")
    void getPaymentStatus_fails_whenPaymentNotFound() {
        // given
        UUID paymentId = UUID.randomUUID();
        UserDetailsImpl loginUser = mockLoginUser();

        given(paymentRepository.findById(paymentId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPaymentStatus(paymentId, loginUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("결제 취소 실패 - 결제를 찾을 수 없음")
    void cancelPayment_fails_whenPaymentNotFound() {
        // given
        UUID paymentId = UUID.randomUUID();
        UserDetailsImpl loginUser = mockLoginUser();

        given(paymentRepository.findById(paymentId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(paymentId, loginUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("결제 취소 성공 - 결제 대기 상태")
    void cancelPayment_succeeds_whenPaymentIsPending() {
        // given
        UUID paymentId = UUID.randomUUID();
        UserDetailsImpl loginUser = mockLoginUser();
        PaymentEntity payment = mock(PaymentEntity.class);

        given(paymentRepository.findById(paymentId))
                .willReturn(Optional.of(payment));

        given(payment.getDeletedAt()).willReturn(null);
        given(payment.isCancelled()).willReturn(false);
        given(payment.isCompleted()).willReturn(false);

        // when
        paymentService.cancelPayment(paymentId, loginUser);

        // then
        verify(payment).cancel();
    }

    @Test
    @DisplayName("결제 취소 성공 - 결제 완료 후 5분 이내")
    void cancelPayment_succeeds_whenCompletedWithinFiveMinutes() {
        // given
        UUID paymentId = UUID.randomUUID();
        UserDetailsImpl loginUser = mockLoginUser();
        PaymentEntity payment = mock(PaymentEntity.class);

        given(paymentRepository.findById(paymentId))
                .willReturn(Optional.of(payment));

        given(payment.getDeletedAt()).willReturn(null);
        given(payment.isCancelled()).willReturn(false);
        given(payment.isCompleted()).willReturn(true);
        given(payment.getCreatedAt()).willReturn(LocalDateTime.now().minusMinutes(3));

        // when
        paymentService.cancelPayment(paymentId, loginUser);

        // then
        verify(payment).cancel();
    }

    @Test
    @DisplayName("결제 취소 실패 - 결제 완료 후 5분 초과")
    void cancelPayment_fails_whenCompletedAfterFiveMinutes() {
        // given
        UUID paymentId = UUID.randomUUID();
        UserDetailsImpl loginUser = mockLoginUser();
        PaymentEntity payment = mock(PaymentEntity.class);

        given(paymentRepository.findById(paymentId))
                .willReturn(Optional.of(payment));

        given(payment.getDeletedAt()).willReturn(null);
        given(payment.isCancelled()).willReturn(false);
        given(payment.isCompleted()).willReturn(true);
        given(payment.getCreatedAt()).willReturn(LocalDateTime.now().minusMinutes(6));

        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(paymentId, loginUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("결제 후 5분이 지나 취소할 수 없습니다.");

        verify(payment, never()).cancel();
    }

    @Test
    @DisplayName("결제 취소 실패 - 이미 취소된 결제")
    void cancelPayment_fails_whenAlreadyCancelled() {
        // given
        UUID paymentId = UUID.randomUUID();
        UserDetailsImpl loginUser = mockLoginUser();
        PaymentEntity payment = mock(PaymentEntity.class);

        given(paymentRepository.findById(paymentId))
                .willReturn(Optional.of(payment));

        given(payment.getDeletedAt()).willReturn(null);
        given(payment.isCancelled()).willReturn(true);

        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(paymentId, loginUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 취소된 결제입니다.");

        verify(payment, never()).cancel();
    }
}