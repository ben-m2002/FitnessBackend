package com.example.fitnessbackend.dtos.requests.auth;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterDto extends AuthRequestDto {
    @NotEmpty
    private String firstName;
    @NotEmpty
    @JsonProperty("username")
    private String userName;

    public RegisterDto(String email, String password, String firstName, String userName) {
        super(email, password);
        this.firstName = firstName;
        this.userName = userName;
    }
}
