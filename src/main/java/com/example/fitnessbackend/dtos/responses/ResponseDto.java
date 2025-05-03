package com.example.fitnessbackend.dtos.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseDto {
    private String message;

    public ResponseDto(String message) {
        this.message = message;
    }
}
