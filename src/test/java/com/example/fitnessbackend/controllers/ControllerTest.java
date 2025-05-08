package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.responses.auth.AuthResponseDto;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.nonPersistData.UserRole;
import com.example.fitnessbackend.repositories.UserModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserModelRepository userModelRepository;

  protected void addDefaultUserToDB() {
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    UserModel defaultUser =
        UserModel.builder()
            .email("admin@admin.com")
            .password(encoder.encode("admin"))
            .firstName("Admin")
            .username("admin")
            .role(UserRole.ADMIN)
            .build();
    userModelRepository.save(defaultUser);
  }

  protected String createValidRefreshToken(String requestBody) throws Exception {
    MvcResult loginResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    String setCookieHeader = loginResult.getResponse().getHeader("Set-Cookie");

    return Arrays.stream(setCookieHeader.split(";")).findFirst().get().split("=")[1];
  }

  protected AuthResponseDto getAuthLoginResponse(String requestBody) throws Exception {
    MvcResult loginResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    String responseBody = loginResult.getResponse().getContentAsString();
    return objectMapper.readValue(responseBody, AuthResponseDto.class);
  }
}
