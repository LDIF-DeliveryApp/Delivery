package com.ldif.delivery.ai.application.service;

import com.ldif.delivery.ai.domain.repository.AiRequestLogRepository;
import com.ldif.delivery.ai.infrastructure.api.gemini.client.GeminiClient;
import com.ldif.delivery.ai.infrastructure.api.gemini.dto.response.GeminiResponseDto;
import com.ldif.delivery.ai.presentation.dto.AiRequest;
import com.ldif.delivery.ai.presentation.dto.AiResponse;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AiServiceV1Test {

    @Mock
    private AiRequestLogRepository aiRequestLogRepository;
    @Mock
    private GeminiClient geminiClient;

    @InjectMocks
    private AiServiceV1 aiServiceV1;

    private UserDetailsImpl loginUser;
    private UserEntity ownerUser;

    @BeforeEach
    void setUp() {
        ownerUser = new UserEntity("User", "nick", "user@user.com", "pass", UserRoleEnum.OWNER);
        loginUser = new UserDetailsImpl(ownerUser);
    }

    @Test
    @DisplayName("AI 성공")
    void setDescription_Success() {
        // given
        AiRequest request = new AiRequest();
        request.setPrompt("설명");

        GeminiResponseDto geminiResponseDto = new GeminiResponseDto("설명", "치킨");

        given(geminiClient.call(any())).willReturn(geminiResponseDto);

        //when
        AiResponse aiResponse = aiServiceV1.setDescription(request, loginUser);

        //then
        assertNotNull(aiResponse);
        assertEquals("치킨", aiResponse.getResult());
    }

    @Test
    @DisplayName("AI 실패 - 프롬프트 누락")
    void setDescription_Fail_PromptMissing() {
        //given
        UserEntity customer = new UserEntity("user", "nick", "u@u.com", "pass", UserRoleEnum.CUSTOMER);
        UserDetailsImpl customerUser = new UserDetailsImpl(customer);

        AiRequest request = new AiRequest(); // prompt is null

        //when,then
        assertThrows(NullPointerException.class, () ->
                aiServiceV1.setDescription(request, customerUser));
    }
}