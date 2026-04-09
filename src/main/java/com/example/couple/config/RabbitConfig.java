package com.example.couple.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
public class RabbitConfig {

  public static final String COUPLE_EXCHANGE = "couple.exchange";
  public static final String COUPLE_QUEUE = "couple.queue";
  public static final String COUPLE_CREATED_ROUTING_KEY = "couple.#";
  public static final String DEADLETTER_EXCHANGE = "couple.dlx";
  public static final String DEADLETTER_ROUTING_KEY = "couple.dlq";
  public static final String DEADLETTER_QUEUE = "couple.dl.queue";

  @Bean
  public TopicExchange coupleExchange() {
    return new TopicExchange(COUPLE_EXCHANGE, true, false);
  }

  @Bean
  public Queue coupleQueue() {
    return QueueBuilder.durable(COUPLE_QUEUE)
        .deadLetterExchange(DEADLETTER_EXCHANGE)
        .deadLetterRoutingKey(DEADLETTER_ROUTING_KEY)
        .build();
  }

  @Bean
  public Queue deadLetterQueue() {
    return QueueBuilder.durable(DEADLETTER_QUEUE).build();
  }

  @Bean
  public DirectExchange deadLetterExchange() {
    return new DirectExchange(DEADLETTER_EXCHANGE, true, false);
  }

  @Bean
  public Binding deadLetterBinding(
      @Qualifier("deadLetterQueue") Queue deadLetterQueue,
      @Qualifier("deadLetterExchange") DirectExchange deadLetterExchange) {
    return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DEADLETTER_ROUTING_KEY);
  }

  @Bean
  public Binding coupleCreatedBinding(
      @Qualifier("coupleQueue") Queue coupleQueue,
      @Qualifier("coupleExchange") TopicExchange coupleExchange) {
    return BindingBuilder.bind(coupleQueue).to(coupleExchange).with(COUPLE_CREATED_ROUTING_KEY);
  }

  @Bean
  public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
  }

  @Bean
  public ApplicationRunner rabbitInitRunner(AmqpAdmin amqpAdmin) {
    return args -> {
      if (amqpAdmin instanceof RabbitAdmin rabbitAdmin) {
        rabbitAdmin.initialize();
      }
    };
  }
}
