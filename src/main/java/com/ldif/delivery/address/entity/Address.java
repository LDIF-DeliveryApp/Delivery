package com.ldif.delivery.address.entity;

import com.ldif.delivery.user.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.ldif.delivery.global.infrastructure.entity.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id", columnDefinition = "BINARY(16)")
    private UUID addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "alias", length = 20)
    private String alias;

    /*@Column(name = "recipient_name", length =50)
    private String recipientName;

    @Column(name = "phone", length= 20)
    private String phone;*/

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Column(name = "detail_address", length =255, nullable = false)
    private String detailAddress;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "is_default")
    private boolean isDefault = false;

    @Builder
    public Address(UserEntity user, String alias, String recipientName,
                   String phone, String address, String detailAddress,
                   String zipCode, boolean isDefault){
        this.user = user;
        this.alias = alias;
        /*this.recipientName = recipientName;
        this.phone = phone;*/
        this.address = address;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
        this.isDefault = isDefault;
    }
    // 기본 주소 설정
    public void changeDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    // 주소 수정
    public void update(String alias, String recipientName, String phone,
                       String address, String detailAddress, String zipCode) {
        this.alias = alias;
        /*this.recipientName = recipientName;
        this.phone = phone;*/
        this.address = address;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
    }

}
