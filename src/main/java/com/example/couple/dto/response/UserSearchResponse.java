package com.example.couple.dto.response;

public record UserSearchResponse(
        String username,
        String email,
        String friendCode
) {}
