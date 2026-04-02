package com.example.couple.dto.event;


public record CoupleCreatedNotificationEvent(
        Long id,
        Long userId
) {
}
