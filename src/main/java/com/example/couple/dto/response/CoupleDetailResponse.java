package com.example.couple.dto.response;

import java.time.LocalDateTime;

public record CoupleDetailResponse(
        Long id,
        UserBasicResponse user,
        LocalDateTime createdAt

) {
}
