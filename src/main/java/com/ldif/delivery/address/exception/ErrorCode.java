package com.ldif.delivery.address.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
        ADDRESS_NOT_FOUND(HttpStatus.NOT.FOUND, "주소를 찾을 수 없습니다."),
            ADDRESS_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 주소에 접근 권한이 없습니다."),
                ADDRESS_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "주소는 최대 10개까지 등록 가능합니다.");

    private final HttpStatus status;
    private final String message;
}
