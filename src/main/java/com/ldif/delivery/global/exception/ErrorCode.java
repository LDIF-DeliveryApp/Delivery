package com.ldif.delivery.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력 값입니다."),
    
    // Review
    REVIEW_REGISTRATION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "리뷰 등록이 실패했습니다. 재시도 해주세요."),
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 등록된 리뷰가 있습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    
    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    ORDER_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "배달이 완료된 주문만 리뷰를 작성할 수 있습니다."),
    
    // Auth
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;
}
