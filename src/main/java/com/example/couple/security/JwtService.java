package com.example.couple.security;

import com.example.couple.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class JwtService {

  private final JwtProperties jwtProperties;

  public String getSecretKey() {
    return jwtProperties.secretKey();
  }

  public Long getExpiration() {
    return jwtProperties.expiration();
  }

  public Long getRefreshExpiration() {
    return jwtProperties.refreshToken().expiration();
  }

  public String generateToken(String username) {
    return buildToken(new HashMap<>(), username, getExpiration());
  }

  public String generateRefreshToken(String username) {
    return buildToken(new HashMap<>(), username, getRefreshExpiration());
  }

  private String buildToken(Map<String, Object> extraClaims, String username, long expiration) {
    return Jwts.builder()
        .claims(extraClaims)
        .subject(username)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignInKey())
        .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public boolean isTokenValid(String token, String username) {
    final String extractedUsername = extractUsername(token);
    return (extractedUsername.equals(username)) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(getSecretKey());
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
