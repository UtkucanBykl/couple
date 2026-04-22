package com.example.couple.controller;

import com.example.couple.dto.request.RefreshTokenRequest;
import com.example.couple.dto.response.RefreshTokenResponse;
import com.example.couple.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth/")
@AllArgsConstructor
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(refreshTokenService.refreshToken(refreshTokenRequest));
    }
}
