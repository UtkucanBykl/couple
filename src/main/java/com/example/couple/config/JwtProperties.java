package com.example.couple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.security.jwt")
public record JwtProperties(
        String secretKey,
        Long expiration,
        RefreshToken refreshToken
) {
    public record RefreshToken(
            Long expiration
    ) {}
}