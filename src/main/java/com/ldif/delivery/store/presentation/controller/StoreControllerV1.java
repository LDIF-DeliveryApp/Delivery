package com.ldif.delivery.store.presentation.controller;

import com.ldif.delivery.store.presentation.dto.StoreRequest;
import com.ldif.delivery.store.presentation.dto.StoreResponse;
import com.ldif.delivery.store.application.service.StoreServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreControllerV1 {

    private final StoreServiceV1 storeServiceV1;

    @PostMapping
    public UUID createStore(@RequestBody StoreRequest request){
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
    public StoreResponse getStore(@PathVariable UUID storeId){
        return storeServiceV1.getStore(storeId);
    }

    @PutMapping("/{storeId}")
    public void updateStore(@PathVariable UUID storeId,
                            @RequestBody StoreRequest request){
        storeServiceV1.updateStore(storeId, request);
    }

    @DeleteMapping("/{storeId}")
    public void deleteStore(@PathVariable UUID storeId) {
        storeServiceV1.deleteStore(storeId);
    }
}
