package com.example.fitnessbackend.service;


import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.models.AuthToken;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.example.fitnessbackend.repositories.UserModelRepository;
import com.example.fitnessbackend.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class Service {
    protected final JwtTokenProvider jwtTokenProvider;
    protected final AuthTokenRepository authTokenRepository;
    protected final AuthenticationManager authenticationManager;

    protected Service(JwtTokenProvider jwtTokenProvider, AuthTokenRepository authTokenRepository, AuthenticationManager authenticationManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authTokenRepository = authTokenRepository;
        this.authenticationManager = authenticationManager;
    }

    public UserModel getAuthenticatedUserModel(){
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userPrincipal.getUserModel();
    }

}
