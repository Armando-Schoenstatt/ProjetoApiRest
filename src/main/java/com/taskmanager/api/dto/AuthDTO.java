package com.taskmanager.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDTO {

    public record RegisterRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 2, max = 100) String name,
            @NotBlank @Size(min = 6) String password
    ) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record TokenResponse(String token, String type, String email, String name) {
        public TokenResponse(String token, String email, String name) {
            this(token, "Bearer", email, name);
        }
    }
}
