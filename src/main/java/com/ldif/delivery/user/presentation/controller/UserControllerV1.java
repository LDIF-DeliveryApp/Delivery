package com.ldif.delivery.user.presentation.controller;

import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.global.infrastructure.presentation.dto.PageResponseDto;
import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.user.application.service.UserServiceV1;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import com.ldif.delivery.user.presentation.dto.request.ReqUserDto;
import com.ldif.delivery.user.presentation.dto.request.ReqUserRoleDto;
import com.ldif.delivery.user.presentation.dto.response.ResUserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserControllerV1 {

    private final UserServiceV1 userService;

    //TODO
    // Pagenation size파라미터 10,30,50만 허용 (그 외 기본 10건)

    @GetMapping
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    public ResponseEntity<CommonResponse<PageResponseDto<ResUserDto>>> getUsers (
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserRoleEnum role,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {

        Page<ResUserDto> userPage = userService.getUsers(keyword, role, pageable);

        PageResponseDto<ResUserDto> data = new PageResponseDto<>(userPage);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS",data));
    }

    @GetMapping("/{username}")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER, UserRoleEnum.Authority.CUSTOMER})
    public ResponseEntity<CommonResponse<ResUserDto>> getUserInfo (@PathVariable String username, @AuthenticationPrincipal UserDetailsImpl loginUser){

        ResUserDto user = userService.getUserInfo(username, loginUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", user));

    }

    @PutMapping("/{username}")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER, UserRoleEnum.Authority.CUSTOMER})
    public ResponseEntity<CommonResponse<ResUserDto>> updateUserInfo (
            @PathVariable String username,
            @RequestBody ReqUserDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl loginUser){

        if(!loginUser.hasPermission(username)){
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        ResUserDto user = userService.updateUserInfo(username, requestDto, loginUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", user));
    }


    @PutMapping("/{username}/role")
    @Secured(UserRoleEnum.Authority.MASTER)
    public ResponseEntity<CommonResponse<ResUserDto>> updateUserRole (
            @PathVariable String username,
            @Valid @RequestBody ReqUserRoleDto requestDto
    ){

        ResUserDto user = userService.updateUserRole(username, requestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", user));
    }

    @DeleteMapping("/{username}")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    public ResponseEntity<CommonResponse<Void>> deleteUser (
            @PathVariable String username,
            @AuthenticationPrincipal UserDetailsImpl loginUser
    ){
        userService.deleteUser(username, loginUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", null));
    }
}
