package com.ldif.delivery.review.exception;

import com.ldif.delivery.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {

    private final ErrorCode errorCode;

    // ErrorCode의 기본 메시지 사용
    public ReviewException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 상황에 따라 직접 정한 메시지 사용
    public ReviewException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
