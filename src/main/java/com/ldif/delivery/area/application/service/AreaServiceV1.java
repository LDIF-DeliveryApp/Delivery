package com.ldif.delivery.area.application.service;

import com.ldif.delivery.area.domain.repository.AreaRepository;
import com.ldif.delivery.area.persentation.dto.AreaRequest;
import com.ldif.delivery.area.persentation.dto.AreaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class AreaServiceV1 {

    private final AreaRepository areaRepository;

    public AreaResponse setArea(@Valid AreaRequest request) {


        return new AreaResponse();
    }
}
