package com.ldif.delivery.menu.domain.repository;

import com.ldif.delivery.menu.domain.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
}
