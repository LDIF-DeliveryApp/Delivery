package com.ldif.delivery.area.domain.repository;

import com.ldif.delivery.area.domain.entity.AreaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AreaRepository extends JpaRepository<AreaEntity, UUID> {
    Page<AreaEntity> findByNameContainingAndIsDeletedFalse(String keyword, Pageable pageable);
}
