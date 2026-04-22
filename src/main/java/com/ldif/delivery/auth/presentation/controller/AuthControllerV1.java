package com.ldif.delivery.auth.presentation.controller;

import com.ldif.delivery.auth.application.service.AuthServiceV1;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;
import com.ldif.delivery.user.presentation.dto.request.ReqSignupDto;
import com.ldif.delivery.user.presentation.dto.response.ResUserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j(topic = "AuthController")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthControllerV1 {

    private final AuthServiceV1 authService;

    @PostMapping("/signup")
    public ResponseEntity <CommonResponse<ResUserDto>> signup(@Valid @RequestBody ReqSignupDto reqSignupDto, BindingResult bindingResult){

        //Valiadation 예외처리
        if(bindingResult.hasErrors()){
            // BindingResult에 담긴 에러들을 우리 규격(FieldErrorDto)으로 변환
            List<CommonResponse.FieldErrorDto> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> new CommonResponse.FieldErrorDto(error.getField(), error.getDefaultMessage()))
                    .toList();

            errors.forEach(err -> log.error("검증 실패 - 필드: {}, 메시지: {}", err.getField(), err.getMessage()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.error(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", errors));

        }


        ResUserDto resUserDto = authService.signup(reqSignupDto);


        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(HttpStatus.OK.value(), "CREATED", resUserDto));

    }
}
