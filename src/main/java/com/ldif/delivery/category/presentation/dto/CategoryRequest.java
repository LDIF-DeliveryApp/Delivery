package com.ldif.delivery.category.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "카테고리 명은 필수입니다.")
    @Size(max = 20, message = "카테고리 이름은 20자 이내여야 합니다.")
    private String name;
}
