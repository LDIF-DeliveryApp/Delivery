package com.ldif.delivery.user.presentation.dto.request;

import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSignupDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;
    @Email
    @NotBlank
    private String email;
    @NotNull
    private UserRoleEnum role;
}
