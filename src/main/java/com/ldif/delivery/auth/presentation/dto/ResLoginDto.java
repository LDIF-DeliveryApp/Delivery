package com.ldif.delivery.auth.presentation.dto;

import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResLoginDto {
    private String accessToken;
    private String username;
    private UserRoleEnum role;;

    public ResLoginDto(String accessToken, String username, UserRoleEnum role){
        this.accessToken = accessToken;
        this.username = username;
        this.role = role;
    }

}
