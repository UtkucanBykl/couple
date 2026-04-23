package com.example.couple.entity;

import com.example.couple.enums.OutboxAggregateType;
import com.example.couple.enums.OutboxEventType;
import com.example.couple.enums.OutboxStatus;
import com.example.couple.exception.DomainException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class OutboxEvent extends BaseEntity {
  @Enumerated(EnumType.STRING)
  private OutboxAggregateType aggregateType;

  private Long aggregateId;

  @Enumerated(EnumType.STRING)
  private OutboxEventType eventType;

  @Enumerated(EnumType.STRING)
  private OutboxStatus status;

  @Column(nullable = false)
  private String exchangeName;

  @Column(nullable = false)
  private String routingKey;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false, columnDefinition = "jsonb")
  private JsonNode payload;

  private String lastError;

  private int retryCount = 0;

  private OutboxEvent(
      OutboxEventType eventType,
      OutboxAggregateType aggregateType,
      Long aggregateId,
      JsonNode payload,
      String exchangeName,
      String routingKey) {
    if (eventType == null) {
      throw new DomainException("Outbox event type boş olamaz");
    }
    if (aggregateType == null) {
      throw new DomainException("Outbox aggregate type boş olamaz");
    }
    if (aggregateId == null) {
      throw new DomainException("Outbox aggregate id boş olamaz");
    }
    if (payload == null) {
      throw new DomainException("Outbox payload boş olamaz");
    }
    if (exchangeName == null || exchangeName.isBlank()) {
      throw new DomainException("Exchange name boş olamaz");
    }
    if (routingKey == null || routingKey.isBlank()) {
      throw new DomainException("Routing key boş olamaz");
    }

    this.eventType = eventType;
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    this.payload = payload;
    this.exchangeName = exchangeName;
    this.routingKey = routingKey;
    this.status = OutboxStatus.PENDING;
    this.retryCount = 0;
  }

  public static OutboxEvent createPending(
      OutboxEventType eventType,
      OutboxAggregateType aggregateType,
      Long aggregateId,
      JsonNode payload,
      String exchangeName,
      String routingKey) {
    return new OutboxEvent(
        eventType, aggregateType, aggregateId, payload, exchangeName, routingKey);
  }

  public void markSent() {
    this.status = OutboxStatus.PUBLISHED;
    this.lastError = null;
  }

  public void markFailed(String errorMessage) {
    this.status = OutboxStatus.PENDING;
    this.retryCount++;
    this.lastError = errorMessage;
  }

  public void markDead(String errorMessage) {
    this.status = OutboxStatus.FAILED;
    this.retryCount++;
    this.lastError = errorMessage;
  }

  public boolean isPending() {
    return this.status == OutboxStatus.PENDING;
  }
}
