package com.example.couple.contoller;

import com.example.couple.dto.request.RefreshTokenRequest;
import com.example.couple.dto.response.RefreshTokenResponse;
import com.example.couple.entity.RefreshToken;
import com.example.couple.entity.User;
import com.example.couple.repository.RefreshTokenRepository;
import com.example.couple.repository.UserRepository;
import com.example.couple.security.JwtService;
import com.example.couple.util.TokenHashUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class RefreshTokenIntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private RefreshTokenRepository refreshTokenRepository;

  @Autowired private JwtService jwtService;

  @Autowired private UserRepository userRepository;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldReturnNewRefreshTokenWhenRefreshTokenIsValid() throws Exception {
    User user = new User();
    user.setUsername("user1");
    user.setEmail("user1@test.com");
    user.setPasswordHash("hashed_password");
    user.setFriendCode("12345");
    userRepository.saveAndFlush(user);

    String refreshToken = jwtService.generateRefreshToken(user.getUsername());
    ;
    refreshTokenRepository.save(
        RefreshToken.create(TokenHashUtil.sha256(refreshToken), user, Instant.now().plusSeconds(36000)));

    RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
    refreshTokenRequest.setRefreshToken(refreshToken);

    String responseBody =
        mockMvc
            .perform(
                post("/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(refreshTokenRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

    RefreshTokenResponse response =
        objectMapper.readValue(responseBody, RefreshTokenResponse.class);

    String hashToken = TokenHashUtil.sha256(response.refreshToken());
    RefreshToken token =
        refreshTokenRepository
            .findActiveByHashTokenForUpdate(hashToken, Instant.now())
            .orElseThrow(() -> new AssertionError("New refresh token hash was not persisted"));

    assertThat(token.getUser().getUsername()).isEqualTo(user.getUsername());
    assertThat(response.refreshToken()).isNotEqualTo(refreshToken);
  }
}
