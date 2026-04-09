package com.example.couple.mapper;

import com.example.couple.dto.response.DayWriteResponse;
import com.example.couple.entity.Day;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DayMapper {
    DayWriteResponse toResponse(Day day);
}
