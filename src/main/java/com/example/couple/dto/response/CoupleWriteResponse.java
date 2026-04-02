package com.example.couple.dto.response;

public record CoupleWriteResponse(
        Long id,
        UserBasicResponse secondUser
) {
}