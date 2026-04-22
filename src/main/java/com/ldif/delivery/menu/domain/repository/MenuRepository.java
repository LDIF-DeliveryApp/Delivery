package com.ldif.delivery.menu.domain.repository;

import com.ldif.delivery.menu.domain.entity.MenuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<MenuEntity, UUID> {
    Page<MenuEntity> findAllByStoreEntity_StoreIdAndNameContainingIgnoreCaseAndIsDeletedFalse(UUID storeId, String keyword, Pageable pageable);
}
