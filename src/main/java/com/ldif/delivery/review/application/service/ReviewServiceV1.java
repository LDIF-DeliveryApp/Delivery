package com.ldif.delivery.review.application.service;

import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.review.domain.entity.ReviewEntity;
import com.ldif.delivery.review.domain.respository.ReviewRepository;
import com.ldif.delivery.review.presentation.dto.ReqReviewDto;
import com.ldif.delivery.review.presentation.dto.ResReviewDetailDto;
import com.ldif.delivery.review.presentation.dto.ResReviewDto;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.store.domain.repository.StoreRepository;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j(topic = "ReviewService")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceV1 {

    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
//    private final OrderRepository orderRepository;

    @Transactional
    public ResReviewDetailDto createReview(UUID orderId, @Valid ReqReviewDto reqReviewDto, UserDetailsImpl loginUser) {


//        OrderEntity order = orderRepository.findByOrderIdAndDeletedAtIsNull(orderId)
//                .orElseThrow(() -> new IllegalArgumentException("주문 없음." + orderId));
//
//
//        validateReviewAuthor(order.getUser().getUsername(), loginUser);

        UserEntity user = userRepository.findByUsername(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

//        StoreEntity store = storeRepository.findByStoreId((order.getStoreId())
//                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        //테스트용
        UUID storeId = UUID.fromString("550e8400-e29b-41d4-a716-446655440020");

                StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        ReviewEntity review = new ReviewEntity(store, user, reqReviewDto.getRating(), reqReviewDto.getContent());

        return new ResReviewDetailDto(review);
    }


    public Page<ResReviewDto> getReviews(UUID storeId, Integer rating, Pageable pageable) {
        // 1. 허용된 사이즈 리스트 (10, 30, 50)
        List<Integer> allowedSizes = List.of(10, 30, 50);
        int pageSize = pageable.getPageSize();

        // 2. 허용되지 않은 사이즈면 기본값 10으로 세팅한 새로운 PageRequest 생성
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
                        .orElseThrow(() -> new IllegalArgumentException("리뷰 없음." + reviewId));

        return new ResReviewDetailDto(review);
    }

    @Transactional
    public ResReviewDetailDto updateReview(UUID reviewId, ReqReviewDto reqReviewDto, UserDetailsImpl loginUser) {
        ReviewEntity review = reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 없음." + reviewId));

        validateReviewAuthor(review.getUser().getUsername(), loginUser);


        if (reqReviewDto.getRating() != null) review.setRating(reqReviewDto.getRating());
        if (reqReviewDto.getContent() != null) review.setContent(reqReviewDto.getContent());




        return new ResReviewDetailDto(review);
    }


    @Transactional
    public void deleteReview(UUID reviewId, UserDetailsImpl loginUser) {
        ReviewEntity review = reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 없음." + reviewId));

        validateReviewAuthor(review.getUser().getUsername(), loginUser);

        review.softDelete(loginUser.getUsername());

        return;
    }


    /**
     * 리뷰 내용을 10자로 요약하는 공통 함수
     */
    private String summarizeContent(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }

        // 줄바꿈이나 연속된 공백을 하나로 합쳐서 목록을 깔끔하게 (선택 사항)
        String cleanContent = content.replaceAll("\\s+", " ").trim();

        return (cleanContent.length() > 10)
                ? cleanContent.substring(0, 10) + "..."
                : cleanContent;
    }

    private void validateReviewAuthor(String authorName, UserDetailsImpl loginUser){
        if(!loginUser.hasPermission(authorName)){
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }
}
