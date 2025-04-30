package com.example.fitnessbackend.dtos.requests.auth;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AuthRequestDto {
    private String email;
    private String password;
}
