package com.example.couple.mapper;

import com.example.couple.dto.response.GlobalErrorResponse;
import org.springframework.stereotype.Component;


@Component
public class GlobalErrorMapper {
    public GlobalErrorResponse toResponse(String error){
        return new GlobalErrorResponse(
                error
        );
    }
}
