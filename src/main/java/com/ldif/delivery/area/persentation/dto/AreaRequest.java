package com.ldif.delivery.area.persentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AreaRequest {

    @NotNull
    private String name;

    @NotNull
    private String city;

    @NotNull
    private String district;
}
