package com.ldif.delivery.menu.application.service;

import com.ldif.delivery.ai.application.service.AiServiceV1;
import com.ldif.delivery.area.domain.entity.AreaEntity;
import com.ldif.delivery.category.domain.entity.CategoryEntity;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.menu.domain.entity.MenuEntity;
import com.ldif.delivery.menu.domain.repository.MenuRepository;
import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.menu.presentation.dto.MenuResponse;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import com.ldif.delivery.user.domain.repository.UserRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceV1Test {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuServiceV1 menuServiceV1;

    @InjectMocks
    private AiServiceV1 aiServiceV1;

    private AreaEntity areaEntity;
    private UserEntity ownerUser;
    private UserEntity ownerUser2;
    private CategoryEntity categoryEntity;
    private StoreEntity storeEntity;
    private StoreEntity storeEntity2;
    private MenuEntity menuEntity;


    @BeforeEach
    void setting() {
        // 메뉴, 가게, 유저 설정
        EasyRandom easyRandom = new EasyRandom();
        categoryEntity = easyRandom.nextObject(CategoryEntity.class);
        areaEntity = easyRandom.nextObject(AreaEntity.class);
        ownerUser = new UserEntity("User", "nick", "user@user.com", "pass", UserRoleEnum.OWNER);
        ownerUser2 = new UserEntity("User2", "nick2", "user2@user2.com", "pass", UserRoleEnum.OWNER);
        storeEntity = new StoreEntity(ownerUser, categoryEntity, areaEntity, "store", "address", "phone");
        storeEntity2 = new StoreEntity(ownerUser2, categoryEntity, areaEntity, "store2", "address", "phone");
        MenuRequest menuRequest = new MenuRequest();
        ReflectionTestUtils.setField(menuRequest, "name", "asdf");
        ReflectionTestUtils.setField(menuRequest, "price", 123);
        menuEntity = new MenuEntity(menuRequest, storeEntity);
    }

    @Test
    @DisplayName("메뉴 수정 성공")
    void updateMenu_Success() {

        //given
        UUID menuId = UUID.randomUUID();
        UserDetailsImpl loginUser = new UserDetailsImpl(ownerUser);

        // mock 설정
        given(menuRepository.findById(menuId)).willReturn(Optional.of(menuEntity));

        //when
        MenuRequest newMenuRequest = new MenuRequest();
        ReflectionTestUtils.setField(newMenuRequest, "name", "qwer");
        ReflectionTestUtils.setField(newMenuRequest, "price", 1234);
        MenuResponse response = menuServiceV1.updateMenu(menuId, newMenuRequest, loginUser);

        //then
        assertNotNull(response);
        assertEquals("qwer", menuEntity.getName());
        assertEquals(1234, menuEntity.getPrice());
    }

    @Test
    @DisplayName("메뉴 수정 실패 - 권한 부족(본인 아님)")
    void updateMenu_Fail() {
        // given
        UUID menuId = UUID.randomUUID();
        //다른 가게 오너
        UserDetailsImpl loginUser = new UserDetailsImpl(ownerUser2);

        given(menuRepository.findById(menuId)).willReturn(Optional.of(menuEntity));
        //given(userRepository.findById("User")).willReturn(Optional.of(ownerUser));
        //given(userRepository.findById("User2")).willReturn(Optional.of(ownerUser2));

        //when
        MenuRequest newMenuRequest = new MenuRequest();
        ReflectionTestUtils.setField(newMenuRequest, "name", "qwer");
        ReflectionTestUtils.setField(newMenuRequest, "price", 1234);

        //then
        assertThrows(AccessDeniedException.class, () ->
                menuServiceV1.updateMenu(menuId, newMenuRequest, loginUser));


    }

    @Test
    @DisplayName("메뉴 등록 성공")
    void setMenu() {
        //given
        MenuRequest newMenuRequest = new MenuRequest();
        ReflectionTestUtils.setField(newMenuRequest, "name", "qwer");
        ReflectionTestUtils.setField(newMenuRequest, "price", 1234);
        UserDetailsImpl loginUser = new UserDetailsImpl(ownerUser);

        // when
        MenuResponse response = menuServiceV1.setMenu(newMenuRequest, storeEntity, loginUser);

        //then
        assertNotNull(response);
        assertEquals("qwer", response.getName());
    }

}