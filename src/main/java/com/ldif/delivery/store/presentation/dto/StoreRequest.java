package com.ldif.delivery.store.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class StoreRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    private String phone;

    @NotNull
    private UUID categoryId;

    @NotNull
    private UUID areaId;
}