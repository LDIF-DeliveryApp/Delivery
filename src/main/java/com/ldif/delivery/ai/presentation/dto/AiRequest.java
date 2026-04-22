package com.ldif.delivery.ai.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AiRequest {

    @NotBlank(message = "요청 텍스트는 필수")
    @NotNull
    private String prompt;
}
