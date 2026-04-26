package com.ldif.delivery.area.persentation.controller;

import com.ldif.delivery.area.application.service.AreaServiceV1;
import com.ldif.delivery.area.persentation.dto.AreaRequest;
import com.ldif.delivery.area.persentation.dto.AreaResponse;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.global.infrastructure.presentation.dto.PageResponseDto;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Secured({
        UserRoleEnum.Authority.CUSTOMER,
        UserRoleEnum.Authority.OWNER,
        UserRoleEnum.Authority.MANAGER,
        UserRoleEnum.Authority.MASTER
})
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
public class AreaControllerV1 {

    private final AreaServiceV1 areaServiceV1;

    //지역 생성
    @PostMapping
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    public ResponseEntity<CommonResponse<AreaResponse>> setArea(@Valid @RequestBody AreaRequest request, @AuthenticationPrincipal UserDetailsImpl loginUser) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", areaServiceV1.setArea(request, loginUser)));
    }

    //지역 조회
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

    //지역 상세 조회
    @GetMapping("/{areaId}")
    public ResponseEntity<CommonResponse<AreaResponse>> getArea(@PathVariable UUID areaId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", areaServiceV1.getArea(areaId)));
    }

    //지역 수정
    @PutMapping("/{areaId}")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    public ResponseEntity<CommonResponse<AreaResponse>> updateArea(@PathVariable UUID areaId, @Valid @RequestBody AreaRequest request, @AuthenticationPrincipal UserDetailsImpl loginUser) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", areaServiceV1.updateArea(areaId, request, loginUser)));
    }

    //지역 삭제
    @DeleteMapping("/{areaId}")
    @Secured(UserRoleEnum.Authority.MASTER)
    public ResponseEntity<CommonResponse<String>> deleteArea(@PathVariable UUID areaId, @AuthenticationPrincipal UserDetailsImpl loginUser) {
        areaServiceV1.deleteArea(areaId, loginUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", "deleted"));
    }

    @PatchMapping("/{areaId}")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    public ResponseEntity<CommonResponse<AreaResponse>> toggleActive(@PathVariable UUID areaId, @AuthenticationPrincipal UserDetailsImpl loginUser) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "SUCCESS", areaServiceV1.toggleActive(areaId, loginUser)));
    }
}
