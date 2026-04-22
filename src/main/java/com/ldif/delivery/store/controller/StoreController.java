package com.ldif.delivery.store.controller;

import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.menu.presentation.dto.MenuResponse;
import com.ldif.delivery.store.presentation.dto.StoreRequest;
import com.ldif.delivery.store.presentation.dto.StoreResponse;
import com.ldif.delivery.store.application.service.StoreServiceV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreServiceV1 storeService;

    @PostMapping
    public UUID createStore(@RequestBody StoreRequest request) {
        return storeService.createStore(request);
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
        return storeService.getStore(storeId);
    }

    @PutMapping("/{storeId}")
    public void updateStore(@PathVariable UUID storeId,
                            @RequestBody StoreRequest request) {
        storeService.updateStore(storeId, request);
    }

    @DeleteMapping("/{storeId}")
    public void deleteStore(@PathVariable UUID storeId) {
        storeService.deleteStore(storeId);
    }

    @PostMapping("/{storeId}/menus")
    public ResponseEntity<MenuResponse> setMenu(@PathVariable UUID storeId, @Valid @RequestBody MenuRequest request) {
        return ResponseEntity.ok(storeService.newMenu(storeId, request));
    }

    @GetMapping("/{storeId}/menus")
    public Page<MenuResponse> getMenus(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam("sort") String sort,
            @PathVariable UUID storeId
    ) {
        return storeService.getMenus(keyword, page, size, sort, storeId);
    }
}
