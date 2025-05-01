package com.example.fitnessbackend.repositories;

import com.example.fitnessbackend.models.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Integer> {
    AuthToken findAuthTokensByToken(String token);
    AuthToken findAuthTokenByEmail(String email);
    void deleteByToken(String token);
    void deleteByEmail(String email);
}
