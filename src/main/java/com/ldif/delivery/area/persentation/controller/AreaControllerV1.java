package com.ldif.delivery.area.persentation.controller;

import com.ldif.delivery.area.application.service.AreaServiceV1;
import com.ldif.delivery.area.persentation.dto.AreaRequest;
import com.ldif.delivery.area.persentation.dto.AreaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class AreaControllerV1 {

    private final AreaServiceV1 areaServiceV1;

    @PostMapping
    public ResponseEntity<AreaResponse> setArea(@Valid @RequestBody AreaRequest request){
        return ResponseEntity.ok(areaServiceV1.setArea(request));
    }
}
