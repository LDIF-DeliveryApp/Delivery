package com.ldif.delivery.area.domain.entity;

import com.ldif.delivery.area.persentation.dto.AreaRequest;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import com.ldif.delivery.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_area")
@NoArgsConstructor
public class AreaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID areaId;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String district;

    @Column(nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @Column(nullable = false)
    private Boolean isDeleted = Boolean.FALSE;

    public AreaEntity(AreaRequest request) {
        this.name = request.getName();
        this.city = request.getCity();
        this.district = request.getDistrict();
    }

    public void update(@Valid AreaRequest areaRequest) {

    }

    public void delete(UserDetailsImpl loginUser) {
        this.isDeleted = Boolean.TRUE;
        super.softDelete(loginUser.getUsername());
    }
}
