package com.ldif.delivery.user.application.service;

import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.repository.UserRepository;
import com.ldif.delivery.user.presentation.dto.request.ReqUserDto;
import com.ldif.delivery.user.presentation.dto.request.ReqUserRoleDto;
import com.ldif.delivery.user.presentation.dto.response.ResUserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j(topic = "UserService")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceV1 {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<ResUserDto> getUsers(Pageable pageable) {

        Page<UserEntity> userPage = userRepository.findAll(pageable);

        return userPage.map(ResUserDto::new);

    }

    public ResUserDto getUserInfo(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. username: " + username)
        );

        return new ResUserDto(user);

    }

    @Transactional
    public ResUserDto updateUserInfo(String username, ReqUserDto requestDto, UserDetailsImpl loginUser) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 공통 수정 필드
        if (requestDto.getNickname() != null) user.setNickname(requestDto.getNickname());
        if (requestDto.getEmail() != null) user.setEmail(requestDto.getEmail());
        if (requestDto.getIsPublic() != null) user.setIsPublic(requestDto.getIsPublic());


        // 2. 권한별 차등 수정 필드
        // 비밀번호: 오직 본인만! (관리자도 안됨)
        if (requestDto.getPassword() != null) {
            if (loginUser.getUsername().equals(username)) {
                user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            } else {
                throw new AccessDeniedException("비밀번호는 본인만 수정할 수 있습니다.");
            }
        }
        return new ResUserDto(user);

    }

    @Transactional
    public ResUserDto updateUserRole(String username, @Valid ReqUserRoleDto requestDto) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setRole(requestDto.getRole());

        return new ResUserDto(user);
    }
}
