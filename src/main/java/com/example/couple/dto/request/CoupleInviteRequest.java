package com.example.couple.dto.request;

import com.example.couple.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CoupleInviteRequest {
    @NotNull
    Long receiverUserId;
}
