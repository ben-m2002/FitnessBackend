package com.example.fitnessbackend.dtos.responses.auth;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.nonPersistData.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserResponseDto extends ResponseDto {
    private String email;
    private Integer Id;
    private String username;
    private String firstName;
    private UserRole role;

    public UserResponseDto(String message, String email, Integer id,
                           String username, String firstName, UserRole role) {
        super(message);
        this.email = email;
        Id = id;
        this.username = username;
        this.firstName = firstName;
        this.role = role;
    }

}
