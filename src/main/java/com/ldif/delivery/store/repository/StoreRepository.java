package com.ldif.delivery.store.repository;

import com.ldif.delivery.store.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {
}
