package com.ldif.delivery.global.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    // 1. 생성 시간
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // 2. 생성자
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    // 3. 수정 시간
    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    // 4. 수정자
    @LastModifiedBy
    @Column
    private String updatedBy;

    // 5. 삭제 시간 (Soft Delete용)
    @Column
    private LocalDateTime deletedAt;

    // 6. 삭제자
    @Column
    private String deletedBy;


    // 7. 소프트 삭제
    public void softDelete(String username) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = username;
    }

}
