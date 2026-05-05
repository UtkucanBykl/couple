package com.example.couple.entity;

import com.example.couple.enums.InviteStatus;
import com.example.couple.exception.DomainException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class CoupleInvite extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "sender_user_id", nullable = false)
  private User senderUser;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "receiver_user_id", nullable = false)
  private User receiverUser;

  @Enumerated(EnumType.STRING)
  private InviteStatus status;

  @Version
  private Long version;

  private CoupleInvite(User senderUser, User receiverUser, InviteStatus status) {
    this.senderUser = senderUser;
    this.receiverUser = receiverUser;
    this.status = status;
  }

  public static CoupleInvite createPending(User senderUser, User receiverUser) {
    if (senderUser == null || receiverUser == null) {
      throw new DomainException("Kullanıcılar boş olamaz");
    }
    if (senderUser.equals(receiverUser)) {
      throw new DomainException("Kullanıcılar aynı olamaz");
    }
    return new CoupleInvite(senderUser, receiverUser, InviteStatus.PENDING);
  }

  public void accept() {
    if (this.status != InviteStatus.PENDING) {
      throw new DomainException("Sadece bekleyen davet kabul edilebilir");
    }
    this.status = InviteStatus.ACCEPTED;
  }

  public void reject() {
    if (this.status != InviteStatus.PENDING) {
      throw new DomainException("Sadece bekleyen davet reddedilebilir");
    }
    this.status = InviteStatus.REJECTED;
  }
}
