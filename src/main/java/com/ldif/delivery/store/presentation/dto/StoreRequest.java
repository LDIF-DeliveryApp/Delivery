package com.ldif.delivery.store.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    private String phone;
}
