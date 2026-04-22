package com.ldif.delivery.ai.domain.entity;

import com.ldif.delivery.ai.infrastructure.api.gemini.dto.response.GeminiResponseDto;
import com.ldif.delivery.ai.presentation.dto.AiRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_ai_request_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AiRequestLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID aiLogId;

    //요청 텍스트
    @Column(nullable = false, length = 100)
    private String requestText;

    //AI 응답 텍스트
    @Column
    private String responseText;

    //Product_Description
    @Column(nullable = false)
    private String requestType;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false, length = 100)
    private String createdBy;

    //요청자
//    @ManyToOne
//    @JoinColumn(name = userId, nullable = false)
//    private User user;

    public AiRequestLogEntity(AiRequest aiRequest) {
        this.requestType = aiRequest.getPrompt();
    }

    public void setAiResponse(GeminiResponseDto geminiResponseDto) {
        this.requestText = geminiResponseDto.request();
        this.responseText = geminiResponseDto.response();
    }

}
