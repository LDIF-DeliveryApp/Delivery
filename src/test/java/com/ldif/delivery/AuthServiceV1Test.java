package com.ldif.delivery;

import com.ldif.delivery.auth.application.service.AuthServiceV1;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import com.ldif.delivery.user.domain.repository.UserRepository;
import com.ldif.delivery.user.presentation.dto.request.ReqSignupDto;
import com.ldif.delivery.user.presentation.dto.response.ResUserDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceV1Test {

    @InjectMocks
    private AuthServiceV1 authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Validator validator;
    private ReqSignupDto reqSignupDto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        reqSignupDto = new ReqSignupDto();
        reqSignupDto.setUsername("testuser");
        reqSignupDto.setPassword("Abcd123!@#"); // 유효한 비밀번호로 변경
        reqSignupDto.setNickname("tester");
        reqSignupDto.setEmail("test@example.com");
        reqSignupDto.setRole(UserRoleEnum.CUSTOMER);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() {
        // given
        given(userRepository.findByUsername(reqSignupDto.getUsername())).willReturn(Optional.empty());
        given(userRepository.findByEmail(reqSignupDto.getEmail())).willReturn(Optional.empty());
        given(passwordEncoder.encode(reqSignupDto.getPassword())).willReturn("encodedPassword");

        UserEntity savedUser = new UserEntity(
                reqSignupDto.getUsername(),
                reqSignupDto.getNickname(),
                reqSignupDto.getEmail(),
                "encodedPassword",
                reqSignupDto.getRole()
        );
        given(userRepository.save(any(UserEntity.class))).willReturn(savedUser);

        // when
        ResUserDto result = authService.signup(reqSignupDto);

        // then
        assertNotNull(result);
        assertEquals(reqSignupDto.getUsername(), result.getUsername());
        assertEquals(reqSignupDto.getNickname(), result.getNickname());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 username")
    void signup_Fail_DuplicateUsername() {
        // given
        given(userRepository.findByUsername(reqSignupDto.getUsername()))
                .willReturn(Optional.of(new UserEntity()));

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.signup(reqSignupDto);
        });
        assertEquals("중복된 사용자가 존재합니다.", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("비밀번호 검증 성공 - 최소 길이(8자) 및 조건 충족")
    void password_Validation_Success_MinLength() {
        // given
        reqSignupDto.setPassword("Abc12!@#");

        // when
        Set<ConstraintViolation<ReqSignupDto>> violations = validator.validate(reqSignupDto);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("비밀번호 검증 성공 - 최대 길이(15자) 및 조건 충족")
    void password_Validation_Success_MaxLength() {
        // given
        reqSignupDto.setPassword("Abcdefg1234!@#$");

        // when
        Set<ConstraintViolation<ReqSignupDto>> violations = validator.validate(reqSignupDto);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 7자 (길이 미달)")
    void password_Validation_Fail_Short() {
        // given
        reqSignupDto.setPassword("Ab1!345");

        // when
        Set<ConstraintViolation<ReqSignupDto>> violations = validator.validate(reqSignupDto);

        // then
        assertFalse(violations.isEmpty());
        assertEquals("비밀번호는 8~15자이며, 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다.",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 16자 (길이 초과)")
    void password_Validation_Fail_TooLong() {
        // given
        reqSignupDto.setPassword("Abcdefg1234!@#$5");

        // when
        Set<ConstraintViolation<ReqSignupDto>> violations = validator.validate(reqSignupDto);

        // then
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 대문자 누락")
    void password_Validation_Fail_NoUpperCase() {
        // given
        reqSignupDto.setPassword("abcd123!@#");

        // when
        Set<ConstraintViolation<ReqSignupDto>> violations = validator.validate(reqSignupDto);

        // then
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 소문자 누락")
    void password_Validation_Fail_NoLowerCase() {
        // given
        reqSignupDto.setPassword("ABCD123!@#");

        // when
        Set<ConstraintViolation<ReqSignupDto>> violations = validator.validate(reqSignupDto);

        // then
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 숫자 누락")
    void password_Validation_Fail_NoDigit() {
        // given
        reqSignupDto.setPassword("Abcdefg!@#");

        // when
        Set<ConstraintViolation<ReqSignupDto>> violations = validator.validate(reqSignupDto);

        // then
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 특수문자 누락")
    void password_Validation_Fail_NoSpecialChar() {
        // given
        reqSignupDto.setPassword("Abcd123456");

        // when
        Set<ConstraintViolation<ReqSignupDto>> violations = validator.validate(reqSignupDto);

        // then
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("비밀번호 검증 실패 - 공백 포함")
    void password_Validation_Fail_ContainsSpace() {
        // given
        reqSignupDto.setPassword("Abcd 123!@#");

        // when
        Set<ConstraintViolation<ReqSignupDto>> violations = validator.validate(reqSignupDto);

        // then
        assertFalse(violations.isEmpty());
    }
}
