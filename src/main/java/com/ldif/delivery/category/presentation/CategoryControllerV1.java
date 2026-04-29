package com.ldif.delivery.category.presentation;

import com.ldif.delivery.category.application.service.CategoryServiceV1;
import com.ldif.delivery.category.presentation.dto.CategoryRequest;
import com.ldif.delivery.category.presentation.dto.CategoryResponse;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryControllerV1 {

    private final CategoryServiceV1 categoryServiceV1;

    @PostMapping
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    public ResponseEntity<CommonResponse<CategoryResponse>> createCategory(
            @AuthenticationPrincipal UserDetailsImpl loginUser,
            @Valid @RequestBody CategoryRequest request
    ) {
        CategoryResponse response = categoryServiceV1.createCategory(request, loginUser);

        return ResponseEntity
                .created(URI.create("/api/categories/" + response.getCategoryId()))
                .body(CommonResponse.success(
                        HttpStatus.CREATED.value(),
                        "SUCCESS",
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<Page<CategoryResponse>>> searchCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CategoryResponse> categories =
                categoryServiceV1.searchCategories(keyword, page, size);

        return ResponseEntity.ok(
                CommonResponse.success(
                        HttpStatus.OK.value(),
                        "SUCCESS",
                        categories
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<CategoryResponse>> getCategory(@PathVariable UUID id) {
        CategoryResponse category = categoryServiceV1.getCategory(id);

        return ResponseEntity.ok(
                CommonResponse.success(
                        HttpStatus.OK.value(),
                        "SUCCESS",
                        category
                )
        );
    }

    @PutMapping("/{id}")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    public ResponseEntity<CommonResponse<CategoryResponse>> updateCategory(
            @AuthenticationPrincipal UserDetailsImpl loginUser,
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request
    ) {
        CategoryResponse response = categoryServiceV1.updateCategory(id, request, loginUser);

        return ResponseEntity.ok(
                CommonResponse.success(
                        HttpStatus.OK.value(),
                        "SUCCESS",
                        response
                )
        );
    }

    @DeleteMapping("/{id}")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    public ResponseEntity<CommonResponse<Void>> deleteCategory(
            @AuthenticationPrincipal UserDetailsImpl loginUser,
            @PathVariable UUID id
    ) {
        categoryServiceV1.deleteCategory(id, loginUser);

        return ResponseEntity.ok(
                CommonResponse.success(
                        HttpStatus.OK.value(),
                        "SUCCESS",
                        null
                )
        );
    }

}
