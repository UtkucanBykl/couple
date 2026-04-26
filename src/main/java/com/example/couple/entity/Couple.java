package com.example.couple.entity;

import com.example.couple.enums.CoupleStatus;
import com.example.couple.exception.DomainException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "couples",
    indexes = {
      @Index(name = "couple_search_index", columnList = "first_user_id, second_user_id, status")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

  @OneToMany(mappedBy = "couple", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Day> days = new ArrayList<>();

  private LocalDate anniversaryDate;

  private Couple(User firstUser, User secondUser) {
    this.firstUser = firstUser;
    this.secondUser = secondUser;
    this.status = CoupleStatus.ACTIVE;
  }

  public static Couple createActive(User firstUser, User secondUser) {
    if (firstUser == null) {
      throw new DomainException("Kullanıcı boş bırakılamaz");
    }
    if (secondUser == null) {
      throw new DomainException("Kullanıcı boş bırakılamaz");
    }
    if (firstUser.getId().equals(secondUser.getId())) {
      throw new DomainException("Kullanıcılar farklı olmalı");
    }
    return new Couple(firstUser, secondUser);
  }

  public void attachActiveCoupleToUsers() {
    requireFirstUser().attachActiveCouple(this);
    requireSecondUser().attachActiveCouple(this);
  }

  private User requireFirstUser() {
    if (firstUser == null) {
      throw new DomainException("Kullanıcı boş bırakılamaz");
    }
    return firstUser;
  }

  private User requireSecondUser() {
    if (secondUser == null) {
      throw new DomainException("Kullanıcı boş bırakılamaz");
    }
    return secondUser;
  }

  public void detachFromUsers() {
    requireFirstUser().detachActiveCouple(this);
    requireSecondUser().detachActiveCouple(this);
  }

}
