package com.ldif.delivery.store.domain.entity;

import com.ldif.delivery.area.domain.entity.AreaEntity;
import com.ldif.delivery.category.domain.entity.CategoryEntity;
import com.ldif.delivery.global.infrastructure.entity.BaseEntity;
import com.ldif.delivery.user.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "store_id", updatable = false, nullable = false)
    private UUID storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private AreaEntity area;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(name = "average_rating", precision = 2, scale = 1)
    private BigDecimal averageRating = BigDecimal.valueOf(0.0);

    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer reviewCount = 0;

    @Column(name = "is_hidden")
    private boolean isHidden = false;

    public StoreEntity(
            UserEntity owner,
            CategoryEntity category,
            AreaEntity area,
            String name,
            String address,
            String phone
    ) {
        this.owner = owner;
        this.category = category;
        this.area = area;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.isHidden = false;
        this.averageRating = BigDecimal.valueOf(0.0);
    }

    public void updateStore(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public void updateRating(BigDecimal newRating) {
        if (newRating.compareTo(BigDecimal.ZERO) < 0 ||
                newRating.compareTo(BigDecimal.valueOf(5.0)) > 0) {
            throw new IllegalArgumentException("평점은 0.0 이상 5.0 이하여야 합니다.");
        }
        this.averageRating = newRating;
    }

    public void addReview(Integer newRating) {
        // 1. 현재 총점 계산 = 기존 평균 * 기존 개수
        BigDecimal currentTotalScore = this.averageRating.multiply(new BigDecimal(this.reviewCount));

        // 2. 새로운 총점 = 기존 총점 + 이번 평점
        BigDecimal newTotalScore = currentTotalScore.add(new BigDecimal(newRating));

        // 3. 새로운 리뷰 개수
        this.reviewCount++;

        // 4. 새로운 평균 = 새로운 총점 / 새로운 개수 (소수점 첫째 자리까지 반올림)
        // 2.15 -> 2.2 / 2.14 -> 2.1
        this.averageRating = newTotalScore.divide(new BigDecimal(this.reviewCount), 1, RoundingMode.HALF_UP);
    }

    public void hide() {
        this.isHidden = true;
    }

    public void unhide() {
        this.isHidden = false;
    }

    public boolean isDeleted() {
        return this.getDeletedAt() != null;
    }
}