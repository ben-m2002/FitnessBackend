package com.example.fitnessbackend.dtos.requests.auth;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterDto extends AuthRequestDto {
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String userName;

    public RegisterDto(String email, String password, String firstName, String userName) {
        super(email, password);
        this.firstName = firstName;
        this.userName = userName;
    }
}
