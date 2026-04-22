package com.ldif.delivery.category.domain.repository;

import com.ldif.delivery.category.domain.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    boolean existsByName(String name);
}
