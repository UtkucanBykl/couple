package com.example.couple.dto.response;

public record UserCreateResponse(
        Long id,
        String username,
        String email,
        String friendCode,
        String accessToken,
        String refreshToken
) {
}