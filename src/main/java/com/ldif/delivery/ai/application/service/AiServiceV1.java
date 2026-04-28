package com.ldif.delivery.ai.application.service;

import com.ldif.delivery.ai.domain.entity.AiRequestLogEntity;
import com.ldif.delivery.ai.domain.repository.AiRequestLogRepository;
import com.ldif.delivery.ai.infrastructure.api.gemini.client.GeminiClient;
import com.ldif.delivery.ai.infrastructure.api.gemini.dto.response.GeminiResponseDto;
import com.ldif.delivery.ai.presentation.dto.AiRequest;
import com.ldif.delivery.ai.presentation.dto.AiResponse;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import com.ldif.delivery.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.EnumSet;

@Service
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
public class AiServiceV1 {

    private final AiRequestLogRepository aiRequestLogRepository;
    private final GeminiClient geminiClient;

    @Transactional
    public AiResponse setDescription(AiRequest aiRequest, @AuthenticationPrincipal UserDetailsImpl loginUser) {

        GeminiResponseDto result = geminiClient.call(aiRequest.getPrompt());
        AiRequestLogEntity aiRequestLogEntity = new AiRequestLogEntity(aiRequest, loginUser);
        aiRequestLogEntity.setAiResponse(result);
        aiRequestLogRepository.save(aiRequestLogEntity);
        return new AiResponse(aiRequestLogEntity);
    }
}
