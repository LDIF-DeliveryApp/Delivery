package com.ldif.delivery.store.presentation.controller;

import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.global.infrastructure.presentation.dto.PageResponseDto;
import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.menu.presentation.dto.MenuResponse;
import com.ldif.delivery.store.application.service.StoreServiceV1;
import com.ldif.delivery.store.presentation.dto.StoreRequest;
import com.ldif.delivery.store.presentation.dto.StoreResponse;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreControllerV1 {

    private final StoreServiceV1 storeServiceV1;

    @PostMapping
    public UUID createStore(@RequestBody StoreRequest request) {
        return storeServiceV1.createStore(request);
    }
// 인증/인가 적용 후 사용
// 현재 로그인한 사용자 정보를 받아 createdBy에 전달하기 위한 코드
//    @PostMapping
//    public Long createStore(@RequestBody StoreRequest request,
//                          @AuthenticationPrincipal UserDetails user) {
//      return storeService.createStore(request);
//    }

    @GetMapping("/{storeId}")
    public StoreResponse getStore(@PathVariable UUID storeId) {
        return storeServiceV1.getStore(storeId);
    }

    @PutMapping("/{storeId}")
    public void updateStore(@PathVariable UUID storeId,
                            @RequestBody StoreRequest request) {
        storeServiceV1.updateStore(storeId, request);
    }

    @DeleteMapping("/{storeId}")
    public void deleteStore(@PathVariable UUID storeId) {
        storeServiceV1.deleteStore(storeId);
    }

    @PostMapping("/{storeId}/menus")
    @Secured(UserRoleEnum.Authority.OWNER)
    public ResponseEntity<CommonResponse<MenuResponse>> setMenu(@PathVariable UUID storeId, @Valid @RequestBody MenuRequest request, @AuthenticationPrincipal UserDetailsImpl loginUser) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", storeServiceV1.newMenu(storeId, request, loginUser)));
    }

    @GetMapping("/{storeId}/menus")
    public ResponseEntity<CommonResponse<PageResponseDto<MenuResponse>>> getMenus(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam("sort") String sort,
            @PathVariable UUID storeId
    ) {
        Page<MenuResponse> menuPage = storeServiceV1.getMenus(keyword, page, size, sort, storeId);
        PageResponseDto<MenuResponse> data = new PageResponseDto<>(menuPage);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", data));
    }
}
