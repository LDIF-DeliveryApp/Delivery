package com.ldif.delivery.review.presentation.controller;

import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.global.infrastructure.presentation.dto.PageResponseDto;
import com.ldif.delivery.review.application.service.ReviewServiceV1;
import com.ldif.delivery.review.presentation.dto.ReqReviewDto;
import com.ldif.delivery.review.presentation.dto.ResReviewDetailDto;
import com.ldif.delivery.review.presentation.dto.ResReviewDto;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewControllerV1 {

    private final ReviewServiceV1 reviewService;

    @PostMapping("/orders/{orderId}/reviews")
    public ResponseEntity<CommonResponse<ResReviewDetailDto>> createReview(
            @PathVariable UUID orderId,
            @Valid @RequestBody ReqReviewDto reqReviewDto,
            @AuthenticationPrincipal UserDetailsImpl loginUser) {

        ResReviewDetailDto createReview = reviewService.createReview(orderId, reqReviewDto, loginUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", createReview));

    }

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

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonResponse<ResReviewDetailDto>> getReviewDetail(@PathVariable UUID reviewId){

        ResReviewDetailDto reviewDetail = reviewService.getReviewDetail(reviewId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", reviewDetail));

    }

    @PutMapping("/reviews/{reviewId}")
    @Secured(UserRoleEnum.Authority.CUSTOMER)
    public ResponseEntity<CommonResponse<ResReviewDetailDto>> updateReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReqReviewDto reqReviewDto,
            @AuthenticationPrincipal UserDetailsImpl loginUser){

        ResReviewDetailDto updateReview = reviewService.updateReview(reviewId, reqReviewDto, loginUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", updateReview));

    }

    @DeleteMapping("/reviews/{reviewId}")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER, UserRoleEnum.Authority.CUSTOMER})
    public ResponseEntity<CommonResponse<Void>> deleteReview (
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetailsImpl loginUser
    ){

        reviewService.deleteReview(reviewId, loginUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", null));
    }



}
