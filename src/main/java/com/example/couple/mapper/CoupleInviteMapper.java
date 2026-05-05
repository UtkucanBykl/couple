package com.example.couple.mapper;

import com.example.couple.dto.response.CoupleInviteResponse;
import com.example.couple.entity.CoupleInvite;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoupleInviteMapper {
    CoupleInviteResponse toResponse(CoupleInvite coupleInvite);
}
