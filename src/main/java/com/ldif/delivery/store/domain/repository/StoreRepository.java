package com.ldif.delivery.store.domain.repository;

import com.ldif.delivery.store.domain.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {

}
