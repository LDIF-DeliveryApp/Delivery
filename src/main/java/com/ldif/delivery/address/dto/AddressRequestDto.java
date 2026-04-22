package com.ldif.delivery.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddressRequestDto {

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    private String detailAddress;

    @Pattern(regexp = "\\d{5}", message = "우편번호는 5자리 숫자여야 합니다.")
    private String zipCode;

    @Size(max = 20, message = "별칭은 20자 이하여야 합니다")
    private String alias;

    @Size(max = 50, message = "받는 사람 이름은 50자 이하여야 합니다.")
    private String recipientName;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;

    private boolean isDefault;
}
