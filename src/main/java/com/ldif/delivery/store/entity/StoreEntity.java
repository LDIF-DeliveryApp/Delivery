package com.ldif.delivery.store.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "p_store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(name = "average_rating", precision = 2, scale = 1)
    private BigDecimal averageRating = BigDecimal.valueOf(0.0);

    @Column(name = "is_hidden")
    private boolean isHidden = false;

    // 생성 시간
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 생성자
    @Column(name = "created_by", length = 100)
    private String createdBy;

    // 수정 시간
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 수정자
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    // 삭제 시간 (soft delete)
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 삭제자
    @Column(name = "deleted_by", length = 100)
    private String deletedBy;


    // 생성자
    public StoreEntity(String name, String address, String phone, String createdBy) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.isHidden = false;
        this.averageRating = BigDecimal.valueOf(0.0);
    }


    public void updateStore(String name, String address, String phone, String updatedBy) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRating(BigDecimal newRating) {
        if (newRating.compareTo(BigDecimal.ZERO) < 0 ||
                newRating.compareTo(BigDecimal.valueOf(5.0)) > 0) {
            throw new IllegalArgumentException("평점은 0.0 이상 5.0 이하여야 합니다.");
        }
        this.averageRating = newRating;
    }

    public void hide() {
        this.isHidden = true;
    }

    public void unhide() {
        this.isHidden = false;
    }

    public void softDelete(String deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}