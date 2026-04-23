package com.ldif.delivery.review.presentation.controller;

import com.google.genai.Common;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.global.infrastructure.presentation.dto.PageResponseDto;
import com.ldif.delivery.review.application.service.ReviewServiceV1;
import com.ldif.delivery.review.presentation.dto.ResReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewControllerV1 {

    private final ReviewServiceV1 reviewService;

    @GetMapping("/reviews")
    public ResponseEntity<CommonResponse<PageResponseDto<ResReviewDto>>> getReviews(
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) Integer rating,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {

        Page<ResReviewDto> reviewPage = reviewService.getReviews(storeId, rating, pageable);

        PageResponseDto<ResReviewDto> data = new PageResponseDto<>(reviewPage);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", data));
    }

}
