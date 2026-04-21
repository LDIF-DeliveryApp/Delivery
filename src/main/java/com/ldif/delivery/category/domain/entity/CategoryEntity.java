package com.ldif.delivery.category.domain.entity;

import com.ldif.delivery.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "p_category") // db예약어랑 안겹치기위해 테이블 네임 지어주기
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_category SET delete_at = NOW() WHERE category_id = ?") // soft delete
@SQLRestriction("delete_at IS NULL")
public class CategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID categoryId;
    // Identity 1씩증가 UUID 유일한값을위해 (확장성 보안)!!

    @Column(nullable = false,unique = true)
    private String name;

    @Builder
    public CategoryEntity(String name) {
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }

    public void softDelete()
    {

    }
}
