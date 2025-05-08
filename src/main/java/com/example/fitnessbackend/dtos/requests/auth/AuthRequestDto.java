package com.example.fitnessbackend.dtos.requests.auth;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AuthRequestDto {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
