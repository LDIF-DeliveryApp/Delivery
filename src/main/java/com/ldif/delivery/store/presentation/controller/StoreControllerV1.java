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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreControllerV1 {

    private final StoreServiceV1 storeServiceV1;

    // 가게 생성
    @PostMapping
    @Secured({
            UserRoleEnum.Authority.OWNER,
            UserRoleEnum.Authority.MANAGER,
            UserRoleEnum.Authority.MASTER
    })
    public ResponseEntity<CommonResponse<UUID>> createStore(
            @RequestBody StoreRequest request,
            @AuthenticationPrincipal UserDetailsImpl loginUser
    ) {
        UUID storeId = storeServiceV1.createStore(request, loginUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(HttpStatus.CREATED.value(), "SUCCESS", storeId));
    }

    // 가게 목록 조회
    @GetMapping
    @Secured({
            UserRoleEnum.Authority.CUSTOMER,
            UserRoleEnum.Authority.OWNER,
            UserRoleEnum.Authority.MANAGER,
            UserRoleEnum.Authority.MASTER
    })
    public ResponseEntity<CommonResponse<List<StoreResponse>>> getStores() {
        List<StoreResponse> stores = storeServiceV1.getStores();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", stores));
    }

    // 가게 상세 조회
    @GetMapping("/{storeId}")
    @Secured({
            UserRoleEnum.Authority.CUSTOMER,
            UserRoleEnum.Authority.OWNER,
            UserRoleEnum.Authority.MANAGER,
            UserRoleEnum.Authority.MASTER
    })
    public ResponseEntity<CommonResponse<StoreResponse>> getStore(@PathVariable UUID storeId) {
        StoreResponse store = storeServiceV1.getStore(storeId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", store));
    }

    // 가게 수정
    @PutMapping("/{storeId}")
    @Secured({
            UserRoleEnum.Authority.OWNER,
            UserRoleEnum.Authority.MANAGER,
            UserRoleEnum.Authority.MASTER
    })
    public ResponseEntity<CommonResponse<Void>> updateStore(
            @PathVariable UUID storeId,
            @RequestBody StoreRequest request,
            @AuthenticationPrincipal UserDetailsImpl loginUser
    ) {
        storeServiceV1.updateStore(storeId, request, loginUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", null));
    }

    // 가게 삭제
    @DeleteMapping("/{storeId}")
    @Secured({
            UserRoleEnum.Authority.OWNER,
            UserRoleEnum.Authority.MANAGER,
            UserRoleEnum.Authority.MASTER
    })
    public ResponseEntity<CommonResponse<Void>> deleteStore(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal UserDetailsImpl loginUser
    ) {
        storeServiceV1.deleteStore(storeId, loginUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", null));
    }

    //메뉴 추가
    @PostMapping("/{storeId}/menus")
    @Secured(UserRoleEnum.Authority.OWNER)
    public ResponseEntity<CommonResponse<MenuResponse>> setMenu(@PathVariable UUID storeId, @Valid @RequestBody MenuRequest request, @AuthenticationPrincipal UserDetailsImpl loginUser) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", storeServiceV1.newMenu(storeId, request, loginUser)));
    }

    //메뉴 목록 조회
    @GetMapping("/{storeId}/menus")
    @Secured({
            UserRoleEnum.Authority.CUSTOMER,
            UserRoleEnum.Authority.OWNER,
            UserRoleEnum.Authority.MANAGER,
            UserRoleEnum.Authority.MASTER
    })
    public ResponseEntity<CommonResponse<PageResponseDto<MenuResponse>>> getMenus(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam("sort") String sort,
            @PathVariable UUID storeId) {
        Page<MenuResponse> menuPage = storeServiceV1.getMenus(keyword, page, size, sort, storeId);
        PageResponseDto<MenuResponse> data = new PageResponseDto<>(menuPage);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", data));
    }
}
