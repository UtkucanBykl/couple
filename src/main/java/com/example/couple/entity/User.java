package com.example.couple.entity;

import com.example.couple.exception.DomainException;
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

  public boolean hasActiveCouple(){
    return this.activeCouple != null;
  }

  public void attachActiveCouple(Couple couple) {
    if (couple == null) {
      throw new DomainException("Couple boş olamaz");
    }
    if (this.activeCouple != null) {
      throw new DomainException("Kullanıcının aktif couple var");
    }
    this.activeCouple = couple;
  }

  public void detachActiveCouple(Couple couple) {
    if (couple == null) {
      throw new DomainException("Couple boş olamaz");
    }
    if (this.activeCouple == null) {
      throw new DomainException("Kullanıcının aktif couple yok");
    }

    if(this.getActiveCouple().getId().equals(couple.getId())){
      throw new DomainException("Kullanıcının aktif couple farklı");
    }
    this.activeCouple = null;
  }
}
