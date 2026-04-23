package com.ldif.delivery.user.presentation.dto.response;

import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ResUserDto {
    String username;
    String nickname;
    String email;
    UserRoleEnum role;
    LocalDateTime createdAt;

    public ResUserDto(UserEntity user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}
