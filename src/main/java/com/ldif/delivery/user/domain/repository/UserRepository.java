package com.ldif.delivery.user.domain.repository;

import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.deletedAt IS NULL " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:keyword IS NULL OR (u.username LIKE %:keyword% OR u.nickname LIKE %:keyword%))")
    Page<UserEntity> searchUsers(
            @Param("keyword") String keyword,
            @Param("role") UserRoleEnum role,
            Pageable pageable
    );

}
