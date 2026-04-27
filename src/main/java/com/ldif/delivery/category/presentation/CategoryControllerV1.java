package com.ldif.delivery.category.presentation;

import com.ldif.delivery.category.application.service.CategoryServiceV1;
import com.ldif.delivery.category.presentation.dto.CategoryRequest;
import com.ldif.delivery.category.presentation.dto.CategoryResponse;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import jakarta.servlet.ServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryControllerV1 {

    private final CategoryServiceV1 categoryServiceV1;

    @PostMapping
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    public ResponseEntity<CommonResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request)
    {
        CategoryResponse createCategoryResponse = categoryServiceV1.createCategory(request);
        return ResponseEntity
                .created(URI.create("/api/categories" + createCategoryResponse.getCategoryId()))
                .body(CommonResponse.success(
                        HttpStatus.CREATED.value(),
                        "SUCCESS",
                        createCategoryResponse
                ));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<CategoryResponse>>> getProducts(){
        List<CategoryResponse> categories = categoryServiceV1.getCategories();

        return ResponseEntity.ok(CommonResponse.success(
                HttpStatus.OK.value(),
                "SUCCESS",
                categories
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<CategoryResponse>> getProduct(@PathVariable UUID id)
    {
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
    public ResponseEntity<CommonResponse<CategoryResponse>> updateProduct(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request)
    {
        CategoryResponse updatedCategory = categoryServiceV1.updateCategory(id, request);

        return ResponseEntity.ok(
                CommonResponse.success(
                        HttpStatus.OK.value(),
                        "SUCCESS",
                        updatedCategory
                )
        );
    }

    @DeleteMapping("/{id}")
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    public ResponseEntity<CommonResponse<Void>> deleteProduct(@PathVariable UUID id){
        categoryServiceV1.deleteCategory(id);

        return ResponseEntity.ok(
                CommonResponse.success(
                        HttpStatus.OK.value(),
                        "SUCCESS",
                        null
                )
        );
    }

}
