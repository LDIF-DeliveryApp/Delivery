package com.ldif.delivery.global.infrastructure.presentation.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonPropertyOrder({ "content", "page", "size", "totalElements", "totalPages", "sort" })
public class PageResponseDto<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final String sort;

    public PageResponseDto(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.sort = page.getSort().stream()
                .map(order -> order.getProperty() + "," + order.getDirection())
                .collect(Collectors.joining("; "));
    }
}
