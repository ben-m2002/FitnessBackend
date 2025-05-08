package com.example.fitnessbackend.service;

import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.requests.auth.RegisterDto;
import com.example.fitnessbackend.dtos.responses.auth.AuthResponseDto;
import com.example.fitnessbackend.exceptions.EmailAlreadyExists;
import com.example.fitnessbackend.mappers.AuthMapper;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.example.fitnessbackend.repositories.UserModelRepository;
import org.apache.catalina.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
  @Mock AuthenticationManager authenticationManager;
  @Mock JwtTokenProvider jwtTokenProvider;
  @Mock AuthMapper authMapper;
  @Mock UserModelRepository userModelRepository;
  @Mock AuthTokenRepository authTokenRepository;
  @Mock PasswordEncoder passwordEncoder;

  @InjectMocks AuthService authService;

  MockHttpServletRequest request;
  MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  void register_validInformation() {
    RegisterDto registerDto = new RegisterDto("ben@admin.com", "admin", "Ben", "ben");
    AuthResponseDto expectedResponseDto =
        new AuthResponseDto("User registered successfully", "access_token", 1);
    // Mock the behavior of the dependencies
    when(userModelRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
    when(userModelRepository.existsByUsername(registerDto.getUserName())).thenReturn(false);
    when(authMapper.registerToModel(registerDto))
        .thenReturn(
            UserModel.builder()
                .email("ben@admin.com")
                .password("admin")
                .firstName("Ben")
                .username("ben")
                .build());
    when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("password");
    when(userModelRepository.save(any(UserModel.class)))
        .thenReturn(
            UserModel.builder()
                .id(1)
                .email("ben@admin.com")
                .password("admin")
                .firstName("Ben")
                .username("ben")
                .build());
    when(authenticationManager.authenticate(any()))
        .thenReturn(mock(Authentication.class, invocation -> true));
    when(authTokenRepository.findAuthTokenByEmail(registerDto.getEmail())).thenReturn(null);
    when(jwtTokenProvider.createAccessToken(any(UserModel.class))).thenReturn("access_token");
    when(jwtTokenProvider.createRefreshToken(any(UserModel.class))).thenReturn("refresh_token");
    when(jwtTokenProvider.getRefreshValidityInMilliseconds()).thenReturn(121323L);
    when(authMapper.modelToResponseDto(any(UserModel.class), any(String.class), any(String.class)))
        .thenReturn(expectedResponseDto);
    AuthResponseDto resultDto = authService.register(registerDto, response);
    // assert
    Assertions.assertEquals(expectedResponseDto, resultDto);
  }

  @Test
  void register_emailAlreadyExists() {
    RegisterDto registerDto = new RegisterDto("ben@admin.com", "admin", "Ben", "ben");
    AuthResponseDto expectedResponseDto =
        new AuthResponseDto("User registered successfully", "access_token", 1);
    // Mock the behavior of the dependencies
    when(userModelRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);
    Assertions.assertThrows(EmailAlreadyExists.class, () -> authService.register(registerDto, response));
  }
}
