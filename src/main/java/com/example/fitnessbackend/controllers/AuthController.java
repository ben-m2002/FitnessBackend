package com.example.fitnessbackend.controllers;


import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.requests.auth.AuthRequestDto;
import com.example.fitnessbackend.dtos.requests.auth.RegisterDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody RegisterDto registerDto) {
        return ResponseEntity.status(200).body(authService.register(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody AuthRequestDto dto) {
        return ResponseEntity.status(200).body(authService.login(dto));
    }

    @DeleteMapping("/logout")
    @Transactional
    public ResponseEntity<ResponseDto> logout(HttpServletRequest request) {
        String token = resolveToken(request);
        return ResponseEntity.status(200).body(authService.logout(token));
    }

    private String resolveToken(HttpServletRequest req) {
        String bearer = req.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

}


