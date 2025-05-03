package com.example.fitnessbackend.dtos.responses.auth;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AuthResponseDto extends ResponseDto {
    private Integer userId;
    private String accessToken;
    public AuthResponseDto(String message, String accessToken, Integer userId) {
        super(message);
        this.accessToken = accessToken;
        this.userId = userId;
    }
}
