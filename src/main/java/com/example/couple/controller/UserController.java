package com.example.couple.controller;

import com.example.couple.dto.query.UserListSearchQuery;
import com.example.couple.dto.request.UserCreateRequest;
import com.example.couple.dto.request.UserLoginRequest;
import com.example.couple.dto.response.PagedResponse;
import com.example.couple.dto.response.UserCreateResponse;
import com.example.couple.dto.response.UserSearchResponse;
import com.example.couple.dto.response.UserLoginResponse;
import com.example.couple.mapper.PageResponseMapper;
import com.example.couple.security.CustomUserPrincipal;
import com.example.couple.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserCreateResponse> createUser(
      @Valid @RequestBody UserCreateRequest request) {
    UserCreateResponse response = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping(path = "/login")
  public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody UserLoginRequest request) {
    UserLoginResponse response = userService.loginUser(request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public PagedResponse<UserSearchResponse> searchUsers(
      @ModelAttribute UserListSearchQuery userListSearchQuery,
      @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
      @PageableDefault(size = 20, sort = "username") Pageable pageable
  ) {
    Page<UserSearchResponse> result = userService.search(userListSearchQuery, customUserPrincipal.getId(), pageable);
    return PageResponseMapper.from(result);
  }
}
