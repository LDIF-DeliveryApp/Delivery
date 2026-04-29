package com.ldif.delivery.review;

import com.ldif.delivery.global.exception.ErrorCode;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.order.domain.entity.OrderEntity;
import com.ldif.delivery.order.domain.entity.OrderStatus;
import com.ldif.delivery.order.domain.repository.OrderRepository;
import com.ldif.delivery.review.application.service.ReviewServiceV1;
import com.ldif.delivery.review.domain.entity.ReviewEntity;
import com.ldif.delivery.review.domain.respository.ReviewRepository;
import com.ldif.delivery.review.exception.ReviewException;
import com.ldif.delivery.review.presentation.dto.ReqReviewDto;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.store.domain.repository.StoreRepository;
import com.ldif.delivery.user.domain.entity.UserEntity;
import org.hibernate.PessimisticLockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewConcurrencyTest {

    @InjectMocks
    private ReviewServiceV1 reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreRepository storeRepository;

    @Test
    @DisplayName("T2: 복구 로직 검증 - PessimisticLockException 발생 시 recover 메소드 작동 확인")
    void recover_LogicTest() {
        // Given
        UUID orderId = UUID.randomUUID();
        ReqReviewDto dto = new ReqReviewDto(5, "테스트");
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);
        PessimisticLockException exception = new PessimisticLockException("Lock Fail", null, "");

        // When & Then
        ReviewException result = assertThrows(ReviewException.class, () -> {
            reviewService.recover(exception, orderId, dto, loginUser);
        });

        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.REVIEW_REGISTRATION_FAILED);
    }

    @Test
    @DisplayName("T3: 동시성 로직 테스트 - StoreEntity의 평점 업데이트 원자성 검증")
    void concurrency_AtomicUpdateRating() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        UserEntity owner = mock(UserEntity.class);
        StoreEntity store = new StoreEntity(owner, null, null, "가게", "주소", "010");
        
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    synchronized (store) {
                        store.addReview(5);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        
        assertThat(store.getReviewCount()).isEqualTo(threadCount);
        assertThat(store.getAverageRating()).isEqualByComparingTo("5.0");
    }

    @Test
    @DisplayName("T1: 로직 검증 - findByIdWithLock 호출 및 결과 매핑 확인")
    void findByIdWithLock_CallTest() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        ReqReviewDto dto = new ReqReviewDto(5, "테스트");
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);
        
        OrderEntity mockOrder = mock(OrderEntity.class);
        StoreEntity mockStore = mock(StoreEntity.class);
        UserEntity mockUser = mock(UserEntity.class);
        ReviewEntity mockReview = mock(ReviewEntity.class);
        
        given(orderRepository.findActiveById(orderId)).willReturn(Optional.of(mockOrder));
        given(mockOrder.getCustomer()).willReturn(mockUser);
        given(mockOrder.getStore()).willReturn(mockStore);
        given(mockOrder.getStatus()).willReturn(OrderStatus.COMPLETED);
        
        given(mockStore.getStoreId()).willReturn(storeId);
        given(mockStore.getName()).willReturn("가게이름");
        
        given(mockUser.getUsername()).willReturn("testUser");
        given(mockUser.getNickname()).willReturn("닉네임");
        
        given(loginUser.hasPermission(anyString())).willReturn(true);
        
        given(storeRepository.findByIdWithLock(storeId)).willReturn(Optional.of(mockStore));
        
        given(reviewRepository.save(any())).willReturn(mockReview);
        given(mockReview.getReviewId()).willReturn(UUID.randomUUID());
        given(mockReview.getStore()).willReturn(mockStore);
        given(mockReview.getUser()).willReturn(mockUser);
        given(mockReview.getRating()).willReturn(5);
        given(mockReview.getCreatedAt()).willReturn(LocalDateTime.now());

        // When
        reviewService.createReview(orderId, dto, loginUser);

        // Then
        verify(storeRepository, times(1)).findByIdWithLock(storeId);
        verify(reviewRepository, times(1)).save(any());
        verify(mockStore, times(1)).addReview(5);
    }
}
