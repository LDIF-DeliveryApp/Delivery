package com.ldif.delivery.address.service;

import com.ldif.delivery.address.dto.AddressRequestDto;
import com.ldif.delivery.address.dto.AddressResponseDto;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponseDto createAddress(UUID userId, AddressRequestDto dto);
    List<AddressResponseDto> getAddresses(UUID userId);
    AddressResponseDto getAddress(UUID userId, UUID addressId);
    AddressResponseDto updateAddress(UUID userId, UUID addressId, AddressRequestDto dto);
    void deleteAddress(UUID userId, UUID addressId);
    AddressResponseDto setDefaultAddress(UUID userId, UUID addressId);
}
