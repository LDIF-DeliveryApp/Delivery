package com.ldif.delivery.store.entity;

import com.ldif.delivery.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "p_store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreEntity extends BaseEntity {

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

    public StoreEntity(String name, String address, String phone) {
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