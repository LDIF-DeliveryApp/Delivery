package com.ldif.delivery.ai.presentation.dto;

import com.ldif.delivery.ai.domain.entity.AiRequestLogEntity;
import lombok.Getter;

@Getter
public class AiResponse {
    private final String prompt;
    private final String result;

    public AiResponse(AiRequestLogEntity aiRequestLogEntity) {
        this.prompt = aiRequestLogEntity.getRequestType();
        this.result = aiRequestLogEntity.getResponseText();
    }
}
