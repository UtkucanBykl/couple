package com.example.couple.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UserCreateRequest {

    @Size(min = 10, message = "Parola minimum ")
    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 3)
    private String username;

    @NotBlank
    @Size(min = 3)
    private String firstName;

    @NotBlank
    @Size(min = 3)
    private String lastName;

    @Email
    @NotBlank
    private String email;
}
