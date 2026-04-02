package com.example.couple.dto.response;

public record UserLoginResponse(
        Long id,
        String username,
        String email,
        String friendCode,
        String accessToken,
        String refreshToken
) {
}