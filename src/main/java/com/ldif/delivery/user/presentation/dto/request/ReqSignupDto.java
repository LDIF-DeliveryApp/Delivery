package com.ldif.delivery.user.presentation.dto.request;

import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSignupDto {
    @NotBlank
    private String username;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_=+])[A-Za-z\\d@$!%*?&#^()_=+]{8,15}$",
            message = "비밀번호는 8~15자이며, 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private UserRoleEnum role;
}
