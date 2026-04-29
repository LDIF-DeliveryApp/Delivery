package com.ldif.delivery;

import com.ldif.delivery.area.domain.entity.AreaEntity;
import com.ldif.delivery.category.domain.entity.CategoryEntity;
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
import com.ldif.delivery.review.presentation.dto.ResReviewDetailDto;
import com.ldif.delivery.review.presentation.dto.ResReviewDto;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.store.domain.repository.StoreRepository;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewTest {

    @InjectMocks
    private ReviewServiceV1 reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    private UserDetailsImpl loginUser;
    private OrderEntity mockOrder;
    private UserEntity mockUser;
    private StoreEntity mockStore;
    private ReviewEntity mockReview;

    private final UUID orderId = UUID.randomUUID();
    private final UUID storeId = UUID.randomUUID();
    private final UUID reviewId = UUID.randomUUID();
    private final String username = "testUser";

    @BeforeEach
    void setUp() {
        loginUser = mock(UserDetailsImpl.class);
        lenient().when(loginUser.getUsername()).thenReturn(username);
        lenient().when(loginUser.hasPermission(username)).thenReturn(true);

        mockStore = mock(StoreEntity.class);
        lenient().when(mockStore.getStoreId()).thenReturn(storeId);
        lenient().when(mockStore.getName()).thenReturn("가게이름");

        mockUser = mock(UserEntity.class);
        lenient().when(mockUser.getUsername()).thenReturn(username);
        lenient().when(mockUser.getNickname()).thenReturn("닉네임");

        mockOrder = mock(OrderEntity.class);
        lenient().when(mockOrder.getCustomer()).thenReturn(mockUser);
        lenient().when(mockOrder.getStore()).thenReturn(mockStore);
        lenient().when(mockOrder.getStatus()).thenReturn(OrderStatus.COMPLETED);

        mockReview = mock(ReviewEntity.class);
        lenient().when(mockReview.getReviewId()).thenReturn(reviewId);
        lenient().when(mockReview.getUser()).thenReturn(mockUser);
        lenient().when(mockReview.getStore()).thenReturn(mockStore);
        lenient().when(mockReview.getContent()).thenReturn("맛있어요!");
        lenient().when(mockReview.getRating()).thenReturn(5);
        lenient().when(mockReview.getCreatedAt()).thenReturn(LocalDateTime.now());
    }

    @Nested
    @DisplayName("A. 리뷰 생성 (createReview)")
    class CreateReviewTest {

        @Test
        @DisplayName("성공: 모든 검증 통과 시 리뷰 정상 생성")
        void createReview_Success() {
            // Given
            ReqReviewDto dto = new ReqReviewDto(5, "맛있어요!");

            given(orderRepository.findActiveById(orderId)).willReturn(Optional.of(mockOrder));
            given(storeRepository.findByIdWithLock(any())).willReturn(Optional.of(mockStore));
            given(reviewRepository.findByOrder_OrderId(orderId)).willReturn(Optional.empty());

            // Mock 객체를 save 반환값으로 사용해야 DTO 변환 시 NPE 안 남
            given(reviewRepository.save(any(ReviewEntity.class))).willReturn(mockReview);

            // When
            ResReviewDetailDto result = reviewService.createReview(orderId, dto, loginUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getReviewId()).isEqualTo(reviewId);
            assertThat(result.getRating()).isEqualTo(5);
            assertThat(result.getContent()).isEqualTo("맛있어요!");
            assertThat(result.getStoreName()).isEqualTo("가게이름");
            verify(reviewRepository, times(1)).save(any(ReviewEntity.class));
            verify(mockStore, times(1)).addReview(dto.getRating());
        }

        @Test
        @DisplayName("실패: 존재하지 않거나 삭제된 주문")
        void createReview_Fail_OrderNotFound() {
            // Given
            ReqReviewDto dto = new ReqReviewDto(5, "맛있어요!");
            given(orderRepository.findActiveById(orderId)).willReturn(Optional.empty());

            // When & Then
            ReviewException e = assertThrows(ReviewException.class, () -> {
                reviewService.createReview(orderId, dto, loginUser);
            });
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 작성자 권한 불일치")
        void createReview_Fail_AccessDenied() {
            // Given
            ReqReviewDto dto = new ReqReviewDto(5, "맛있어요!");
            given(orderRepository.findActiveById(orderId)).willReturn(Optional.of(mockOrder));
            given(storeRepository.findByIdWithLock(any())).willReturn(Optional.of(mockStore));

            // 권한 없음 설정
            given(loginUser.hasPermission(username)).willReturn(false);

            // When & Then
            AccessDeniedException e = assertThrows(AccessDeniedException.class, () -> {
                reviewService.createReview(orderId, dto, loginUser);
            });
            assertThat(e.getMessage()).isEqualTo("접근 권한이 없습니다.");
        }

        @Test
        @DisplayName("실패: 이미 등록된 리뷰 존재")
        void createReview_Fail_AlreadyExists() {
            // Given
            ReqReviewDto dto = new ReqReviewDto(5, "맛있어요!");
            given(orderRepository.findActiveById(orderId)).willReturn(Optional.of(mockOrder));
            given(storeRepository.findByIdWithLock(any())).willReturn(Optional.of(mockStore));

            given(reviewRepository.findByOrder_OrderId(orderId)).willReturn(Optional.of(mockReview));

            // When & Then
            ReviewException e = assertThrows(ReviewException.class, () -> {
                reviewService.createReview(orderId, dto, loginUser);
            });
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("실패: 주문 상태가 배달 완료(COMPLETED)가 아님")
        void createReview_Fail_InvalidStatus() {
            // Given
            ReqReviewDto dto = new ReqReviewDto(5, "맛있어요!");
            given(orderRepository.findActiveById(orderId)).willReturn(Optional.of(mockOrder));
            given(storeRepository.findByIdWithLock(any())).willReturn(Optional.of(mockStore));
            given(reviewRepository.findByOrder_OrderId(orderId)).willReturn(Optional.empty());

            given(mockOrder.getStatus()).willReturn(OrderStatus.PENDING);

            // When & Then
            ReviewException e = assertThrows(ReviewException.class, () -> {
                reviewService.createReview(orderId, dto, loginUser);
            });
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_COMPLETED);
        }

        @Test
        @DisplayName("성공: 여러 개의 리뷰가 달릴 때 가게 평균 평점 및 리뷰 개수 계산 검증")
        void createReview_UpdateStoreAverageRating() {
            // Given: 실제 로직이 동작하는 StoreEntity 생성
            StoreEntity realStore = new StoreEntity(mockUser, mock(CategoryEntity.class), mock(AreaEntity.class), "계산테스트가게", "주소", "010-1111-2222");

            // 리뷰 저장은 전달받은 객체를 그대로 반환하도록 설정
            given(reviewRepository.save(any(ReviewEntity.class))).willAnswer(returnsFirstArg());
            given(reviewRepository.findByOrder_OrderId(any(UUID.class))).willReturn(Optional.empty());
            given(storeRepository.findByIdWithLock(any())).willReturn(Optional.of(realStore));

            // 1. 첫 번째 리뷰: 5점
            UUID orderId1 = UUID.randomUUID();
            OrderEntity order1 = mock(OrderEntity.class);
            given(order1.getStore()).willReturn(realStore);
            given(order1.getCustomer()).willReturn(mockUser);
            given(order1.getStatus()).willReturn(OrderStatus.COMPLETED);
            given(orderRepository.findActiveById(orderId1)).willReturn(Optional.of(order1));

            reviewService.createReview(orderId1, new ReqReviewDto(5, "최고예요"), loginUser);

            // 검증: 5.0점, 리뷰 1개
            assertThat(realStore.getAverageRating()).isEqualByComparingTo("5.0");
            assertThat(realStore.getReviewCount()).isEqualTo(1);

            // 2. 두 번째 리뷰: 4점
            UUID orderId2 = UUID.randomUUID();
            OrderEntity order2 = mock(OrderEntity.class);
            given(order2.getStore()).willReturn(realStore);
            given(order2.getCustomer()).willReturn(mockUser);
            given(order2.getStatus()).willReturn(OrderStatus.COMPLETED);
            given(orderRepository.findActiveById(orderId2)).willReturn(Optional.of(order2));

            reviewService.createReview(orderId2, new ReqReviewDto(4, "맛있어요"), loginUser);

            // 검증: (5+4)/2 = 4.5점, 리뷰 2개
            assertThat(realStore.getAverageRating()).isEqualByComparingTo("4.5");
            assertThat(realStore.getReviewCount()).isEqualTo(2);

            // 3. 세 번째 리뷰: 2점
            UUID orderId3 = UUID.randomUUID();
            OrderEntity order3 = mock(OrderEntity.class);
            given(order3.getStore()).willReturn(realStore);
            given(order3.getCustomer()).willReturn(mockUser);
            given(order3.getStatus()).willReturn(OrderStatus.COMPLETED);
            given(orderRepository.findActiveById(orderId3)).willReturn(Optional.of(order3));

            reviewService.createReview(orderId3, new ReqReviewDto(2, "그냥 그래요"), loginUser);

            // 검증: (5+4+2)/3 = 11/3 = 3.666... -> 반올림하여 3.7점, 리뷰 3개
            assertThat(realStore.getAverageRating()).isEqualByComparingTo("3.7");
            assertThat(realStore.getReviewCount()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("B. 리뷰 목록 조회 (getReviews)")
    class GetReviewsTest {

        @Test
        @DisplayName("성공: 허용된 페이징 사이즈(10, 30, 50)로 정상 조회")
        void getReviews_Success_AllowedSize() {
            // Given
            Pageable pageable = PageRequest.of(0, 30);
            Page<ReviewEntity> page = new PageImpl<>(List.of(mockReview));
            given(reviewRepository.searchReviews(storeId, 5, pageable)).willReturn(page);

            // When
            Page<ResReviewDto> result = reviewService.getReviews(storeId, 5, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("성공/경계값 검증: 허용되지 않은 사이즈 시 10으로 고정되어 조회")
        void getReviews_Success_UnallowedSize() {
            // Given
            Pageable pageable = PageRequest.of(0, 20); // 허용되지 않은 사이즈
            Page<ReviewEntity> page = new PageImpl<>(List.of(mockReview));

            // 변경된 Pageable(size = 10) 인자로 받는지 검증
            given(reviewRepository.searchReviews(eq(storeId), eq(5), argThat(p -> p.getPageSize() == 10))).willReturn(page);

            // When
            Page<ResReviewDto> result = reviewService.getReviews(storeId, 5, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            verify(reviewRepository).searchReviews(eq(storeId), eq(5), argThat(p -> p.getPageSize() == 10));
        }

        @Test
        @DisplayName("성공/로직 검증: 본문 10자 초과 시 말줄임표 처리 확인")
        void getReviews_Success_SummaryContent() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            ReviewEntity longReview = mock(ReviewEntity.class);
            given(longReview.getReviewId()).willReturn(reviewId);
            given(longReview.getUser()).willReturn(mockUser);
            given(longReview.getStore()).willReturn(mockStore);
            given(longReview.getRating()).willReturn(5);
            given(longReview.getCreatedAt()).willReturn(LocalDateTime.now());
            // 10자 초과 내용 설정
            given(longReview.getContent()).willReturn("이 리뷰는 10자가 넘어가서 말줄임표가 붙어야 합니다.");

            Page<ReviewEntity> page = new PageImpl<>(List.of(longReview));
            given(reviewRepository.searchReviews(storeId, 5, pageable)).willReturn(page);

            // When
            Page<ResReviewDto> result = reviewService.getReviews(storeId, 5, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            // 요약된 결과를 리플렉션 등으로 직접 조회할 수 없지만, 로직 상 10자가 넘으면 잘리는지 확인할 수 있습니다.
            assertThat(result.getContent().get(0).getSummary()).isEqualTo("이 리뷰는 10자가... ");
        }
    }

    @Nested
    @DisplayName("C. 리뷰 단건 조회 (getReviewDetail)")
    class GetReviewDetailTest {

        @Test
        @DisplayName("성공: 삭제되지 않은 유효한 리뷰 단건 조회")
        void getReviewDetail_Success() {
            // Given
            given(reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.of(mockReview));

            // When
            ResReviewDetailDto result = reviewService.getReviewDetail(reviewId);

            // Then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("실패: 존재하지 않거나 삭제된 리뷰 아이디")
        void getReviewDetail_Fail_NotFound() {
            // Given
            given(reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.empty());

            // When & Then
            ReviewException e = assertThrows(ReviewException.class, () -> {
                reviewService.getReviewDetail(reviewId);
            });
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("D. 리뷰 수정 (updateReview)")
    class UpdateReviewTest {

        @Test
        @DisplayName("성공: 작성자 권한 있는 사용자가 평점과 내용 정상 수정")
        void updateReview_Success() {
            // Given
            ReqReviewDto dto = new ReqReviewDto(4, "수정된 내용");
            given(reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.of(mockReview));

            // When
            ResReviewDetailDto result = reviewService.updateReview(reviewId, dto, loginUser);

            // Then
            assertThat(result).isNotNull();
            verify(mockReview).setRating(4);
            verify(mockReview).setContent("수정된 내용");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 리뷰 수정 요청")
        void updateReview_Fail_NotFound() {
            // Given
            ReqReviewDto dto = new ReqReviewDto(4, "수정된 내용");
            given(reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.empty());

            // When & Then
            ReviewException e = assertThrows(ReviewException.class, () -> {
                reviewService.updateReview(reviewId, dto, loginUser);
            });
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 수정 요청자와 리뷰 작성자가 다름")
        void updateReview_Fail_AccessDenied() {
            // Given
            ReqReviewDto dto = new ReqReviewDto(4, "수정된 내용");
            given(reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.of(mockReview));
            given(loginUser.hasPermission(username)).willReturn(false);

            // When & Then
            AccessDeniedException e = assertThrows(AccessDeniedException.class, () -> {
                reviewService.updateReview(reviewId, dto, loginUser);
            });
            assertThat(e.getMessage()).isEqualTo("접근 권한이 없습니다.");
        }
    }

    @Nested
    @DisplayName("E. 리뷰 삭제 (deleteReview)")
    class DeleteReviewTest {

        @Test
        @DisplayName("성공: 작성자 권한 있는 사용자가 리뷰 삭제 요청 (Soft Delete)")
        void deleteReview_Success() {
            // Given
            given(reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.of(mockReview));

            // When
            reviewService.deleteReview(reviewId, loginUser);

            // Then
            verify(mockReview).softDelete(username);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 리뷰 삭제 요청")
        void deleteReview_Fail_NotFound() {
            // Given
            given(reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.empty());

            // When & Then
            ReviewException e = assertThrows(ReviewException.class, () -> {
                reviewService.deleteReview(reviewId, loginUser);
            });
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.REVIEW_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 삭제 요청자와 리뷰 작성자가 다름")
        void deleteReview_Fail_AccessDenied() {
            // Given
            given(reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.of(mockReview));
            given(loginUser.hasPermission(username)).willReturn(false);

            // When & Then
            AccessDeniedException e = assertThrows(AccessDeniedException.class, () -> {
                reviewService.deleteReview(reviewId, loginUser);
            });
            assertThat(e.getMessage()).isEqualTo("접근 권한이 없습니다.");
        }
    }
}
