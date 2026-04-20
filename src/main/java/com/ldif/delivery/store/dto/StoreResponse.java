package com.ldif.delivery.store.dto;

import com.ldif.delivery.store.entity.StoreEntity;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class StoreResponse {

    private Long storeId;
    private String name;
    private String address;
    private String phone;
    private BigDecimal averageRating;

    public StoreResponse(StoreEntity store) {
        this.storeId = store.getStoreId();
        this.name = store.getName();
        this.address = store.getAddress();
        this.phone = store.getPhone();
        this.averageRating = store.getAverageRating();
    }
}
