package com.example.fitnessbackend.mappers;
import com.example.fitnessbackend.dtos.requests.auth.RegisterDto;
import com.example.fitnessbackend.models.UserModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;


@Component
public class AuthMapper {

    public UserModel registerToModel(@NotNull RegisterDto registerDto) {
        UserModel userModel = new UserModel();
        userModel.setEmail(registerDto.getEmail());
        userModel.setPassword(registerDto.getPassword());
        userModel.setFirstName(registerDto.getFirstName());
        userModel.setUsername(registerDto.getUserName());
        return userModel;
    }

}
