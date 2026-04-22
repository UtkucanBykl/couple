package com.example.couple.repository;


import com.example.couple.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  @EntityGraph(attributePaths = {"user"})
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("""
    select rt
    from RefreshToken rt
    where rt.hashToken = :hashToken
      and rt.revoked = false
      and rt.expiresAt > :now
""")
  Optional<RefreshToken> findActiveByHashTokenForUpdate(String hashToken, Instant now);
}
