package com.ldif.delivery.address.controller;

import com.ldif.delivery.address.dto.AddressRequestDto;
import com.ldif.delivery.address.dto.AddressResponseDto;
import com.ldif.delivery.address.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.global.infrastructure.presentation.dto.CommonResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // 주소 등록
    @PostMapping
    public ResponseEntity<CommonResponse<AddressResponseDto>> createAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid AddressRequestDto dto) {

        AddressResponseDto response = addressService.createAddress(userDetails.getUsername(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(HttpStatus.CREATED.value(), "SUCCESS", response));
    }
    // 내 주소 전체 조회
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAddresses(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<AddressResponseDto> response = addressService.getAddresses(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
    // 주소 단건 조회
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID addressId) {

        AddressResponseDto response = addressService.getAddress(userDetails.getUsername(), addressId);
        return ResponseEntity.ok(response);
    }
    // 주소 수정
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID addressId,
            @RequestBody @Valid AddressRequestDto dto) {

        AddressResponseDto response = addressService.updateAddress(userDetails.getUsername(), addressId, dto);
        return ResponseEntity.ok(response);
    }
    // 주소 삭제
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID addressId) {

        addressService.deleteAddress(userDetails.getUsername(), addressId);
        return ResponseEntity.noContent().build();
    }
    // 기본 주소 설정
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponseDto> setDefaultAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID addressId) {

        AddressResponseDto response = addressService.setDefaultAddress(userDetails.getUsername(), addressId);
        return ResponseEntity.ok(response);
    }
}
