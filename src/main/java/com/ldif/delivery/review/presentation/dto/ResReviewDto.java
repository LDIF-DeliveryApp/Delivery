package com.ldif.delivery.review.presentation.dto;

import com.ldif.delivery.review.domain.entity.ReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ResReviewDto {
    private UUID reviewId;
    private UUID storeId;
    private String storeName;
    private String nickName;
    private Integer rating;
    private String summary;
    private LocalDateTime createAt;

    public ResReviewDto(ReviewEntity review, String summary){
        this.reviewId = review.getReviewId();
        this.storeId = review.getStore().getStoreId();
        this.storeName = review.getStore().getName();
        this.nickName = review.getUser().getNickname();
        this.rating = review.getRating();
        this.summary = summary;
        this.createAt = review.getCreatedAt();
    }

}
