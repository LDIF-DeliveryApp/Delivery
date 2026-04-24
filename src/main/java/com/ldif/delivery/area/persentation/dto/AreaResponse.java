package com.ldif.delivery.area.persentation.dto;

import com.ldif.delivery.area.domain.entity.AreaEntity;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AreaResponse {
    private final UUID areaId;
    private final String name;
    private final String city;
    private final String district;
    private final Boolean isActive;

    public AreaResponse(AreaEntity areaEntity) {
        this.areaId = areaEntity.getAreaId();
        this.name = areaEntity.getName();
        this.city = areaEntity.getCity();
        this.district = areaEntity.getDistrict();
        this.isActive = areaEntity.getIsActive();
    }
}
