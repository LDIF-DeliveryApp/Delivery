package com.ldif.delivery.store.domain.repository;

import com.ldif.delivery.store.domain.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {

    Page<StoreEntity> findByNameContainingAndDeletedAtIsNull(String keyword, Pageable pageable);

}
