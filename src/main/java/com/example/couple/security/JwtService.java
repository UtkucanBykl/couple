package com.example.couple.security;

import com.example.couple.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class JwtService {

  private static final String TOKEN_TYPE_CLAIM = "token_type";
  private static final String TOKEN_TYPE_ACCESS = "access";
  private static final String TOKEN_TYPE_REFRESH = "refresh";
  private static final String JTI_CLAIM = "jti";

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
    Map<String, Object> claims = new HashMap<>();
    claims.put(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS);
    claims.put(JTI_CLAIM, UUID.randomUUID().toString());
    return buildToken(claims, username, getExpiration());
  }

  public String generateRefreshToken(String username) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(TOKEN_TYPE_CLAIM, TOKEN_TYPE_REFRESH);
    claims.put(JTI_CLAIM, UUID.randomUUID().toString());
    return buildToken(claims, username, getRefreshExpiration());
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

  public String extractTokenType(String token) {
    return extractAllClaims(token).get(TOKEN_TYPE_CLAIM, String.class);
  }

  public String extractJti(String token) {
    return extractAllClaims(token).get(JTI_CLAIM, String.class);
  }

  public boolean isAccessToken(String token) {
    return TOKEN_TYPE_ACCESS.equals(extractTokenType(token));
  }

  public boolean isRefreshToken(String token) {
    return TOKEN_TYPE_REFRESH.equals(extractTokenType(token));
  }

  public boolean isTokenValid(String token, String username) {
    try {
      final String extractedUsername = extractUsername(token);
      return extractedUsername.equals(username) && !isTokenExpired(token);
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public boolean isTokenValid(String token) {
    try {
      extractAllClaims(token);
      return !isTokenExpired(token);
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public boolean isRefreshTokenValid(String token) {
    try {
      return isTokenValid(token) && isRefreshToken(token);
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public boolean isAccessTokenValid(String token){
    try {
      return isTokenValid(token) && isAccessToken(token);
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
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
