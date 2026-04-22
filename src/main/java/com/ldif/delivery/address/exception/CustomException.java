package com.ldif.delivery.address.exception;

import ch.qos.logback.core.spi.ErrorCodes;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorcode){
        super(errorCode.getmessage());
        this.errorCode = errorcode;
    }
}
