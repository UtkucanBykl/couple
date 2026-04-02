package com.example.couple.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserLoginRequest {
    @NotBlank
    @Size(min = 3)
    private String username;

    @NotBlank
    @Size(min = 10, message = "Parola minimum 10 karakter olmalı")
    private String password;
}
