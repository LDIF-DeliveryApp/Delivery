package com.ldif.delivery.review.domain.respository;

import com.ldif.delivery.review.domain.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    Optional<ReviewEntity> findByReviewIdAndDeletedAtIsNull(UUID reviewId);
    Optional<ReviewEntity> findByOrder_OrderId(UUID orderId);

    @Query("SELECT r FROM ReviewEntity r " +
            "WHERE r.deletedAt IS NULL " +
            "AND (:storeId IS NULL OR r.store.storeId = :storeId) " +
            "AND (:rating IS NULL OR r.rating = :rating)")
    Page<ReviewEntity> searchReviews(
            @Param("storeId") UUID storeId,
            @Param("rating") Integer rating,
            Pageable pageable
    );


}
