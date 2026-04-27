package com.ldif.delivery.area.application.service;

import com.ldif.delivery.area.domain.entity.AreaEntity;
import com.ldif.delivery.area.domain.repository.AreaRepository;
import com.ldif.delivery.area.persentation.dto.AreaRequest;
import com.ldif.delivery.area.persentation.dto.AreaResponse;
import com.ldif.delivery.global.infrastructure.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class AreaServiceV1 {

    private final AreaRepository areaRepository;

    //지역 생성
    @Transactional
    public AreaResponse setArea(@Valid AreaRequest request) {
        AreaEntity areaEntity = new AreaEntity(request);
        areaRepository.save(areaEntity);
        return new AreaResponse(areaEntity);
    }

    //지역 조회
    public Page<AreaResponse> getAreas(String keyword, int page, int size, String sort) {
        Sort.Direction direction = sort.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort SortBy = Sort.by(direction, "createdAt");
        List<Integer> allowedSize = Arrays.asList(10, 30, 50);
        int setSize = allowedSize.contains(size) ? size : 10;
        Pageable pageable = PageRequest.of(page, setSize, SortBy);

        Page<AreaEntity> pagelist = areaRepository.findByNameContainingAndIsDeletedFalse(keyword, pageable);

        return pagelist.map(AreaResponse::new);
    }

    //지역 상세 조회
    public AreaResponse getArea(UUID areaId) {
        AreaEntity areaEntity = findAreaById(areaId);
        return new AreaResponse(areaEntity);
    }

    //지역 수정
    public AreaResponse updateArea(UUID areaId, @Valid AreaRequest request) {
        AreaEntity areaEntity = findAreaById(areaId);
        areaEntity.update(request);
        return new AreaResponse(areaEntity);
    }

    //지역 삭제
    public void deleteArea(UUID areaId, UserDetailsImpl loginUser) {
        AreaEntity areaEntity = findAreaById(areaId);
        areaEntity.delete(loginUser);
    }

    // 지역 활성화/비활성화
    public AreaResponse toggleActive(UUID areaId) {
        AreaEntity areaEntity = findAreaById(areaId);
        areaEntity.toggleActive();
        return new AreaResponse(areaEntity);
    }

    //지역 찾기
    private AreaEntity findAreaById(UUID id) {
        AreaEntity areaEntity = areaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("지역 없음." + id));
        if (areaEntity.getIsDeleted()) {
            throw new IllegalArgumentException("지역 없음." + id);
        }
        return areaEntity;
    }

}
