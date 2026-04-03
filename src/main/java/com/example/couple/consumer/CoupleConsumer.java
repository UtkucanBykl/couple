package com.example.couple.consumer;

import com.example.couple.config.RabbitConfig;
import com.example.couple.dto.event.CoupleCreatedNotificationEvent;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoupleConsumer {
  @RabbitListener(queues = RabbitConfig.COUPLE_QUEUE)
  public void coupleQueue(CoupleCreatedNotificationEvent event) {
    // Will add notification email etc
    log.info("RabbitMQ queue. event={}", event);
  }
}
