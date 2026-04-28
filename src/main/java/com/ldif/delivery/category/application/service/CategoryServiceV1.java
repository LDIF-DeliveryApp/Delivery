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
    public CategoryResponse createCategory(CategoryRequest request,UserDetailsImpl loginUser)
    {
        validateUserAuthority(
                loginUser,
                EnumSet.of(UserRoleEnum.MASTER, UserRoleEnum.MANAGER)
        );

        //1. 이름 중복 체크
        if(categoryRepository.existsByName(request.getName()))
        {
           throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }
        CategoryEntity categoryEntity = CategoryEntity.builder().name(request.getName()).build();
        categoryRepository.save(categoryEntity);
        return CategoryResponse.from(categoryEntity);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(UUID categoryId)
    {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("존재하지 않습니다!"));
        return CategoryResponse.from(categoryEntity);
    }

    public List<CategoryResponse> getCategories(){
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                // 각 CategoryEntity를 받아서 CategoryResponse.from(entity)를 호출해라.
                .toList();
    }

    @Transactional
    public CategoryResponse updateCategory(UUID categoryId,
                                           CategoryRequest request,
                                           UserDetailsImpl loginUser) {

        validateUserAuthority(
                loginUser,
                EnumSet.of(UserRoleEnum.MASTER, UserRoleEnum.MANAGER)
        );

        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
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

        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않습니다"));

        categoryRepository.delete(categoryEntity);
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
