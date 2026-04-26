package com.ldif.delivery.ai.domain.entity;

import com.ldif.delivery.ai.infrastructure.api.gemini.dto.response.GeminiResponseDto;
import com.ldif.delivery.ai.presentation.dto.AiRequest;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.user.domain.entity.UserEntity;
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
    @GeneratedValue(strategy = GenerationType.UUID)
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
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "username", nullable = false)
    private UserEntity userEntity;

    public AiRequestLogEntity(AiRequest aiRequest, UserDetailsImpl loginUser) {
        this.requestType = aiRequest.getPrompt();
        this.userEntity = loginUser.getUser();
    }

    public void setAiResponse(GeminiResponseDto geminiResponseDto) {
        this.requestText = geminiResponseDto.request();
        this.responseText = geminiResponseDto.response();
    }

}
