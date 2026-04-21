package com.ldif.delivery.store.controller;

import com.ldif.delivery.store.dto.StoreRequest;
import com.ldif.delivery.store.dto.StoreResponse;
import com.ldif.delivery.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public Long createStore(@RequestBody StoreRequest request){
        return storeService.createStore(request, "testUser");
    }
// 인증/인가 적용 후 사용
// 현재 로그인한 사용자 정보를 받아 createdBy에 전달하기 위한 코드
//    @PostMapping
//    public Long createStore(@RequestBody StoreRequest request,
//                          @AuthenticationPrincipal UserDetails user) {
//      return storeService.createStore(request, user.getUsername());
//    }

    @GetMapping("/{storeId}")
    public StoreResponse getStore(@PathVariable Long storeId){
        return storeService.getStore(storeId);
    }

    @PutMapping("/{storeId}")
    public void updateStore(@PathVariable Long storeId,
                            @RequestBody StoreRequest request){
        storeService.updateStore(storeId, request, "testUser");
    }

    @DeleteMapping("/{storeId}")
    public void deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId, "testUser");
    }
}
