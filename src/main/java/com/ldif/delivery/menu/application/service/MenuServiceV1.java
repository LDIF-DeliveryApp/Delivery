package com.ldif.delivery.menu.application.service;

import com.ldif.delivery.ai.application.service.AiServiceV1;
import com.ldif.delivery.ai.presentation.dto.AiRequest;
import com.ldif.delivery.ai.presentation.dto.AiResponse;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.menu.domain.entity.MenuEntity;
import com.ldif.delivery.menu.domain.repository.MenuRepository;
import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.menu.presentation.dto.MenuResponse;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import com.ldif.delivery.user.domain.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.EnumSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class MenuServiceV1 {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final AiServiceV1 aiServiceV1;

    //메뉴 상세 조회
    public MenuResponse getMenu(UUID id) {
        MenuEntity menuEntity = findMenuById(id);
        return new MenuResponse(menuEntity);
    }

    //메뉴 수정
    @Transactional
    public MenuResponse updateMenu(UUID id, MenuRequest request, UserDetailsImpl loginUser) {
        MenuEntity menuEntity = findMenuById(id);

        if (!loginUser.hasPermission(menuEntity.getStoreEntity().getOwner().getUsername())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        validateUsrAuthority(loginUser, EnumSet.of(UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER));

        menuEntity.update(request);
        return new MenuResponse(menuEntity);
    }

    //메뉴 삭제(소프트)
    @Transactional
    public void deleteMenu(UUID id, UserDetailsImpl loginUser) {
        MenuEntity menuEntity = findMenuById(id);

        if (!loginUser.hasPermission(menuEntity.getStoreEntity().getOwner().getUsername())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        validateUsrAuthority(loginUser, EnumSet.of(UserRoleEnum.OWNER, UserRoleEnum.MASTER));


        menuEntity.delete(loginUser.getUsername());
    }

    //메뉴 숨김 처리
    @Transactional
    public MenuResponse hideMenu(UUID id, UserDetailsImpl loginUser) {
        MenuEntity menuEntity = findMenuById(id);

        if (!loginUser.hasPermission(menuEntity.getStoreEntity().getOwner().getUsername())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        validateUsrAuthority(loginUser, EnumSet.of(UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER));

        menuEntity.hide();
        return new MenuResponse(menuEntity);
    }

    //메뉴 등록(AI 설명 생성 옵션)
    @Transactional
    public MenuResponse setMenu(@Valid MenuRequest request, StoreEntity store, UserDetailsImpl loginUser) {
        MenuEntity menuEntity = new MenuEntity(request, store);

        if (!loginUser.hasPermission(menuEntity.getStoreEntity().getOwner().getUsername())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        validateUsrAuthority(loginUser, EnumSet.of(UserRoleEnum.OWNER));

        if (Boolean.TRUE.equals(request.getAiDescription())) {
            AiRequest aiRequest = new AiRequest();
            aiRequest.setPrompt(request.getAiPrompt());
            AiResponse aiResponse = aiServiceV1.setDescription(aiRequest, loginUser);
            menuEntity.setDescription(aiResponse.getResult());
        }
        menuRepository.save(menuEntity);
        return new MenuResponse(menuEntity);
    }

    //메뉴 목록 조회
    public Page<MenuResponse> getMenus(Pageable pageable, String keyword, UUID storeId) {
        Page<MenuEntity> menuList;

        // FK storeEntity의 id와 일치 항목 모두 찾아 name에 keyword 포함된 목록 검색, idDeleted가 false인 값들만 반환
        menuList = menuRepository.findAllByStoreEntity_StoreIdAndNameContainingIgnoreCaseAndIsDeletedFalse(storeId, keyword, pageable);

        return menuList.map(MenuResponse::new);
    }

    //메뉴 조회
    private MenuEntity findMenuById(UUID id) {
        MenuEntity menuEntity = menuRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("메뉴 없음." + id));
        if (menuEntity.getIsDeleted()) {
            throw new IllegalArgumentException("메뉴 없음." + id);
        }
        return menuEntity;
    }

    private void validateUsrAuthority(UserDetailsImpl loginUser, EnumSet<UserRoleEnum> requireAuthorities) {
        UserEntity user = userRepository.findById(loginUser.getUsername()).orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));

        if (!requireAuthorities.contains(user.getRole())) {
            throw new AccessDeniedException("권한 없음");
        }
    }

}
