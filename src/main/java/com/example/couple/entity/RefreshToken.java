package com.example.couple.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, unique = true, length = 512)
    private String hashToken;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    private RefreshToken(String hashToken, User user, Instant expiresAt) {
        this.hashToken = hashToken;
        this.user = user;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    public static RefreshToken create(String hashToken, User user, Instant expiresAt) {
        return new RefreshToken(hashToken, user, expiresAt);
    }

    public void revoke() {
        this.revoked = true;
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }
}