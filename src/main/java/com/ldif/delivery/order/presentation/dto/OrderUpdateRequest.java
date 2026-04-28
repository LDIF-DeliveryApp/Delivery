package com.ldif.delivery.order.presentation.dto;

import jakarta.validation.constraints.Size;

public record OrderUpdateRequest(

        // null 허용 (요청사항 삭제 가능), 길이만 제한
        @Size(max = 500, message = "요청사항은 500자 이내로 입력해 주세요.")
        String request
) {
}
