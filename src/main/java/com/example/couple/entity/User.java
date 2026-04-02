package com.example.couple.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {
  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String passwordHash;

  @Column(nullable = false)
  private String friendCode;

  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "active_couple_id", nullable = true)
  private Couple activeCouple = null;
}
