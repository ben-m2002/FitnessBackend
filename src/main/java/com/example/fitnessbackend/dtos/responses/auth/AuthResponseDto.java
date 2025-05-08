package com.example.fitnessbackend.dtos.responses.auth;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto extends ResponseDto {
    private Integer userId;
    private String accessToken;

    @JsonCreator
    public AuthResponseDto(
            @JsonProperty("message") String message,
            @JsonProperty("accessToken")  String accessToken,
            @JsonProperty("userId") Integer userId) {
        super(message);
        this.accessToken = accessToken;
        this.userId = userId;
    }
}
