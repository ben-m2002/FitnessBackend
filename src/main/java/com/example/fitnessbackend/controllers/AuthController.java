package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.requests.auth.AuthRequestDto;
import com.example.fitnessbackend.dtos.requests.auth.RegisterDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.dtos.responses.auth.AuthResponseDto;
import com.example.fitnessbackend.dtos.responses.auth.UserResponseDto;
import com.example.fitnessbackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends Controller {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<ResponseDto> register(
      @Valid @RequestBody RegisterDto registerDto,
      BindingResult result,
      HttpServletResponse response) {
    if (result.hasErrors()) {
      return this.validateResult(result);
    }
    return ResponseEntity.status(200).body(authService.register(registerDto, response));
  }

  @PostMapping("/login")
  public ResponseEntity<ResponseDto> login(
      @Valid @RequestBody AuthRequestDto dto, BindingResult result, HttpServletResponse response) {
    if (result.hasErrors()) {
      return this.validateResult(result);
    }
    return ResponseEntity.status(200).body(authService.login(dto, response));
  }

  @DeleteMapping("/logout")
  @Transactional
  public ResponseEntity<ResponseDto> logout(
      @CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response) {
    return ResponseEntity.status(200).body(authService.logout(refreshToken, response));
  }

  @GetMapping("/refresh")
  public ResponseEntity<AuthResponseDto> refresh(
      @CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response) {
    return ResponseEntity.status(200).body(authService.refresh(refreshToken, response));
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getUser(
      @CookieValue(name = "refreshToken") String refreshToken) {
    return ResponseEntity.status(200).body(authService.getUser(refreshToken));
  }
}
