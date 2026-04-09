package com.example.couple.consumer;

import com.example.couple.config.RabbitConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DeadLetterConsumer {
  private final RabbitTemplate rabbitTemplate;

  @RabbitListener(queues = RabbitConfig.DEADLETTER_QUEUE)
  public void consume(Message message) {
    long retryCount = message.getMessageProperties().getRetryCount();
    String originalExchange =
            (String) message.getMessageProperties().getHeaders().get("original-exchange");

    String originalRoutingKey =
            (String) message.getMessageProperties().getHeaders().get("original-routing-key");

    if(retryCount>3){
      log.info("{} - {} retry count 3", originalExchange, originalRoutingKey);
      return;
    }


    if (originalExchange == null || originalRoutingKey == null) {
      log.error("Original exchange/routing key not found");
      return;
    }

    message.getMessageProperties().incrementRetryCount();
    rabbitTemplate.send(originalExchange, originalRoutingKey, message);
  }
}
