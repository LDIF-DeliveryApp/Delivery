package com.ldif.delivery.review.application.service;

import com.ldif.delivery.review.domain.entity.ReviewEntity;
import com.ldif.delivery.review.domain.respository.ReviewRepository;
import com.ldif.delivery.review.presentation.dto.ResReviewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
}
