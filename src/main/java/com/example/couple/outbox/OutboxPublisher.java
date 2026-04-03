package com.example.couple.outbox;

import com.example.couple.entity.OutboxEvent;
import com.example.couple.enums.OutboxStatus;
import com.example.couple.repository.OutboxRepository;
import com.example.couple.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {
  private final OutboxRepository outboxRepository;
  private final RabbitTemplate rabbitTemplate;
  private final OutboxService outboxService;

  @Scheduled(fixedDelay = 5000)
  public void publishPendingEvents() {
    List<OutboxEvent> outboxEvents =
        outboxRepository.findTop100ByStatusOrderByIdAsc(OutboxStatus.PENDING);

    for (OutboxEvent event : outboxEvents) {
      try {
        rabbitTemplate.convertAndSend(
            event.getExchangeName(),
            event.getRoutingKey(),
            event.getPayload(),
            message -> {
              message.getMessageProperties().setMessageId(String.valueOf(event.getId()));
              message
                  .getMessageProperties()
                  .setHeader("original-exchange", event.getExchangeName());
              message
                  .getMessageProperties()
                  .setHeader("original-routing-key", event.getRoutingKey());
              return message;
            });
        outboxService.markPublished(event.getId());
      } catch (Exception ex) {
        outboxService.markFailed(event.getId(), ex.toString());
      }
    }
  }
}
