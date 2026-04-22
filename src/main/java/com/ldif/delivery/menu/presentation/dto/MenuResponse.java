package com.ldif.delivery.menu.presentation.dto;

import com.ldif.delivery.menu.domain.entity.MenuEntity;
import lombok.Getter;

@Getter
public class MenuResponse {
    private final Long menuId;
    private final String name;
    private final Integer price;
    private final String description;
    private final Boolean isHidden;
    //private final Store store;

    public MenuResponse(MenuEntity menuEntity) {
        this.menuId = menuEntity.getMenuId();
        this.name = menuEntity.getName();
        this.price = menuEntity.getPrice();
        this.description = menuEntity.getDescription();
        this.isHidden = menuEntity.getIsHidden();
        //this.store=menu.getStore();
    }
}
