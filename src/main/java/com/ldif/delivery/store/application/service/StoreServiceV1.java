package com.ldif.delivery.store.application.service;


import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.menu.application.service.MenuServiceV1;
import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.menu.presentation.dto.MenuResponse;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.store.domain.repository.StoreRepository;
import com.ldif.delivery.store.presentation.dto.StoreRequest;
import com.ldif.delivery.store.presentation.dto.StoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceV1 {

    private final StoreRepository storeRepository;
    private final MenuServiceV1 menuServiceV1;

    @Transactional
    public UUID createStore(StoreRequest request) {

        StoreEntity store = new StoreEntity(
                request.getName(),
                request.getAddress(),
                request.getPhone()
        );

        return storeRepository.save(store).getStoreId();
    }

    public StoreResponse getStore(UUID storeId) {

        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (store.isDeleted()) {
            throw new IllegalArgumentException("삭제된 가게입니다.");
        }

        return new StoreResponse(store);
    }

    @Transactional
    public void updateStore(UUID storeId, StoreRequest request) {

        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (store.isDeleted()) {
            throw new IllegalArgumentException("삭제된 가게는 수정할 수 없습니다.");
        }

        store.updateStore(
                request.getName(),
                request.getAddress(),
                request.getPhone()
        );
    }

    @Transactional
    public void deleteStore(UUID storeId) {

        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (store.isDeleted()) {
            throw new IllegalArgumentException("이미 삭제된 가게입니다.");
        }
    }

    @Transactional
    public MenuResponse newMenu(UUID storeId, MenuRequest request, UserDetailsImpl loginUser) {

        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

//        if(!loginUser.hasPermission(store.getOwnerId())){
//            throw new AccessDeniedException("접근 권한이 없습니다.");
//        }

        return menuServiceV1.setMenu(request, store, loginUser);
    }

    public Page<MenuResponse> getMenus(String keyword, int page, int size, String sort, UUID storeId) {
        Sort.Direction direction = sort.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort SortBy = Sort.by(direction, "createdAt");
        Pageable pageable = PageRequest.of(page, size, SortBy);

        return menuServiceV1.getMenus(pageable, keyword, storeId);
    }
}
