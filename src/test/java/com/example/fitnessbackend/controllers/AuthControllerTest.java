package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.auth.AuthRequestDto;
import com.example.fitnessbackend.dtos.requests.auth.RegisterDto;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.nonPersistData.UserRole;
import com.example.fitnessbackend.repositories.UserModelRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthControllerTest extends ControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserModelRepository userModelRepository;

  @BeforeEach
  public void setUp() {
    this.addDefaultUserToDB();
  }

  @AfterEach
  public void tearDown() {
    userModelRepository.deleteAll();
  }

  @Test
  public void register_ValidUser_ShouldReturnUSer() throws Exception {
    RegisterDto registerDto = new RegisterDto("ben@admin.com", "admin", "Ben", "ben");
    String requestBody = objectMapper.writeValueAsString(registerDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message").value("User registered successfully"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.userId").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isString());
  }

  @Test
  public void register_NoEmail_ShouldReturnBadRequest() throws Exception {
    RegisterDto registerDto = new RegisterDto("", "admin", "Ben", "ben");
    String requestBody = objectMapper.writeValueAsString(registerDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value(
                    "Invalid input: email: must not be empty {errors=[email: must not be empty]}"));
  }

  @Test
  public void login_ValidUser_ShouldReturnUser() throws Exception {
    AuthRequestDto authRequestDto = new AuthRequestDto("admin@admin.com", "admin");
    String requestBody = objectMapper.writeValueAsString(authRequestDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User logged in successfully"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isString());
  }

  @Test
  public void login_InvalidCredentials_ShouldReturnAnError() throws Exception {
    AuthRequestDto authRequestDto = new AuthRequestDto("admin12@admin.com", "admin21333");
    String requestBody = objectMapper.writeValueAsString(authRequestDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid email or password"));
  }

  @Test
  public void logout_WithValidRefreshTokenCookie_ShouldSucceed() throws Exception {
    AuthRequestDto authRequestDto = new AuthRequestDto("admin@admin.com", "admin");
    String requestBody = objectMapper.writeValueAsString(authRequestDto);

    String refreshTokenValue = this.createValidRefreshToken(requestBody);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/auth/logout")
                .cookie(new Cookie("refreshToken", refreshTokenValue)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("User logged out successfully")); // Or your actual message
  }

  @Test
  public void logout_WithInvalidRefreshTokenCookie_ShouldReturnError() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/auth/logout")
                .cookie(new Cookie("refreshToken", "sdsdsdwdsedwqeqwdqwd")))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Token not found"));
  }

  @Test
  public void refresh_WithValidRefreshTokenCookie_ShouldSucceed() throws Exception {
    AuthRequestDto authRequestDto = new AuthRequestDto("admin@admin.com", "admin");
    String requestBody = objectMapper.writeValueAsString(authRequestDto);
    String refreshTokenValue = this.createValidRefreshToken(requestBody);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/auth/refresh")
                .cookie(new Cookie("refreshToken", refreshTokenValue)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("Token refreshed successfully")); // Or your actual message
  }

  @Test
  public void refresh_WithInvalidJWTToken_ShouldThrowInvalidJWTToken() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/auth/refresh")
                .cookie(new Cookie("refreshToken", "sdsdsdwdsedwqeqwdqwd")))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid JWT token"));
  }

  @Test
  public void refresh_WithLoggedOutJwtToken_ShouldDisplayTokenNotFound() throws Exception {
    AuthRequestDto authRequestDto = new AuthRequestDto("admin@admin.com", "admin");
    String requestBody = objectMapper.writeValueAsString(authRequestDto);
    String refreshTokenValue = this.createValidRefreshToken(requestBody);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/auth/logout")
                .cookie(new Cookie("refreshToken", refreshTokenValue)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("User logged out successfully")); // Or your actual message

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/auth/refresh")
                .cookie(new Cookie("refreshToken", refreshTokenValue)))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("Token not found")); // Or your actual message
  }

  @Test
  public void getUser_WithValidRefreshTokenCookie_ShouldSucceed() throws Exception {
    AuthRequestDto authRequestDto = new AuthRequestDto("admin@admin.com", "admin");
    String requestBody = objectMapper.writeValueAsString(authRequestDto);
    String refreshTokenValue = this.createValidRefreshToken(requestBody);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/auth/me")
                .cookie(new Cookie("refreshToken", refreshTokenValue)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User retrieved successfully"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("admin@admin.com"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("admin"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Admin"));
  }

  @Test
  public void getUser_WithInValidRefreshTokenCookie_ShouldThrowA400Error() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/auth/me")
                .cookie(new Cookie("refreshToken", "sdsdsdsdsdsdsd")))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  public void getUser_WithLoggedOutJwtToken_ShouldThrowA400Error() throws Exception {
    AuthRequestDto authRequestDto = new AuthRequestDto("admin@admin.com", "admin");
    String requestBody = objectMapper.writeValueAsString(authRequestDto);
    String refreshTokenValue = this.createValidRefreshToken(requestBody);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/auth/logout")
                .cookie(new Cookie("refreshToken", refreshTokenValue)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("User logged out successfully")); // Or your actual message

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/auth/me")
                .cookie(new Cookie("refreshToken", refreshTokenValue)))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("Token not found")); // Or your actual message
  }
}
