package com.ldif.delivery.address.service;

import com.ldif.delivery.address.dto.AddressRequestDto;
import com.ldif.delivery.address.dto.AddressResponseDto;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponseDto createAddress(String username, AddressRequestDto dto);
    List<AddressResponseDto> getAddresses(String username);
    AddressResponseDto getAddress(String username, UUID addressId);
    AddressResponseDto updateAddress(String username, UUID addressId, AddressRequestDto dto);
    void deleteAddress(String username, UUID addressId);
    AddressResponseDto setDefaultAddress(String username, UUID addressId);
}
