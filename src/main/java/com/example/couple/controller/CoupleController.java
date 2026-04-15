package com.example.couple.controller;

import com.example.couple.dto.request.CoupleWriteRequest;
import com.example.couple.dto.response.CoupleDetailResponse;
import com.example.couple.dto.response.CoupleWriteResponse;
import com.example.couple.entity.User;
import com.example.couple.security.CustomUserPrincipal;
import com.example.couple.service.CoupleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/couples")
@RequiredArgsConstructor
public class CoupleController {
    private final CoupleService coupleService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CoupleWriteResponse> createCouple(
            @Valid @RequestBody CoupleWriteRequest request,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        return ResponseEntity.ok(coupleService.createCouple(request, user.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Null> deleteCouple(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        coupleService.deleteCouple(id, user.getId());
        return ResponseEntity.ok(null);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CoupleDetailResponse> getCoupleDetail(
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        return ResponseEntity.ok(coupleService.detailCouple(user.getId()));
    }
}