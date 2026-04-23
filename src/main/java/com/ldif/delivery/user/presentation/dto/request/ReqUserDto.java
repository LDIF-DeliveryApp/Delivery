package com.ldif.delivery.user.presentation.dto.request;

import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class ReqUserDto {
    private String password;
    private String nickname;
    @Email
    private String email;

    private UserRoleEnum role;

    private Boolean isPublic;
}
