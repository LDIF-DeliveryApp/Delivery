package com.ldif.delivery.user.presentation.controller;

import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.global.infrastructure.presentation.dto.PageResponseDto;
import com.ldif.delivery.user.application.service.UserServiceV1;
import com.ldif.delivery.user.presentation.dto.response.ResUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserControllerV1 {

    private final UserServiceV1 userService;

    //TODO
    // Pagenation size파라미터 10,30,50만 허용 (그 외 기본 10건)

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponseDto<ResUserDto>>> getUsers (
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {

        Page<ResUserDto> userPage = userService.getUsers(pageable);

        PageResponseDto<ResUserDto> data = new PageResponseDto<>(userPage);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS",data ));

        //return ResponseEntity.ok(userService.getUsers(pageable));
    }
}
