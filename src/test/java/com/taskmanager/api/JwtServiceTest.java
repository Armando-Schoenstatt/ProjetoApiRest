package com.taskmanager.api;

import com.taskmanager.api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JwtService(
                "3f8a2b1c9d4e7f6a0b5c8d2e1f4a7b3c9d6e2f5a8b1c4d7e0f3a6b9c2d5e8f1a",
                86400000L
        );
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String token = jwtService.generateToken("user@test.com");
        assertThat(token).isNotBlank();

        UserDetails userDetails = new User("user@test.com", "", Collections.emptyList());
        assertThat(jwtService.isValid(token, userDetails)).isTrue();
    }

    @Test
    void shouldExtractEmailFromToken() {
        String token = jwtService.generateToken("email@test.com");
        assertThat(jwtService.extractEmail(token)).isEqualTo("email@test.com");
    }

    @Test
    void shouldRejectTokenWithWrongUser() {
        String token = jwtService.generateToken("user@test.com");
        UserDetails other = new User("other@test.com", "", Collections.emptyList());
        assertThat(jwtService.isValid(token, other)).isFalse();
    }
}
