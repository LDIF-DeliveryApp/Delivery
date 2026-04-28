package com.ldif.delivery.store.domain.repository;

import com.ldif.delivery.store.domain.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {

    Page<StoreEntity> findByNameContainingAndDeletedAtIsNull(String keyword, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("SELECT s FROM StoreEntity s WHERE s.storeId = :storeId AND s.deletedAt IS NULL")
    Optional<StoreEntity> findByIdWithLock(@Param("storeId") UUID storeId);
}
