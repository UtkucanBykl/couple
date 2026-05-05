package com.example.couple.controller;

import com.example.couple.dto.request.CoupleInviteRequest;
import com.example.couple.dto.response.CoupleInviteResponse;
import com.example.couple.security.CustomUserPrincipal;
import com.example.couple.service.CoupleInviteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/invites")
@AllArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CoupleInviteController {
  private final CoupleInviteService coupleInviteService;

  @PostMapping
  public ResponseEntity<CoupleInviteResponse> createInvite(
      @Valid @RequestBody CoupleInviteRequest inviteRequest,
      @AuthenticationPrincipal CustomUserPrincipal user) {
    return ResponseEntity.ok(coupleInviteService.create(inviteRequest, user.getId()));
  }
}
