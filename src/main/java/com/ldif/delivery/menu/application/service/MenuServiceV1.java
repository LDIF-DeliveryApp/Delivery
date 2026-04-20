package com.ldif.delivery.menu.application.service;

import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.menu.presentation.dto.MenuResponse;
import com.ldif.delivery.menu.domain.entity.MenuEntity;
import com.ldif.delivery.menu.domain.repository.MenuRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuServiceV1 {

    private final MenuRepository menuRepository;

    public MenuResponse getMenu(Long id) {
        MenuEntity menuEntity = findMenuById(id);
        return new MenuResponse(menuEntity);
    }

    @Transactional
    public MenuResponse updateMenu(Long id, @Valid MenuRequest request) {
        MenuEntity menuEntity = findMenuById(id);
        menuEntity.update(request);
        return new MenuResponse(menuEntity);
    }

    @Transactional
    public void deleteMenu(Long id) {
        MenuEntity menuEntity = findMenuById(id);
        menuEntity.delete();
    }

    @Transactional
    public MenuResponse hideMenu(Long id) {
        MenuEntity menuEntity = findMenuById(id);
        menuEntity.hide();
        return new MenuResponse(menuEntity);
    }

    private MenuEntity findMenuById(Long id) {
        MenuEntity menuEntity = menuRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("메뉴 없음." + id));
        if(menuEntity.getIsDeleted()){
            throw new IllegalArgumentException("메뉴 없음." + id);
        }
        return menuEntity;
    }


}
