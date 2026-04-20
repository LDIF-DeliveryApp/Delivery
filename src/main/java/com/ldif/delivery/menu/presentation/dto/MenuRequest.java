package com.ldif.delivery.menu.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuRequest {

    @NotBlank
    private String name;

    @NotNull
    @Min(value = 0)
    private Integer price;

    private String description;
}
