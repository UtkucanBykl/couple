package com.example.couple.mapper;

import com.example.couple.dto.response.PagedResponse;
import org.springframework.data.domain.Page;

public final class PageResponseMapper {
    private PageResponseMapper(){}
    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}