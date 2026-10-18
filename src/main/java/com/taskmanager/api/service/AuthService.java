package com.taskmanager.api.service;

import com.taskmanager.api.dto.AuthDTO;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.exception.GlobalExceptionHandler.BusinessException;
import com.taskmanager.api.repository.UserRepository;
import com.taskmanager.api.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthDTO.TokenResponse register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("E-mail já cadastrado.");
        }
        User user = new User(request.email(), request.name(), passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return new AuthDTO.TokenResponse(jwtService.generateToken(user.getEmail()), user.getEmail(), user.getName());
    }

    public AuthDTO.TokenResponse login(AuthDTO.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        return new AuthDTO.TokenResponse(jwtService.generateToken(user.getEmail()), user.getEmail(), user.getName());
    }
}
