package com.ldif.delivery.menu.presentation.controller;

import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.menu.application.service.MenuServiceV1;
import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.menu.presentation.dto.MenuResponse;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuControllerV1 {

    private final MenuServiceV1 menuService;

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<MenuResponse>> getMenu(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", menuService.getMenu(id)));
    }

    @PutMapping("/{id}")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER, UserRoleEnum.Authority.OWNER})
    public ResponseEntity<CommonResponse<MenuResponse>> updateMenu(@PathVariable UUID id, @Valid @RequestBody MenuRequest request, @AuthenticationPrincipal UserDetailsImpl loginUser) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", menuService.updateMenu(id, request, loginUser)));
    }

    @DeleteMapping("/{id}")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.OWNER})
    public ResponseEntity<CommonResponse<String>> deleteMenu(@PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl loginUser) {
        menuService.deleteMenu(id, loginUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", "deleted"));
    }

    @PatchMapping("/{id}/hide")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER, UserRoleEnum.Authority.OWNER})
    public ResponseEntity<CommonResponse<MenuResponse>> hideMenu(@PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl loginUser) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", menuService.hideMenu(id, loginUser)));
    }

}
