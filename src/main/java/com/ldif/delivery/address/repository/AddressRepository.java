package com.ldif.delivery.address.repository;

import com.ldif.delivery.address.entity.Address;
import com.ldif.delivery.user.domain.entity.UserEntity; // User 패키지 맞게 수정
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

 public interface  AddressRepository extends JpaRepository<Address, UUID>{

        // 유저의 전체 주소 조회 (기본 주소 먼저, 최신순)
        List<Address> findAllByUserOrderByIsDefaultDescCreatedAtDesc(UserEntity user);

        // 본인 주소 단건 조회 (보안: userId 조건 포함)
        Optional<Address> findByAddressIdAndUser(UUID addressId, UserEntity user);

        // 기존 기본 주소 조회
        Optional<Address> findByUserAndIsDefaultTrue(UserEntity user);

        // 유저 주소 존재 여부
        boolean existsByUser(UserEntity user);
    }

