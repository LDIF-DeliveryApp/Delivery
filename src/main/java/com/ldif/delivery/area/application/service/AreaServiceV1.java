package com.ldif.delivery.area.application.service;

import com.ldif.delivery.area.domain.entity.AreaEntity;
import com.ldif.delivery.area.domain.repository.AreaRepository;
import com.ldif.delivery.area.persentation.dto.AreaRequest;
import com.ldif.delivery.area.persentation.dto.AreaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class AreaServiceV1 {

    private final AreaRepository areaRepository;

    @Transactional
    public AreaResponse setArea(@Valid AreaRequest request) {
        AreaEntity areaEntity = new AreaEntity(request);
        areaRepository.save(areaEntity);
        return new AreaResponse(areaEntity);
    }

    public Page<AreaResponse> getAreas(String keyword, int page, int size, String sort) {
        Sort.Direction direction = sort.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort SortBy = Sort.by(direction, "createdAt");
        Pageable pageable = PageRequest.of(page, size, SortBy);

        Page<AreaEntity> pagelist = areaRepository.findByNameContainingAndIsDeletedFalse(keyword, pageable);

        return pagelist.map(AreaResponse::new);
    }
}
