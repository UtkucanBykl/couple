package com.example.couple.dto.response;

public record UserBasicResponse(
        Long id,
        String username,
        String email
) {
}