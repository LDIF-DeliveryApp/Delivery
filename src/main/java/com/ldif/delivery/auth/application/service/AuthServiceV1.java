package com.ldif.delivery.auth.application.service;

import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import com.ldif.delivery.user.domain.repository.UserRepository;
import com.ldif.delivery.user.presentation.dto.request.ReqSignupDto;
import com.ldif.delivery.user.presentation.dto.response.ResUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceV1 {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResUserDto signup(ReqSignupDto reqSignupDto) {
        String username = reqSignupDto.getUsername();
        String password = passwordEncoder.encode(reqSignupDto.getPassword());
        String nickname = reqSignupDto.getNickname();
        UserRoleEnum role = reqSignupDto.getRole();

        // 회원 중복 확인
        Optional<UserEntity> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // email 중복 확인
        String email = reqSignupDto.getEmail();
        Optional<UserEntity> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()){
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        

        UserEntity user = new UserEntity(username, nickname, email, password, role);
        UserEntity savedUser = userRepository.save(user);

        return new ResUserDto(savedUser);
    }
}
