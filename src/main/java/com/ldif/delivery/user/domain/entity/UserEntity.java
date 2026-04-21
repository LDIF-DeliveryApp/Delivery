package com.ldif.delivery.user.domain.entity;

import com.ldif.delivery.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_user")
public class UserEntity extends BaseEntity {
    @Id
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[a-z0-9]+$", message = "소문자와 숫자만 입력 가능합니다.")
    @Column(name = "username", length = 10)
    private String username;

    @Column(nullable = false, length = 10)
    private String nickname;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column
    private Boolean isPublic = true;


    public  UserEntity(String username, String nickname, String email, String password, UserRoleEnum role){
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
    }




}
