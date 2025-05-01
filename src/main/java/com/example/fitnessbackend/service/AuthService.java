package com.example.fitnessbackend.service;
import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.requests.auth.AuthRequestDto;
import com.example.fitnessbackend.dtos.requests.auth.RegisterDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.dtos.responses.auth.AuthResponseDto;
import com.example.fitnessbackend.exceptions.EmailAlreadyExists;
import com.example.fitnessbackend.exceptions.InvalidCredentialsException;
import com.example.fitnessbackend.exceptions.UsernameAlreadyExists;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.example.fitnessbackend.repositories.UserModelRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService extends com.example.fitnessbackend.service.Service {
    private final AuthenticationManager authenticationManager;
    protected final UserModelRepository userModelRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                       UserModelRepository userModelRepository, AuthTokenRepository authTokenRepository, PasswordEncoder passwordEncoder) {
        super(jwtTokenProvider, authTokenRepository);
        this.authenticationManager = authenticationManager;
        this.userModelRepository = userModelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Add methods for authentication and token generation here

    public AuthResponseDto register(@NotNull RegisterDto registerDto) {
        // Check if email exists
        if (userModelRepository.existsByEmail(registerDto.getEmail())) {
            throw new EmailAlreadyExists("Email already exists");
        }
        // check if username exists
        if (userModelRepository.existsByUsername(registerDto.getUserName())) {
            throw new UsernameAlreadyExists("Username already exists");
        }
        UserModel userModel = UserModel.builder()
                .email(registerDto.getEmail()).
                password(passwordEncoder.encode(registerDto.getPassword())).
                firstName(registerDto.getFirstName()).
                username(registerDto.getUserName()).
                build();

        UserModel savedModel = userModelRepository.save(userModel);
        String token = authenticateUser(savedModel, registerDto.getPassword());
        return new AuthResponseDto(token, "User registered successfully", savedModel.getId());
    }

    public ResponseDto login(@NotNull AuthRequestDto authRequestDto) {
        UserModel userModel = userModelRepository.findByEmail(authRequestDto.getEmail());
        if (userModel == null) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        String token = authenticateUser(userModel, authRequestDto.getPassword());
        return new AuthResponseDto(token, "User registered successfully", userModel.getId());
    }

    @Transactional
    public ResponseDto logout(String token) {
        String email = jwtTokenProvider.getEmail(token);
        if (email == null) {
            throw new InvalidCredentialsException("Invalid token");
        }
        authTokenRepository.deleteByEmail(email);
        return new ResponseDto("User logged out successfully", null);
    }

    private String authenticateUser(UserModel userModel, String rawPassword) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userModel.getEmail(),
                rawPassword));
        if (auth.isAuthenticated()) {
            return jwtTokenProvider.createToken(userModel);
        }
        else{
            throw new InvalidCredentialsException("recheck login credentials");
        }
    }
}



