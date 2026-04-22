package com.ldif.delivery.ai.infrastructure.api.gemini.client;

import com.ldif.delivery.ai.infrastructure.api.gemini.dto.response.GeminiResponseDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.stereotype.Component;

@Component
public class GeminiClient {
    private final ChatClient chatClient;

    public GeminiClient(ChatClient.Builder builder){
        this.chatClient=builder
                .defaultSystem("답변을 최대한 간결하게 50자 이하로")
                .build();
    }

    public GeminiResponseDto call(String prompt){
        return new GeminiResponseDto(prompt, chatClient.prompt(prompt).call().content());
    }
}
