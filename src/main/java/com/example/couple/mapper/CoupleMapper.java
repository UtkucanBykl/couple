package com.example.couple.mapper;

import com.example.couple.dto.response.CoupleDetailResponse;
import com.example.couple.dto.response.CoupleWriteResponse;
import com.example.couple.dto.response.UserBasicResponse;
import com.example.couple.entity.Couple;
import com.example.couple.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CoupleMapper {
    public CoupleWriteResponse toCreateResponse(Couple couple){
        UserBasicResponse userBasicResponse = new UserBasicResponse(
                couple.getSecondUser().getId(),
                couple.getSecondUser().getUsername(),
                couple.getSecondUser().getEmail()
        );
        return new CoupleWriteResponse(
                couple.getId(),
                userBasicResponse
        );
    }

    public CoupleDetailResponse toDetailResponse(Couple couple, User user){
        UserBasicResponse userBasicResponse = new UserBasicResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
        return new CoupleDetailResponse(
                couple.getId(),
                userBasicResponse,
                couple.getCreatedAt()
        );
    }
}
