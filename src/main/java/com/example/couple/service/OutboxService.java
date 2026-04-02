package com.example.couple.service;

import com.example.couple.entity.OutboxEvent;
import com.example.couple.enums.OutboxStatus;
import com.example.couple.exception.BadRequestException;
import com.example.couple.repository.OutboxRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;

    @Transactional
    public void markPublished(Long eventId){
        OutboxEvent event = outboxRepository.findById(eventId).orElseThrow(
                () -> new BadRequestException("Event bulunamadı")
        );
        event.setStatus(OutboxStatus.PUBLISHED);
        outboxRepository.save(event);
    }

    @Transactional
    public void markFailed(Long eventId, String error){
        OutboxEvent event = outboxRepository.findById(eventId).orElseThrow(
                () -> new BadRequestException("Event bulunamadı")
        );
        event.setStatus(OutboxStatus.PENDING);
        event.setRetryCount(event.getRetryCount()+1);
        event.setLastError(error);
        outboxRepository.save(event);
    }
}
