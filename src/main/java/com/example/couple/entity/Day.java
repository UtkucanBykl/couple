package com.example.couple.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "days")
@Getter
@Setter
@NoArgsConstructor
public class Day extends BaseEntity {
  @NotNull
  @NotBlank
  @Size(min = 3)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "couple_id", nullable = false)
  private Couple couple;

  @NotNull private String description;

  private String coverPhoto;

  @NotNull private LocalDate date;
}
