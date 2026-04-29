package com.ldif.delivery.category.application.service;

import com.ldif.delivery.category.domain.entity.CategoryEntity;
import com.ldif.delivery.category.domain.repository.CategoryRepository;
import com.ldif.delivery.category.presentation.dto.CategoryRequest;
import com.ldif.delivery.category.presentation.dto.CategoryResponse;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import com.ldif.delivery.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceV1 {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, UserDetailsImpl loginUser) {
        validateUserAuthority(
                loginUser,
                EnumSet.of(UserRoleEnum.MASTER, UserRoleEnum.MANAGER)
        );

        if (categoryRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }

        CategoryEntity categoryEntity = CategoryEntity.builder()
                .name(request.getName())
                .build();

        categoryRepository.save(categoryEntity);

        return CategoryResponse.from(categoryEntity);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(UUID categoryId) {
        CategoryEntity categoryEntity = categoryRepository.findByCategoryIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않습니다!"));

        return CategoryResponse.from(categoryEntity);
    }

    @Transactional
    public CategoryResponse updateCategory(UUID categoryId,
                                           CategoryRequest request,
                                           UserDetailsImpl loginUser) {
        validateUserAuthority(
                loginUser,
                EnumSet.of(UserRoleEnum.MASTER, UserRoleEnum.MANAGER)
        );

        CategoryEntity categoryEntity = categoryRepository.findByCategoryIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않습니다"));

        categoryEntity.update(request.getName());

        return CategoryResponse.from(categoryEntity);
    }

    @Transactional
    public void deleteCategory(UUID categoryId, UserDetailsImpl loginUser) {
        validateUserAuthority(
                loginUser,
                EnumSet.of(UserRoleEnum.MASTER, UserRoleEnum.MANAGER)
        );

        CategoryEntity categoryEntity = categoryRepository.findByCategoryIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않습니다"));

        categoryEntity.softDelete(loginUser.getUsername());
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> searchCategories(String keyword, int page, int size) {

        List<Integer> allowedSizes = List.of(10, 30, 50);

        if (!allowedSizes.contains(size)) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<CategoryEntity> categories;

        if (keyword == null || keyword.isBlank()) {
            categories = categoryRepository.findByIsHiddenFalseAndDeletedAtIsNull(pageable);
        } else {
            categories = categoryRepository.findByNameContainingIgnoreCaseAndIsHiddenFalseAndDeletedAtIsNull(
                    keyword,
                    pageable
            );
        }

        return categories.map(CategoryResponse::from);
    }

    private void validateUserAuthority(UserDetailsImpl loginUser,
                                       EnumSet<UserRoleEnum> requiredAuthorities) {
        UserEntity user = userRepository.findById(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));

        if (!requiredAuthorities.contains(user.getRole())) {
            throw new AccessDeniedException("권한 없음");
        }
    }
}