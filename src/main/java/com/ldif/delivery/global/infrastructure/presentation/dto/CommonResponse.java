package com.ldif.delivery.global.infrastructure.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
    private int status;
    private String message;
    private T data;
    private List<FieldErrorDto> errors; // 에러 상세 정보

    // 성공 응답 (데이터 포함)
    public static <T> CommonResponse<T> success(int status, String message, T data) {
        return new CommonResponse<>(status, message, data, null);
    }

    // 에러 응답
    public static <T> CommonResponse<T> error(int status, String message, List<FieldErrorDto> errors) {
        return new CommonResponse<>(status, message, null, errors);
    }

    @Getter
    @AllArgsConstructor
    public static class FieldErrorDto {
        private String field;
        private String message;
    }

}
