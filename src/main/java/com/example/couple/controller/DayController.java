package com.example.couple.controller;

import com.example.couple.dto.request.DayWriteRequest;
import com.example.couple.dto.response.DayWriteResponse;
import com.example.couple.entity.User;
import com.example.couple.service.DayService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/days")
@AllArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DayController {
  private final DayService dayService;

  @PostMapping
  public ResponseEntity<DayWriteResponse> createDay(
      @Valid DayWriteRequest request, @AuthenticationPrincipal User user) {
    return ResponseEntity.status(HttpStatus.CREATED).body(dayService.createDay(request, user));
  }
}
