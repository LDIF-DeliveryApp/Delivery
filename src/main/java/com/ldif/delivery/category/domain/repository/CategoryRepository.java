package com.ldif.delivery.category.domain.repository;

import com.ldif.delivery.category.domain.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    Optional<CategoryEntity> findByCategoryIdAndDeletedAtIsNull(UUID categoryId);

    boolean existsByNameAndDeletedAtIsNull(String name);

    Page<CategoryEntity> findByNameContainingIgnoreCaseAndIsHiddenFalseAndDeletedAtIsNull(
            String keyword,
            Pageable pageable
    );

    Page<CategoryEntity> findByIsHiddenFalseAndDeletedAtIsNull(Pageable pageable);
}
