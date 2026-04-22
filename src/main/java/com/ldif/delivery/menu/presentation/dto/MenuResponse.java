package com.ldif.delivery.menu.presentation.dto;

import com.ldif.delivery.menu.domain.entity.MenuEntity;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MenuResponse {
    private final UUID menuId;
    private final String name;
    private final Integer price;
    private final String description;
    private final Boolean isHidden;
    private final UUID storeId;

    public MenuResponse(MenuEntity menuEntity) {
        this.menuId = menuEntity.getMenuId();
        this.name = menuEntity.getName();
        this.price = menuEntity.getPrice();
        this.description = menuEntity.getDescription();
        this.isHidden = menuEntity.getIsHidden();
        this.storeId = menuEntity.getStoreEntity().getStoreId();
    }
}
