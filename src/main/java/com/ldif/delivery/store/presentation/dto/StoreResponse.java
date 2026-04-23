package com.ldif.delivery.store.presentation.dto;

import com.ldif.delivery.store.domain.entity.StoreEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class StoreResponse {

    private final UUID storeId;
    private final String name;
    private final String address;
    private final String phone;
    private final BigDecimal averageRating;
    private final boolean isHidden;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public StoreResponse(StoreEntity store) {
        this.storeId = store.getStoreId();
        this.name = store.getName();
        this.address = store.getAddress();
        this.phone = store.getPhone();
        this.averageRating = store.getAverageRating();
        this.isHidden = store.isHidden();
        this.createdAt = store.getCreatedAt();
        this.updatedAt = store.getUpdatedAt();
    }
}