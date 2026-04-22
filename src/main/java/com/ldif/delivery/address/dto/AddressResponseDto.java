package com.ldif.delivery.address.dto;

import com.ldif.delivery.address.entity.Address;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AddressResponseDto {

    private UUID addressId;
    private String alias;
    private String recipientName;
    private String phone;
    private String address;
    private String detailAddress;
    private String zipCode;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AddressResponseDto from(Address address){
        return AddressResponseDto.builder()
                .addressId(address.getAddressId())
                .alias(address.getAlias())
                .recipientName(address.getRecipientName())
                .phone(address.getPhone())
                .address(address.getAddress())
                .detailAddress(address.getDetailAddress())
                .zipCode(address.getZipCode())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }

}
