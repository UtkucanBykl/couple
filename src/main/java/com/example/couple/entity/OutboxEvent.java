package com.example.couple.entity;

import com.example.couple.enums.OutboxAggregateType;
import com.example.couple.enums.OutboxEventType;
import com.example.couple.enums.OutboxStatus;
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
}
