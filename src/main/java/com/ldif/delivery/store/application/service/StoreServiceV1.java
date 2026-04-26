package com.ldif.delivery.store.application.service;

import com.ldif.delivery.area.domain.entity.AreaEntity;
import com.ldif.delivery.area.domain.repository.AreaRepository;
import com.ldif.delivery.category.domain.entity.CategoryEntity;
import com.ldif.delivery.category.domain.repository.CategoryRepository;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.menu.application.service.MenuServiceV1;
import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.menu.presentation.dto.MenuResponse;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.store.domain.repository.StoreRepository;
import com.ldif.delivery.store.presentation.dto.StoreRequest;
import com.ldif.delivery.store.presentation.dto.StoreResponse;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StoreServiceV1 {

    private final StoreRepository storeRepository;
    private final MenuServiceV1 menuServiceV1;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AreaRepository areaRepository;

    // 가게 생성
    @Transactional
    public UUID createStore(StoreRequest request, UserDetailsImpl loginUser) {

        // owner 조회
        UserEntity owner = userRepository.findByUsername(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // category 조회
        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        // area 조회
        AreaEntity area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new IllegalArgumentException("지역을 찾을 수 없습니다."));

        // 생성
        StoreEntity store = new StoreEntity(
                owner,
                category,
                area,
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

    // 가게 삭제
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

        validateStorePermission(store, loginUser);

        return menuServiceV1.setMenu(request, store, loginUser);
    }

    // 메뉴 조회
    @Transactional(readOnly = true)
    public Page<MenuResponse> getMenus(String keyword, int page, int size, String sort, UUID storeId) {

        Sort.Direction direction = sort.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        List<Integer> allowedSize = Arrays.asList(10, 30, 50);
        int setSize = allowedSize.contains(size) ? size : 10;

        Pageable pageable = PageRequest.of(page, setSize, Sort.by(direction, "createdAt"));

        return menuServiceV1.getMenus(pageable, keyword, storeId);
    }

    // 권한 체크
    private void validateStorePermission(StoreEntity store, UserDetailsImpl loginUser) {
        if (loginUser.isMasterOrManger()) return;

        String createdBy = store.getCreatedBy();

        if (createdBy == null || !createdBy.equals(loginUser.getUsername())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }
}