package com.ldif.delivery.review.presentation.dto;

import com.ldif.delivery.review.domain.entity.ReviewEntity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ResReviewDetailDto {
    private UUID reviewId;
    private UUID storeId;
    private String storeName;
    private String nickName;
    private Integer rating;
    private String content;
    private LocalDateTime createAt;

    public ResReviewDetailDto(ReviewEntity review){
        this.reviewId = review.getReviewId();
        this.storeId = review.getStore().getStoreId();
        this.storeName = review.getStore().getName();
        this.nickName = review.getUser().getNickname();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createAt = review.getCreatedAt();
    }
}
