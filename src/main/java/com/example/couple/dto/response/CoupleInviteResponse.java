package com.example.couple.dto.response;

import com.example.couple.enums.InviteStatus;


public record CoupleInviteResponse(
        UserBasicResponse senderUser,
        UserBasicResponse receiverUser,
        InviteStatus status
) {}
