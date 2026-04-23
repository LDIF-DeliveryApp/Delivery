package com.ldif.delivery.store.presentation.controller;

import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.store.application.service.StoreServiceV1;
import com.ldif.delivery.store.presentation.dto.StoreRequest;
import com.ldif.delivery.store.presentation.dto.StoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreControllerV1 {

    private final StoreServiceV1 storeServiceV1;

    @PostMapping
    public ResponseEntity<CommonResponse<UUID>> createStore(@RequestBody StoreRequest request) {
        UUID storeId = storeServiceV1.createStore(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(HttpStatus.CREATED.value(), "SUCCESS", storeId));
    }

// 인증/인가 적용 후 사용
// 현재 로그인한 사용자 정보를 받아 createdBy에 전달하기 위한 코드
//    @PostMapping
//    public ResponseEntity<CommonResponse<UUID>> createStore(@RequestBody StoreRequest request,
//                                                            @AuthenticationPrincipal UserDetails user) {
//        UUID storeId = storeServiceV1.createStore(request);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(CommonResponse.success(HttpStatus.CREATED.value(), "SUCCESS", storeId));
//    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<StoreResponse>>> getStores() {
        List<StoreResponse> stores = storeServiceV1.getStores();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", stores));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<CommonResponse<StoreResponse>> getStore(@PathVariable UUID storeId) {
        StoreResponse store = storeServiceV1.getStore(storeId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", store));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<CommonResponse<Void>> updateStore(@PathVariable UUID storeId,
                                                            @RequestBody StoreRequest request) {
        storeServiceV1.updateStore(storeId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", null));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<CommonResponse<Void>> deleteStore(@PathVariable UUID storeId) {
        storeServiceV1.deleteStore(storeId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", null));
    }
}