package com.ldif.delivery.auth.presentation.dto;

import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReqLoginDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
