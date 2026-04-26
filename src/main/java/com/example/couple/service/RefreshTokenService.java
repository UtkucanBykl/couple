package com.example.couple.service;

import com.example.couple.config.JwtProperties;
import com.example.couple.dto.request.RefreshTokenRequest;
import com.example.couple.dto.response.RefreshTokenResponse;
import com.example.couple.entity.RefreshToken;
import com.example.couple.entity.User;
import com.example.couple.exception.BadRequestException;
import com.example.couple.mapper.RefreshTokenMapper;
import com.example.couple.repository.RefreshTokenRepository;
import com.example.couple.security.JwtService;
import com.example.couple.util.TokenHashUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtService jwtService;
  private final RefreshTokenMapper refreshTokenMapper;
  private final JwtProperties jwtProperties;

  @Transactional
  public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest){
    if(!jwtService.isRefreshTokenValid(refreshTokenRequest.getRefreshToken())){
      throw new BadRequestException("Token geçerli değil");
    }
    Instant now = Instant.now();
    String hashToken = TokenHashUtil.sha256(refreshTokenRequest.getRefreshToken());
    RefreshToken token = refreshTokenRepository.findActiveByHashTokenForUpdate(
            hashToken, now
    ).orElseThrow(() -> new BadRequestException("Token yok"));
    String accessToken = jwtService.generateToken(token.getUser().getUsername());
    String refreshToken = jwtService.generateRefreshToken(token.getUser().getUsername());
    RefreshToken newRefreshToken = RefreshToken.create(
            TokenHashUtil.sha256(refreshToken),
            token.getUser(),
            now.plusMillis(jwtProperties.refreshToken().expiration())
    );
    refreshTokenRepository.save(newRefreshToken);
    token.revoke();
    return refreshTokenMapper.toResponse(accessToken, refreshToken);
  }

  @Transactional
  public RefreshTokenResponse issueTokens(User user) {
    String accessToken = jwtService.generateToken(user.getUsername());
    String rawRefreshToken = jwtService.generateRefreshToken(user.getUsername());

    Instant now = Instant.now();
    RefreshToken refreshToken = RefreshToken.create(
            TokenHashUtil.sha256(rawRefreshToken),
            user,
            now.plusMillis(jwtProperties.refreshToken().expiration())
    );

    refreshTokenRepository.save(refreshToken);

    return refreshTokenMapper.toResponse(accessToken, rawRefreshToken);
  }
}
