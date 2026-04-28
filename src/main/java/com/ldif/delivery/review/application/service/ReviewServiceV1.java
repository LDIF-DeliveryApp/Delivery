package com.ldif.delivery.review.application.service;

import com.ldif.delivery.global.exception.ErrorCode;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.order.domain.entity.OrderEntity;
import com.ldif.delivery.order.domain.entity.OrderStatus;
import com.ldif.delivery.order.domain.repository.OrderRepository;
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
import jakarta.persistence.LockTimeoutException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PessimisticLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j(topic = "ReviewService")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceV1 {

    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Retryable(
            retryFor = { PessimisticLockException.class, LockTimeoutException.class },
            maxAttempts = 5,
            backoff = @Backoff(
                    delay = 500,
                    multiplier = 2.0,
                    random = true,
                    maxDelay = 5000
            )
    )
    @Transactional
    public ResReviewDetailDto createReview(UUID orderId, @Valid ReqReviewDto reqReviewDto, UserDetailsImpl loginUser) {

        OrderEntity order = orderRepository.findActiveById(orderId)
                .orElseThrow(() -> new ReviewException(ErrorCode.ORDER_NOT_FOUND));

        UserEntity user = order.getCustomer();

        StoreEntity store = order.getStore();

        validateReviewAuthor(user.getUsername(), loginUser);

        Optional<ReviewEntity> checkReview = reviewRepository.findByOrder_OrderId(orderId);
        if (checkReview.isPresent()){
            throw new ReviewException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        OrderStatus orderStatus = order.getStatus();

        if(orderStatus != OrderStatus.COMPLETED) {
            throw new ReviewException(ErrorCode.ORDER_NOT_COMPLETED);
        }

        ReviewEntity review = new ReviewEntity(order, store, user, reqReviewDto.getRating(), reqReviewDto.getContent());
        ReviewEntity savedReview = reviewRepository.save(review);

        store.addReview(reqReviewDto.getRating());

        return new ResReviewDetailDto(savedReview);
    }

    @Recover
    public ResReviewDetailDto recover(PessimisticLockException e, UUID orderId, ReqReviewDto reqReviewDto, UserDetailsImpl loginUser) {
        log.error("평점 업데이트 최종 실패 - 주문ID: {}, 유저: {}, 에러: {}",
                orderId, loginUser.getUsername(), e.getMessage());

        throw new ReviewException(ErrorCode.REVIEW_REGISTRATION_FAILED);
    }


    public Page<ResReviewDto> getReviews(UUID storeId, Integer rating, Pageable pageable) {
        List<Integer> allowedSizes = List.of(10, 30, 50);
        int pageSize = pageable.getPageSize();

        if (!allowedSizes.contains(pageSize)) {
            pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }

        Page<ReviewEntity> reviewPage = reviewRepository.searchReviews(storeId, rating, pageable);

        return reviewPage.map(review -> {
            String summary = summarizeContent(review.getContent());

            return new ResReviewDto(review, summary);
        });
    }

    public ResReviewDetailDto getReviewDetail(UUID reviewId) {
        ReviewEntity review = reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)
                        .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));

        return new ResReviewDetailDto(review);
    }

    @Transactional
    public ResReviewDetailDto updateReview(UUID reviewId, ReqReviewDto reqReviewDto, UserDetailsImpl loginUser) {
        ReviewEntity review = reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));

        validateReviewAuthor(review.getUser().getUsername(), loginUser);

        if (reqReviewDto.getRating() != null) review.setRating(reqReviewDto.getRating());
        if (reqReviewDto.getContent() != null) review.setContent(reqReviewDto.getContent());

        return new ResReviewDetailDto(review);
    }


    @Transactional
    public void deleteReview(UUID reviewId, UserDetailsImpl loginUser) {
        ReviewEntity review = reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));

        validateReviewAuthor(review.getUser().getUsername(), loginUser);

        review.softDelete(loginUser.getUsername());
    }


    private String summarizeContent(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }

        String cleanContent = content.replaceAll("\\s+", " ").trim();

        return (cleanContent.length() > 10)
                ? cleanContent.substring(0, 10) + "... "
                : cleanContent;
    }

    private void validateReviewAuthor(String authorName, UserDetailsImpl loginUser){
        if(!loginUser.hasPermission(authorName)){
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }
}
