package com.ldif.delivery.category.presentation;

import com.ldif.delivery.category.application.service.CategoryServiceV1;
import com.ldif.delivery.category.presentation.dto.CategoryRequest;
import com.ldif.delivery.category.presentation.dto.CategoryResponse;
import jakarta.servlet.ServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request)
    {
        CategoryResponse createCategoryResponse = categoryServiceV1.createCategory(request);
        return ResponseEntity.created(URI.create("/api/categories/" + createCategoryResponse.getCategoryId())).body(createCategoryResponse);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getProducts(){
        return ResponseEntity.ok(categoryServiceV1.getCategorys());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getProduct(@PathVariable UUID id, ServletResponse servletResponse)
    {
        return ResponseEntity.ok(categoryServiceV1.getCategory(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateProdct(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request)
    {
        return ResponseEntity.ok(categoryServiceV1.updateCategory(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id){
        categoryServiceV1.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
