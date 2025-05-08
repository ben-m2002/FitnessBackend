package com.example.fitnessbackend.service;
import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.requests.auth.AuthRequestDto;
import com.example.fitnessbackend.dtos.requests.auth.RegisterDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.dtos.responses.auth.AuthResponseDto;
import com.example.fitnessbackend.dtos.responses.auth.UserResponseDto;
import com.example.fitnessbackend.exceptions.EmailAlreadyExists;
import com.example.fitnessbackend.exceptions.InvalidCredentialsException;
import com.example.fitnessbackend.exceptions.UsernameAlreadyExists;
import com.example.fitnessbackend.mappers.AuthMapper;
import com.example.fitnessbackend.models.AuthToken;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.nonPersistData.Tokens;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.example.fitnessbackend.repositories.UserModelRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;

@Service
public class AuthService extends com.example.fitnessbackend.service.Service {
    private final AuthMapper authMapper;
    protected final UserModelRepository userModelRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, AuthMapper authMapper,
                       UserModelRepository userModelRepository, AuthTokenRepository authTokenRepository, PasswordEncoder passwordEncoder) {
        super(jwtTokenProvider, authTokenRepository, authenticationManager);
        this.authMapper = authMapper;
        this.userModelRepository = userModelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Add methods for authentication and token generation here

    public AuthResponseDto register(@NotNull RegisterDto registerDto, HttpServletResponse response) {
        // Check if email exists
        if (userModelRepository.existsByEmail(registerDto.getEmail())) {
            throw new EmailAlreadyExists("Email already exists");
        }
        // check if username exists
        if (userModelRepository.existsByUsername(registerDto.getUserName())) {
            throw new UsernameAlreadyExists("Username already exists");
        }

        UserModel userModel = authMapper.registerToModel(registerDto);
        userModel.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        UserModel savedModel = userModelRepository.save(userModel);
        Tokens tokens = authenticateUser(savedModel, registerDto.getPassword(), response);
        if (tokens == null) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return authMapper.modelToResponseDto(userModel, tokens.getAccessToken(),
                "User registered successfully");

    }

    public ResponseDto login(@NotNull AuthRequestDto authRequestDto, HttpServletResponse response) {
        UserModel userModel = userModelRepository.findByEmail(authRequestDto.getEmail());
        if (userModel == null) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        Tokens tokens = authenticateUser(userModel, authRequestDto.getPassword(), response);
        return authMapper.modelToResponseDto(userModel, tokens.getAccessToken(),
                "User logged in successfully");
    }

    @Transactional
    public ResponseDto logout(String refreshToken, HttpServletResponse response) {
        jwtTokenProvider.deleteToken(refreshToken);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return new ResponseDto("User logged out successfully");
    }

    public AuthResponseDto refresh(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }
        AuthToken authToken = authTokenRepository.findAuthTokenByToken(refreshToken);
        if (authToken == null) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }
        UserModel userModel = userModelRepository.findByEmail(authToken.getEmail());
        if (userModel == null) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }
        // delete the old token
        jwtTokenProvider.deleteToken(refreshToken);
        Tokens tokens = new Tokens(jwtTokenProvider.createRefreshToken(userModel),
                jwtTokenProvider.createAccessToken(userModel));
        return authMapper.modelToResponseDto(userModel, tokens.getAccessToken(),
                "Token refreshed successfully");
    }

    public UserResponseDto getUser (String refreshToken){
        if (refreshToken == null || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }
        String email = jwtTokenProvider.getEmail(refreshToken);
        UserModel userModel = userModelRepository.findByEmail(email);
        if (userModel == null) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }
        return authMapper.modelToUserResponseDto(userModel, "User retrieved successfully");
    }

    private Tokens authenticateUser(UserModel userModel, String rawPassword, HttpServletResponse response) {
        Authentication auth = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userModel.getEmail(),
                rawPassword));
        if (auth.isAuthenticated()) {
            // check to see if there is already a token before creating one
            AuthToken authToken = authTokenRepository.findAuthTokenByEmail(userModel.getEmail());
            if (authToken != null) {
               jwtTokenProvider.deleteToken(authToken.getToken());
            }
            Tokens tokens = new Tokens(jwtTokenProvider.createRefreshToken(userModel),
                    jwtTokenProvider.createAccessToken(userModel));
            Duration duration = Duration.ofMillis(jwtTokenProvider.getRefreshValidityInMilliseconds());
            ResponseCookie responseCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                    .httpOnly(true)
                    .path("/api")
                    .sameSite("None")
                    .secure(true)
                    .maxAge((int)duration.getSeconds())
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            return tokens;
        }
        else{
            throw new InvalidCredentialsException("recheck login credentials");
        }
    }
}



