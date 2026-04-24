package com.ldif.delivery.user.presentation.dto.request;

import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReqUserRoleDto {
    @NotNull
    private UserRoleEnum role;
}
