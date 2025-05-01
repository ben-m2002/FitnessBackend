package com.example.fitnessbackend.dtos.responses.auth;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AuthResponseDto extends ResponseDto {
    private Integer userId;
    public AuthResponseDto(String token, String message, Integer userId) {
        super(token, message);
        this.userId = userId;
    }
}
