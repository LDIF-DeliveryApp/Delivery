package com.ldif.delivery.menu.presentation.controller;

import com.ldif.delivery.menu.application.service.MenuServiceV1;
import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.menu.presentation.dto.MenuResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuControllerV1 {

    private final MenuServiceV1 menuService;

    @GetMapping("/{id}")
    public ResponseEntity<MenuResponse> getMenu(@PathVariable UUID id) {
        return ResponseEntity.ok(menuService.getMenu(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuResponse> updateMenu(@PathVariable UUID id, @Valid @RequestBody MenuRequest request) {
        return ResponseEntity.ok(menuService.updateMenu(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable UUID id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/hide")
    public ResponseEntity<MenuResponse> hideMenu(@PathVariable UUID id) {
        return ResponseEntity.ok(menuService.hideMenu(id));
    }

}
