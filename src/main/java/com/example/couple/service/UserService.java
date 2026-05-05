package com.example.couple.service;

import java.security.SecureRandom;

import java.util.Optional;

import com.example.couple.annotation.LogExecutionTime;
import com.example.couple.dto.query.UserListSearchQuery;
import com.example.couple.dto.request.UserLoginRequest;
import com.example.couple.dto.response.RefreshTokenResponse;
import com.example.couple.dto.response.UserCreateResponse;
import com.example.couple.dto.response.UserSearchResponse;
import com.example.couple.dto.response.UserLoginResponse;
import com.example.couple.exception.BadRequestException;
import com.example.couple.mapper.UserMapper;
import com.example.couple.observability.UserMetrics;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.couple.dto.request.UserCreateRequest;
import com.example.couple.entity.User;
import com.example.couple.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private static final SecureRandom random = new SecureRandom();
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final RefreshTokenService refreshTokenService;
  private final UserMetrics userMetrics;

  public String createFriendCode() {
    while (true) {
      StringBuilder sb = new StringBuilder(6);
      String chars = "0123456789";

      for (int i = 0; i < 6; i++) {
        int index = random.nextInt(chars.length());
        sb.append(chars.charAt(index));
      }

      String code = sb.toString();

      if (!userRepository.existsByFriendCode(code)) {
        return code;
      }
    }
  }

  public void validate(UserCreateRequest userCreateRequest) {
    boolean emailExists = userRepository.existsByEmail(userCreateRequest.getEmail());
    boolean usernameExists = userRepository.existsByUsername(userCreateRequest.getUsername());
    if (emailExists || usernameExists) {
      throw new BadRequestException("Böyle bir hesap var.");
    }
  }

  public String hashPassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  public boolean checkPassword(String rawPassword, String hashedPassword) {
    return passwordEncoder.matches(rawPassword, hashedPassword);
  }

  @Transactional
  public UserCreateResponse createUser(UserCreateRequest userCreateRequest) {
    this.validate(userCreateRequest);
    String friendCode = this.createFriendCode();
    User user = User.create(
            userCreateRequest.getUsername(),
            userCreateRequest.getEmail(),
            this.hashPassword(userCreateRequest.getPassword()),
            friendCode
    );
    User savedUser = userRepository.save(user);
    RefreshTokenResponse tokenResponse = refreshTokenService.issueTokens(savedUser);

    return userMapper.toCreateResponse(
        savedUser, tokenResponse.accessToken(), tokenResponse.refreshToken());
  }

  @LogExecutionTime
  @Transactional()
  public UserLoginResponse loginUser(UserLoginRequest userLoginRequest) {
    Optional<User> optionalUser = userRepository.findByUsername(userLoginRequest.getUsername());
    if (optionalUser.isEmpty()) {
      throw new BadRequestException("Kullanıcı adı yok");
    }
    User user = optionalUser.get();
    boolean isPasswordValid = checkPassword(userLoginRequest.getPassword(), user.getPasswordHash());
    if (!isPasswordValid) {
      userMetrics.increaseLoginPasswordUnmatch();
      throw new BadRequestException("Parola yanlış");
    }
    RefreshTokenResponse tokenResponse = refreshTokenService.issueTokens(user);
    return userMapper.toLoginResponse(
        user, tokenResponse.accessToken(), tokenResponse.refreshToken());
  }

  @Transactional(readOnly = true)
  public Page<UserSearchResponse> search(
      UserListSearchQuery queryParameters, Long currentUserId, Pageable pageable) {
    if (!queryParameters.hasAnyFilter()) {
      throw new BadRequestException("En az bir arama kriteri girilmelidir.");
    }
    return userRepository.searchUsers(
        currentUserId,
        queryParameters.friendCodeOrNull(),
        queryParameters.usernameOrNull(),
        queryParameters.emailOrNull(),
        pageable);
  }

  @Transactional(readOnly = true)
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }
}
