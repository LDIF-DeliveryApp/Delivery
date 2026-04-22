package com.ldif.delivery.user.application.service;

import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.repository.UserRepository;
import com.ldif.delivery.user.presentation.dto.response.ResUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceV1 {

    private final UserRepository userRepository;

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
}
