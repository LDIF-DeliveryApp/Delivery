package com.ldif.delivery.store.application.service;

import com.ldif.delivery.area.domain.repository.AreaRepository;
import com.ldif.delivery.category.domain.entity.CategoryEntity;
import com.ldif.delivery.category.domain.repository.CategoryRepository;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.menu.application.service.MenuServiceV1;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.store.domain.repository.StoreRepository;
import com.ldif.delivery.store.presentation.dto.StoreRequest;
import com.ldif.delivery.store.presentation.dto.StoreResponse;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceV1Test {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuServiceV1 menuServiceV1;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private StoreServiceV1 storeService;

    @Test
    @DisplayName("가게 상세 조회 실패 - 가게를 찾을 수 없음")
    void getStore_fails_whenStoreNotFound() {
        // given
        UUID storeId = UUID.randomUUID();

        given(storeRepository.findById(storeId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.getStore(storeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가게를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("가게 상세 조회 실패 - 삭제된 가게")
    void getStore_fails_whenStoreDeleted() {
        // given
        UUID storeId = UUID.randomUUID();
        StoreEntity store = mock(StoreEntity.class);

        given(storeRepository.findById(storeId))
                .willReturn(Optional.of(store));
        given(store.isDeleted())
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> storeService.getStore(storeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("삭제된 가게입니다.");
    }

    @Test
    @DisplayName("가게 목록 조회 성공")
    void getStores_succeeds() {
        // given
        String keyword = "치킨";
        int page = 0;
        int size = 10;
        String sort = "DESC";

        StoreEntity store = mock(StoreEntity.class);

        given(store.getStoreId()).willReturn(UUID.randomUUID());
        given(store.getName()).willReturn("치킨 가게");
        given(store.getAddress()).willReturn("서울시 종로구");
        given(store.getPhone()).willReturn("010-1234-5678");
        given(store.getAverageRating()).willReturn(BigDecimal.valueOf(0.0));
        given(store.isHidden()).willReturn(false);
        given(store.getCreatedAt()).willReturn(null);
        given(store.getUpdatedAt()).willReturn(null);

        Page<StoreEntity> storePage = new PageImpl<>(List.of(store));

        given(storeRepository.findByNameContainingAndDeletedAtIsNull(
                eq(keyword),
                any(Pageable.class)
        )).willReturn(storePage);

        // when
        Page<StoreResponse> result = storeService.getStores(keyword, page, size, sort);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("치킨 가게");

        verify(storeRepository).findByNameContainingAndDeletedAtIsNull(
                eq(keyword),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("가게 목록 조회 성공 - 허용되지 않은 페이지 사이즈는 10으로 변경")
    void getStores_succeeds_whenInvalidSizeChangedToDefaultSize() {
        // given
        String keyword = "피자";
        int page = 0;
        int size = 999;
        String sort = "DESC";

        given(storeRepository.findByNameContainingAndDeletedAtIsNull(
                eq(keyword),
                any(Pageable.class)
        )).willReturn(Page.empty());

        // when
        Page<StoreResponse> result = storeService.getStores(keyword, page, size, sort);

        // then
        assertThat(result).isEmpty();

        verify(storeRepository).findByNameContainingAndDeletedAtIsNull(
                eq(keyword),
                argThat(pageable ->
                        pageable.getPageNumber() == 0 &&
                                pageable.getPageSize() == 10 &&
                                pageable.getSort().getOrderFor("createdAt").isDescending()
                )
        );
    }

    @Test
    @DisplayName("가게 수정 실패 - 가게를 찾을 수 없음")
    void updateStore_fails_whenStoreNotFound() {
        // given
        UUID storeId = UUID.randomUUID();
        StoreRequest request = mock(StoreRequest.class);
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);

        given(storeRepository.findById(storeId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.updateStore(storeId, request, loginUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가게를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("가게 수정 실패 - 삭제된 가게")
    void updateStore_fails_whenStoreDeleted() {
        // given
        UUID storeId = UUID.randomUUID();
        StoreRequest request = mock(StoreRequest.class);
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);
        StoreEntity store = mock(StoreEntity.class);

        given(storeRepository.findById(storeId))
                .willReturn(Optional.of(store));
        given(store.isDeleted())
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> storeService.updateStore(storeId, request, loginUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("삭제된 가게는 수정할 수 없습니다.");

        verify(store, never()).updateStore(any(), any(), any());
    }

    @Test
    @DisplayName("가게 수정 실패 - 접근 권한 없음")
    void updateStore_fails_whenAccessDenied() {
        // given
        UUID storeId = UUID.randomUUID();
        StoreRequest request = mock(StoreRequest.class);
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);
        StoreEntity store = mock(StoreEntity.class);

        given(storeRepository.findById(storeId))
                .willReturn(Optional.of(store));
        given(store.isDeleted())
                .willReturn(false);

        given(loginUser.isMasterOrManger())
                .willReturn(false);
        given(loginUser.getUsername())
                .willReturn("otherUser");
        given(store.getCreatedBy())
                .willReturn("ownerUser");

        // when & then
        assertThatThrownBy(() -> storeService.updateStore(storeId, request, loginUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("접근 권한이 없습니다.");

        verify(store, never()).updateStore(any(), any(), any());
    }

    @Test
    @DisplayName("가게 수정 성공")
    void updateStore_succeeds() {
        // given
        UUID storeId = UUID.randomUUID();
        StoreRequest request = mock(StoreRequest.class);
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);
        StoreEntity store = mock(StoreEntity.class);

        given(storeRepository.findById(storeId))
                .willReturn(Optional.of(store));
        given(store.isDeleted())
                .willReturn(false);

        given(loginUser.isMasterOrManger())
                .willReturn(false);
        given(loginUser.getUsername())
                .willReturn("ownerUser");
        given(store.getCreatedBy())
                .willReturn("ownerUser");

        given(request.getName()).willReturn("수정된 가게");
        given(request.getAddress()).willReturn("서울시 종로구");
        given(request.getPhone()).willReturn("010-1234-5678");

        // when
        storeService.updateStore(storeId, request, loginUser);

        // then
        verify(store).updateStore(
                "수정된 가게",
                "서울시 종로구",
                "010-1234-5678"
        );
    }

    @Test
    @DisplayName("가게 삭제 실패 - 가게를 찾을 수 없음")
    void deleteStore_fails_whenStoreNotFound() {
        // given
        UUID storeId = UUID.randomUUID();
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);

        given(storeRepository.findById(storeId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.deleteStore(storeId, loginUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가게를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("가게 삭제 실패 - 이미 삭제된 가게")
    void deleteStore_fails_whenAlreadyDeleted() {
        // given
        UUID storeId = UUID.randomUUID();
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);
        StoreEntity store = mock(StoreEntity.class);

        given(storeRepository.findById(storeId))
                .willReturn(Optional.of(store));
        given(store.isDeleted())
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> storeService.deleteStore(storeId, loginUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 삭제된 가게입니다.");

        verify(store, never()).softDelete(any());
    }

    @Test
    @DisplayName("가게 삭제 성공")
    void deleteStore_succeeds() {
        // given
        UUID storeId = UUID.randomUUID();
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);
        StoreEntity store = mock(StoreEntity.class);

        given(storeRepository.findById(storeId))
                .willReturn(Optional.of(store));
        given(store.isDeleted())
                .willReturn(false);

        given(loginUser.isMasterOrManger())
                .willReturn(false);
        given(loginUser.getUsername())
                .willReturn("ownerUser");
        given(store.getCreatedBy())
                .willReturn("ownerUser");

        // when
        storeService.deleteStore(storeId, loginUser);

        // then
        verify(store).softDelete("ownerUser");
    }

    @Test
    @DisplayName("가게 생성 실패 - 유저를 찾을 수 없음")
    void createStore_fails_whenUserNotFound() {
        // given
        StoreRequest request = mock(StoreRequest.class);
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);

        given(loginUser.getUsername())
                .willReturn("ownerUser");
        given(userRepository.findByUsername("ownerUser"))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.createStore(request, loginUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유저를 찾을 수 없습니다.");

        verify(storeRepository, never()).save(any());
    }

    @Test
    @DisplayName("가게 생성 실패 - 카테고리를 찾을 수 없음")
    void createStore_fails_whenCategoryNotFound() {
        // given
        StoreRequest request = mock(StoreRequest.class);
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);
        UserEntity owner = mock(UserEntity.class);
        UUID categoryId = UUID.randomUUID();

        given(loginUser.getUsername()).willReturn("ownerUser");
        given(userRepository.findByUsername("ownerUser"))
                .willReturn(Optional.of(owner));

        given(request.getCategoryId()).willReturn(categoryId);
        given(categoryRepository.findById(categoryId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.createStore(request, loginUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리를 찾을 수 없습니다.");

        verify(storeRepository, never()).save(any());
    }

    @Test
    @DisplayName("가게 생성 실패 - 지역을 찾을 수 없음")
    void createStore_fails_whenAreaNotFound() {
        // given
        StoreRequest request = mock(StoreRequest.class);
        UserDetailsImpl loginUser = mock(UserDetailsImpl.class);
        UserEntity owner = mock(UserEntity.class);
        CategoryEntity category = mock(CategoryEntity.class);
        UUID categoryId = UUID.randomUUID();
        UUID areaId = UUID.randomUUID();

        given(loginUser.getUsername()).willReturn("ownerUser");
        given(userRepository.findByUsername("ownerUser"))
                .willReturn(Optional.of(owner));

        given(request.getCategoryId()).willReturn(categoryId);
        given(categoryRepository.findById(categoryId))
                .willReturn(Optional.of(category));

        given(request.getAreaId()).willReturn(areaId);
        given(areaRepository.findById(areaId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.createStore(request, loginUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지역을 찾을 수 없습니다.");

        verify(storeRepository, never()).save(any());
    }
}