package com.ldif.delivery.global.infrastructure.presentation.advice;

import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * IllegalArgumentException 처리 (잘못된 인자 전달 시)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(CommonResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(), null));
    }

    /**
     * NullPointerException 처리 (참조 값이 null인 경우)
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<CommonResponse<?>> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage() != null ? e.getMessage() : "Null pointer exception occurred", null));
    }

    /**
     * DTO 검증 실패 시 (@Valid) 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException 발생: {}", e.getMessage());

        List<CommonResponse.FieldErrorDto> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new CommonResponse.FieldErrorDto(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", errors));
    }

    /**
     * 엔티티 검증 실패 시 (DB 저장 직전 등) 발생하는 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException 발생: {}", e.getMessage());

        List<CommonResponse.FieldErrorDto> errors = e.getConstraintViolations().stream()
                .map(violation -> new CommonResponse.FieldErrorDto(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "ENTITY_VALIDATION_ERROR", errors));
    }

    /**
     * JPA 트랜잭션 커밋 시 발생하는 예외 처리 (내부에 ConstraintViolationException이 포함된 경우가 많음)
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<CommonResponse<?>> handleTransactionSystemException(TransactionSystemException e) {
        Throwable cause = e.getRootCause();
        if (cause instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) cause);
        }
        
        log.error("TransactionSystemException 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "TRANSACTION_ERROR", null));
    }

    /**
     * 그 외 예상치 못한 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleException(Exception e) {
        log.error("예상치 못한 Exception 발생: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR", null));
    }
}
