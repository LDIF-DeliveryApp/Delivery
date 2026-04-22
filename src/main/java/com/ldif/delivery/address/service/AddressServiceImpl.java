package com.ldif.delivery.address.service;

import com.ldif.delivery.address.dto.AddressRequestDto;
import com.ldif.delivery.address.dto.AddressResponseDto;
import com.ldif.delivery.address.entity.Address;
import com.ldif.delivery.address.exception.CustomException;
import com.ldif.delivery.address.exception.ErrorCode;
import com.ldif.delivery.address.repository.AddressRepository;
// import com.ldif.delivery.user.entity.User;            ← 팀원 User 패키지 맞게
// import com.ldif.delivery.user.repository.UserRepository; ← 팀원 UserRepository 패키지 맞게

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {
    private static final int MAX_ADDRESS_COUNT = 10;

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AddressResponseDto createAddress(UUID userId, AddressRequestDto dto) {

        User user = findUser(userId);

        // 주소 최대 개수 체크
        List<Address> existing = addressRepository.findAllByUserOrderByIsDefaultDescCreatedAtDesc(user);
        if (existing.size() >= MAX_ADDRESS_COUNT) {
            throw new CustomException(ErrorCode.ADDRESS_LIMIT_EXCEEDED);
        }

        // 첫 주소이거나 isDefault=true면 기존 기본 주소 해제
        if (dto.isDefault() || existing.isEmpty()) {
            addressRepository.findByUserAndIsDefaultTrue(user)
                    .ifPresent(a -> a.changeDefault(false));
        }

        Address address = Address.builder()
                .user(user)
                .alias(dto.getAlias())
                .recipientName(dto.getRecipientName())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .detailAddress(dto.getDetailAddress())
                .zipCode(dto.getZipCode())
                .isDefault(dto.isDefault() || existing.isEmpty()) // 첫 주소면 자동 기본
                .build();

        return AddressResponseDto.from(addressRepository.save(address));
    }

    @Override
    public List<AddressResponseDto> getAddresses(UUID userId) {
        User user = findUser(userId);
        return addressRepository.findAllByUserOrderByIsDefaultDescCreatedAtDesc(user)
                .stream()
                .map(AddressResponseDto::from)
                .toList();
    }

    @Override
    public AddressResponseDto getAddress(UUID userId, UUID addressId) {
        User user = findUser(userId);
        Address address = findAddress(addressId, user);
        return AddressResponseDto.from(address);
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(UUID userId, UUID addressId, AddressRequestDto dto) {
        User user = findUser(userId);
        Address address = findAddress(addressId, user);

        address.update(
                dto.getAlias(),
                dto.getRecipientName(),
                dto.getPhone(),
                dto.getAddress(),
                dto.getDetailAddress(),
                dto.getZipCode()
        );

        return AddressResponseDto.from(address);
    }
    @Override
    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        User user = findUser(userId);
        Address address = findAddress(addressId, user);

        // 기본 주소 삭제 시 다음 최신 주소를 기본으로 승격
        if (address.isDefault()) {
            addressRepository.findAllByUserOrderByIsDefaultDescCreatedAtDesc(user)
                    .stream()
                    .filter(a -> !a.getAddressId().equals(addressId))
                    .findFirst()
                    .ifPresent(a -> a.changeDefault(true));
        }

        addressRepository.delete(address);
    }
    @Override
    @Transactional
    public AddressResponseDto setDefaultAddress(UUID userId, UUID addressId) {
        User user = findUser(userId);

        // 기존 기본 주소 해제
        addressRepository.findByUserAndIsDefaultTrue(user)
                .ifPresent(a -> a.changeDefault(false));

        // 새 기본 주소 설정
        Address address = findAddress(addressId, user);
        address.changeDefault(true);

        return AddressResponseDto.from(address);
    }

    // ── private 헬퍼 ──────────────────────────────

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Address findAddress(UUID addressId, User user) {
        return addressRepository.findByAddressIdAndUser(addressId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));
    }
}
