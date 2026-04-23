package com.ldif.delivery.menu.application.service;

import com.ldif.delivery.ai.application.service.AiServiceV1;
import com.ldif.delivery.ai.presentation.dto.AiRequest;
import com.ldif.delivery.ai.presentation.dto.AiResponse;
import com.ldif.delivery.menu.domain.entity.MenuEntity;
import com.ldif.delivery.menu.domain.repository.MenuRepository;
import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.menu.presentation.dto.MenuResponse;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class MenuServiceV1 {

    private final MenuRepository menuRepository;
    private final AiServiceV1 aiServiceV1;

    public MenuResponse getMenu(UUID id) {
        MenuEntity menuEntity = findMenuById(id);
        return new MenuResponse(menuEntity);
    }

    @Transactional
    public MenuResponse updateMenu(UUID id, @Valid MenuRequest request) {
        MenuEntity menuEntity = findMenuById(id);
        menuEntity.update(request);
        return new MenuResponse(menuEntity);
    }

    @Transactional
    public void deleteMenu(UUID id) {
        MenuEntity menuEntity = findMenuById(id);
        menuEntity.delete();
    }

    @Transactional
    public MenuResponse hideMenu(UUID id) {
        MenuEntity menuEntity = findMenuById(id);
        menuEntity.hide();
        return new MenuResponse(menuEntity);
    }

    private MenuEntity findMenuById(UUID id) {
        MenuEntity menuEntity = menuRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("메뉴 없음." + id));
        if (menuEntity.getIsDeleted()) {
            throw new IllegalArgumentException("메뉴 없음." + id);
        }
        return menuEntity;
    }

    @Transactional
    public MenuResponse setMenu(@Valid MenuRequest request, StoreEntity store) {
        MenuEntity menuEntity = new MenuEntity(request, store);
        if (Boolean.TRUE.equals(request.getAiDescription())) {
            AiRequest aiRequest = new AiRequest();
            aiRequest.setPrompt(request.getAiPrompt());
            AiResponse aiResponse = aiServiceV1.setDescription(aiRequest);
            menuEntity.setDescription(aiResponse.getResult());
        }
        menuRepository.save(menuEntity);
        return new MenuResponse(menuEntity);
    }

    public Page<MenuResponse> getMenus(Pageable pageable, String keyword, UUID storeId) {
        Page<MenuEntity> menuList;

        menuList = menuRepository.findAllByStoreEntity_StoreIdAndNameContainingIgnoreCaseAndIsDeletedFalse(storeId, keyword, pageable);

        return menuList.map(MenuResponse::new);
    }
}
