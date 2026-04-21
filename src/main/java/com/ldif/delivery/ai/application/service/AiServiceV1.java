package com.ldif.delivery.ai.application.service;

import com.ldif.delivery.ai.domain.entity.AiRequestLogEntity;
import com.ldif.delivery.ai.domain.repository.AiRequestLogRepository;
import com.ldif.delivery.ai.infrastructure.api.gemini.client.GeminiClient;
import com.ldif.delivery.ai.infrastructure.api.gemini.dto.response.GeminiResponseDto;
import com.ldif.delivery.ai.presentation.dto.AiRequest;
import com.ldif.delivery.ai.presentation.dto.AiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiServiceV1 {

    private final AiRequestLogRepository aiRequestLogRepository;
    private final GeminiClient geminiClient;

    @Transactional
    public AiResponse setDescription(@Valid AiRequest aiRequest) {
        GeminiResponseDto result = geminiClient.call(aiRequest.getPrompt());
        AiRequestLogEntity aiRequestLogEntity = new AiRequestLogEntity(aiRequest);
        aiRequestLogEntity.getAiResponse(result);
        aiRequestLogRepository.save(aiRequestLogEntity);
        return new AiResponse(aiRequestLogEntity);
    }
}
