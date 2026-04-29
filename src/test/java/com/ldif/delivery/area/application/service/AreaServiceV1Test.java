package com.ldif.delivery.area.application.service;

import com.ldif.delivery.area.domain.repository.AreaRepository;
import com.ldif.delivery.area.persentation.dto.AreaRequest;
import com.ldif.delivery.area.persentation.dto.AreaResponse;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import com.ldif.delivery.user.domain.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AreaServiceV1Test {

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private AreaServiceV1 areaServiceV1;

    private UserDetailsImpl loginUser;
    private UserEntity manger;

    @BeforeEach
    void set() {
        manger = new UserEntity("man", "nic", "e@e.com", "pass", UserRoleEnum.MANAGER);
        loginUser = new UserDetailsImpl(manger);
    }


    @Test
    @DisplayName("새로운 지역 성공")
    void setArea_Success() {
        //given
        AreaRequest request = new AreaRequest();
        ReflectionTestUtils.setField(request, "name", "서울");

        //when
        AreaResponse response = areaServiceV1.setArea(request, loginUser);

        //then
        assertNotNull(response);
        assertEquals("서울", response.getName());
    }

    @Test
    @DisplayName("새로운 지역 성공 - 권한 체크 없음")
    void setArea_NoAuthCheck() {
        //given
        UserEntity customer = new UserEntity("user", "nick", "u@u.com", "pass", UserRoleEnum.CUSTOMER);
        UserDetailsImpl customerUser = new UserDetailsImpl(customer);
        AreaRequest request = new AreaRequest();
        ReflectionTestUtils.setField(request, "name", "경기");

        //when
        AreaResponse response = areaServiceV1.setArea(request, customerUser);

        //then
        assertNotNull(response);
        assertEquals("경기", response.getName());
    }

}