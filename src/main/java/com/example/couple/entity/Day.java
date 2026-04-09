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

  public static Day create(
      Couple couple, String name, String description, LocalDate date, String coverPhoto) {
    if (couple == null) {
      throw new IllegalArgumentException("Bir gün (Day) mutlaka bir çifte (Couple) ait olmalıdır.");
    }
    if (name == null || name.trim().length() < 3) {
      throw new IllegalArgumentException("Gün adı en az 3 karakter olmalıdır.");
    }
    if (date == null) {
      throw new IllegalArgumentException("Tarih boş olamaz.");
    }
    Day day = new Day();
    day.setCouple(couple);
    day.setName(name);
    day.setCoverPhoto(coverPhoto);
    day.setDate(date);
    day.setDescription(description);
    return day;
  }
}
