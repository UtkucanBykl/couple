package com.example.couple.mapper;

import com.example.couple.dto.response.RefreshTokenResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    RefreshTokenResponse toResponse(String accessToken, String refreshToken);
}
