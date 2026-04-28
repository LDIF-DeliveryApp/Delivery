package com.ldif.delivery.global.infrastructure.presentation.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.ldif.delivery.global.exception.ErrorCode;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.review.exception.ReviewException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * JSON 파싱 에러 및 Enum 변환 에러 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException 발생: {}", e.getMessage());

        String errorMessage = "잘못된 JSON 형식 또는 데이터 타입입니다.";
        List<CommonResponse.FieldErrorDto> errors = List.of(new CommonResponse.FieldErrorDto("json", "JSON 파싱 중 오류가 발생했습니다."));

        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getTargetType().isEnum()) {
                String fieldName = invalidFormatException.getPath().get(0).getFieldName();
                String invalidValue = String.valueOf(invalidFormatException.getValue());
                String allowedValues = Arrays.toString(invalidFormatException.getTargetType().getEnumConstants());

                errorMessage = "유효하지 않은 Enum 값입니다.";
                errors = List.of(new CommonResponse.FieldErrorDto(fieldName, 
                        String.format("값 '%s'은(는) 유효하지 않습니다. 허용된 값: %s", invalidValue, allowedValues)));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST.value(), errorMessage, errors));
    }

    /**
     * 컨트롤러 파라미터 타입 불일치 에러 처리 (Query Param, Path Variable 등)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonResponse<?>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException 발생: {}", e.getMessage());

        String fieldName = e.getName();
        String invalidValue = String.valueOf(e.getValue());
        String errorMessage = "잘못된 파라미터 타입입니다.";
        
        Class<?> targetType = e.getRequiredType();
        if (targetType != null && targetType.isEnum()) {
            errorMessage = "유효하지 않은 파라미터 값입니다.";
            String allowedValues = Arrays.toString(targetType.getEnumConstants());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.error(HttpStatus.BAD_REQUEST.value(), errorMessage, 
                            List.of(new CommonResponse.FieldErrorDto(fieldName, 
                                    String.format("값 '%s'은(는) 유효하지 않습니다. 허용된 값: %s", invalidValue, allowedValues)))));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST.value(), errorMessage, 
                        List.of(new CommonResponse.FieldErrorDto(fieldName, "타입이 일치하지 않습니다."))));
    }

    /**
     * ReviewException 처리 (리뷰 도메인 비즈니스 에러)
     */
    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<CommonResponse<?>> handleReviewException(ReviewException e) {
        log.error("ReviewException 발생: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(CommonResponse.error(
                        errorCode.getStatus().value(),
                        errorCode.getMessage(),
                        List.of(new CommonResponse.FieldErrorDto("review", e.getMessage()))
                ));
    }

    /**
     * IllegalArgumentException 처리 (잘못된 인자 전달 시)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "잘못된 요청 인자입니다.", 
                        List.of(new CommonResponse.FieldErrorDto("args", e.getMessage()))));
    }

    /**
     * IllegalStateException 처리 (비즈니스 로직 상 잘못된 상태일 때)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalStateException(IllegalStateException e) {
        log.error("IllegalStateException 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "잘못된 요청 상태입니다.", 
                        List.of(new CommonResponse.FieldErrorDto("state", e.getMessage()))));
    }

    /**
     * AccessDeniedException 처리 (권한 거부)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(CommonResponse.error(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다.", 
                        List.of(new CommonResponse.FieldErrorDto("auth", "해당 리소스에 접근할 권한이 없습니다."))));
    }

    /**
     * NullPointerException 처리 (참조 값이 null인 경우)
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<CommonResponse<?>> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException 발생: {}", e.getMessage());
        String message = e.getMessage() != null ? e.getMessage() : "요청한 리소스를 찾을 수 없습니다.";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(HttpStatus.NOT_FOUND.value(), "리소스를 찾을 수 없습니다.", 
                        List.of(new CommonResponse.FieldErrorDto("resource", message))));
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
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "입력 값 검증에 실패하였습니다.", errors));
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
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "데이터 제약 조건 위반입니다.", errors));
    }

    /**
     * JPA 트랜잭션 커밋 시 발생하는 예외 처리
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<CommonResponse<?>> handleTransactionSystemException(TransactionSystemException e) {
        Throwable cause = e.getRootCause();
        if (cause instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) cause);
        }
        
        log.error("TransactionSystemException 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "트랜잭션 처리 중 오류가 발생했습니다.", 
                        List.of(new CommonResponse.FieldErrorDto("transaction", "데이터베이스 트랜잭션 오류입니다."))));
    }

    /**
     * 그 외 예상치 못한 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleException(Exception e) {
        log.error("예상치 못한 Exception 발생: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다.", 
                        List.of(new CommonResponse.FieldErrorDto("server", e.getMessage() != null ? e.getMessage() : "Unknown error"))));
    }
}
