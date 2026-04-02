package com.example.couple.entity;

import com.example.couple.enums.CoupleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
    name = "couples",
    indexes = {
      @Index(name = "couple_search_index", columnList = "first_user_id, second_user_id, status")
    })
@Getter
@Setter
@NoArgsConstructor
public class Couple extends BaseEntity {
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private CoupleStatus status = CoupleStatus.ACTIVE;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "first_user_id", nullable = false)
  private User firstUser;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "second_user_id", nullable = false)
  private User secondUser;

  private LocalDate anniversaryDate;
}
