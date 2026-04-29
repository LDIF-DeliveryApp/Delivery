package com.ldif.delivery.category.domain.entity;

import com.ldif.delivery.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_category") // db예약어랑 안겹치기위해 테이블 네임 지어주기
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID categoryId;
    // Identity 1씩증가 UUID 유일한값을위해 (확장성 보안)!!

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Boolean isHidden = false;

    @Builder
    public CategoryEntity(String name) {
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }

    public void hide() {
        this.isHidden = true;
    }

    public void show() {
        this.isHidden = false;
    }
}
