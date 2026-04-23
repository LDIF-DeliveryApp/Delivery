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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreServiceV1 {

    private final StoreRepository storeRepository;
    private final MenuServiceV1 menuServiceV1;

    // 가게 생성
    @Transactional
    public UUID createStore(StoreRequest request, UserDetailsImpl loginUser) {
        StoreEntity store = new StoreEntity(
                request.getName(),
                request.getAddress(),
                request.getPhone()
        );

        return storeRepository.save(store).getStoreId();
    }

    // 가게 상세 조회
    @Transactional(readOnly = true)
    public StoreResponse getStore(UUID storeId) {
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (store.isDeleted()) {
            throw new IllegalArgumentException("삭제된 가게입니다.");
        }

        return new StoreResponse(store);
    }

    // 가게 목록 조회
    @Transactional(readOnly = true)
    public List<StoreResponse> getStores() {
        return storeRepository.findAll().stream()
                .filter(store -> !store.isDeleted())
                .map(StoreResponse::new)
                .toList();
    }

    // 가게 수정
    @Transactional
    public void updateStore(UUID storeId, StoreRequest request, UserDetailsImpl loginUser) {
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (store.isDeleted()) {
            throw new IllegalArgumentException("삭제된 가게는 수정할 수 없습니다.");
        }

        validateStorePermission(store, loginUser);

        store.updateStore(
                request.getName(),
                request.getAddress(),
                request.getPhone()
        );
    }

    // 가게 삭제 (Soft Delete)
    @Transactional
    public void deleteStore(UUID storeId, UserDetailsImpl loginUser) {
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (store.isDeleted()) {
            throw new IllegalArgumentException("이미 삭제된 가게입니다.");
        }

        validateStorePermission(store, loginUser);
        store.softDelete(loginUser.getUsername());
    }

    // 메뉴 등록
    @Transactional
    public MenuResponse newMenu(UUID storeId, MenuRequest request, UserDetailsImpl loginUser) {

        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (store.isDeleted()) {
            throw new IllegalArgumentException("삭제된 가게에는 메뉴를 등록할 수 없습니다.");
        }

        return menuServiceV1.setMenu(request, store, loginUser);
    }

    // 가게별 메뉴 목록 조회
    @Transactional(readOnly = true)
    public Page<MenuResponse> getMenus(String keyword, int page, int size, String sort, UUID storeId) {
        Sort.Direction direction = sort.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort sortBy = Sort.by(direction, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sortBy);

        return menuServiceV1.getMenus(pageable, keyword, storeId);
    }

    // 가게 권한 체크
    private void validateStorePermission(StoreEntity store, UserDetailsImpl loginUser) {
        if (loginUser.isMasterOrManger()) {
            return;
        }

        String createdBy = store.getCreatedBy();

        if (createdBy == null || !createdBy.equals(loginUser.getUsername())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }
}