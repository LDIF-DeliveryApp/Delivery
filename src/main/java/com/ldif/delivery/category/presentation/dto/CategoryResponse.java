package com.ldif.delivery.category.presentation.dto;

import com.ldif.delivery.category.domain.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryResponse {

    private final UUID categoryId;
    private final String name;

    @Builder
    public CategoryResponse(UUID categoryId, String name)
    {
        this.categoryId = categoryId;
        this.name= name;
    }


    public static CategoryResponse from(CategoryEntity categoryEntity)
    {
        return CategoryResponse.builder()
                .categoryId(categoryEntity.getCategoryId())
                .name(categoryEntity.getName())
                .build();
    }
}


