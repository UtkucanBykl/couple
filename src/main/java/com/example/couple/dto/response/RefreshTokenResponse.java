package com.example.couple.dto.response;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
