package com.ldif.delivery.menu.domain.entity;

import com.ldif.delivery.global.infrastructure.entity.BaseEntity;
import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID menuId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column
    private String description;

    @Column(nullable = false)
    private Boolean isHidden = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean isDeleted = Boolean.FALSE;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity storeEntity;

    public UUID getStoreId() {
        return storeEntity != null ? storeEntity.getStoreId() : null;
    }

    public MenuEntity(MenuRequest request, StoreEntity storeEntity) {
        this.storeEntity = storeEntity;
        this.name = request.getName();
        this.price = request.getPrice();
        this.description = request.getDescription();
    }

    public void update(@Valid MenuRequest request) {
        this.name = request.getName();
        this.price = request.getPrice();
        this.description = request.getDescription();
    }

    public void hide() {
        isHidden = !isHidden;
    }

    public void delete(String username) {
        this.isDeleted = true;
        super.softDelete(username);
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
