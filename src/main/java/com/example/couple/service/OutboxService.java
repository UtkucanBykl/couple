package com.example.couple.service;

import com.example.couple.entity.OutboxEvent;
import com.example.couple.enums.OutboxAggregateType;
import com.example.couple.enums.OutboxEventType;
import com.example.couple.exception.NotFoundException;
import com.example.couple.repository.OutboxRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    private OutboxEvent getOutboxEvent(Long eventId) {
    return outboxRepository
        .findById(eventId)
        .orElseThrow(() -> new NotFoundException("Event bulunamadı"));
    }

    @Transactional
    public void markPublished(Long eventId){
        OutboxEvent event = this.getOutboxEvent(eventId);
        event.markPublished();
    }

    @Transactional
    public void markFailed(Long eventId, String error){
        OutboxEvent event = this.getOutboxEvent(eventId);
        event.markFailed(error);
    }

    @Transactional
    public void save(
            OutboxEventType eventType,
            OutboxAggregateType aggregateType,
            Long aggregateId,
            Object payload,
            String exchangeName,
            String routingKey
    ) {
        OutboxEvent outboxEvent = OutboxEvent.createPending(
                eventType,
                aggregateType,
                aggregateId,
                objectMapper.valueToTree(payload),
                exchangeName,
                routingKey
        );

        outboxRepository.save(outboxEvent);
    }
}
