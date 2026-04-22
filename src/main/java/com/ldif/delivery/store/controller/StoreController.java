package com.ldif.delivery.store.controller;

import com.ldif.delivery.store.dto.StoreRequest;
import com.ldif.delivery.store.dto.StoreResponse;
import com.ldif.delivery.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public UUID createStore(@RequestBody StoreRequest request){
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
    public StoreResponse getStore(@PathVariable UUID storeId){
        return storeService.getStore(storeId);
    }

    @PutMapping("/{storeId}")
    public void updateStore(@PathVariable UUID storeId,
                            @RequestBody StoreRequest request){
        storeService.updateStore(storeId, request);
    }

    @DeleteMapping("/{storeId}")
    public void deleteStore(@PathVariable UUID storeId) {
        storeService.deleteStore(storeId);
    }
}
