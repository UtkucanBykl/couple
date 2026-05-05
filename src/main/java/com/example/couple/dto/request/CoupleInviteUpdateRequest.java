package com.example.couple.dto.request;


import com.example.couple.enums.InviteStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoupleInviteUpdateRequest {
    @NotNull
    InviteStatus status;
}
