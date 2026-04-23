package com.ldif.delivery.area.persentation.controller;

import com.ldif.delivery.area.application.service.AreaServiceV1;
import com.ldif.delivery.area.persentation.dto.AreaRequest;
import com.ldif.delivery.area.persentation.dto.AreaResponse;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.global.infrastructure.presentation.dto.PageResponseDto;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
public class AreaControllerV1 {

    private final AreaServiceV1 areaServiceV1;

    @PostMapping
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    public ResponseEntity<CommonResponse<AreaResponse>> setArea(@Valid @RequestBody AreaRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", areaServiceV1.setArea(request)));
    }


    @GetMapping
    public ResponseEntity<CommonResponse<PageResponseDto<AreaResponse>>> getAreas(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam("sort") String sort
    ) {
        Page<AreaResponse> areaPage = areaServiceV1.getAreas(keyword, page, size, sort);
        PageResponseDto<AreaResponse> data = new PageResponseDto<>(areaPage);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", data));
    }


}
