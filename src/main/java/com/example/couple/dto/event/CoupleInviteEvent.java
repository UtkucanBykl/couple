package com.example.couple.dto.event;

import java.time.Instant;

public record CoupleInviteEvent(
        Long inviteId,
        Long senderUserId,
        Long receiverUserId,
        Instant occurredAt
) {}
