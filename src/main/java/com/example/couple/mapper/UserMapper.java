package com.example.couple.mapper;

import com.example.couple.dto.response.UserCreateResponse;
import com.example.couple.dto.response.UserLoginResponse;
import com.example.couple.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserCreateResponse toCreateResponse(User user, String accessToken, String refershToken){
        return new UserCreateResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFriendCode(),
                accessToken,
                refershToken
        );
    }

    public UserLoginResponse toLoginResponse(User user, String accessToken, String refershToken){
        return new UserLoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFriendCode(),
                accessToken,
                refershToken
        );
    }}
