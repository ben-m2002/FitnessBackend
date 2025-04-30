package com.example.fitnessbackend.dtos.requests.auth;
import lombok.Data;

@Data
public class RegisterDto extends AuthRequestDto {
    private String firstName;
    private String userName;

    public RegisterDto(String email, String password, String firstName, String userName) {
        super(email, password);
        this.firstName = firstName;
        this.userName = userName;
    }
}
