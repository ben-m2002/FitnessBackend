package com.example.fitnessbackend.nonPersistData;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Tokens {
    private String refreshToken;
    private String accessToken;
}
