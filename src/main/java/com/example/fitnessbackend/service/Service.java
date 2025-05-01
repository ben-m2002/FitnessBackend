package com.example.fitnessbackend.service;


import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.models.AuthToken;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.example.fitnessbackend.repositories.UserModelRepository;

public abstract class Service {
    protected final JwtTokenProvider jwtTokenProvider;
    protected final AuthTokenRepository authTokenRepository;


    protected Service(JwtTokenProvider jwtTokenProvider, AuthTokenRepository authTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authTokenRepository = authTokenRepository;
    }

    public String renewToken(UserModel userModel) {
        AuthToken oldToken = authTokenRepository.findAuthTokenByEmail(userModel.getEmail());
        return jwtTokenProvider.renewToken(oldToken.getToken());
    }
}
