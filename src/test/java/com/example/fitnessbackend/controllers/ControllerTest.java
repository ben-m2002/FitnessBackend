package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.auth.AuthRequestDto;
import com.example.fitnessbackend.dtos.responses.auth.AuthResponseDto;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.models.WorkoutExercise;
import com.example.fitnessbackend.models.WorkoutSession;
import com.example.fitnessbackend.nonPersistData.ExerciseName;
import com.example.fitnessbackend.nonPersistData.UserRole;
import com.example.fitnessbackend.repositories.UserModelRepository;
import com.example.fitnessbackend.repositories.WorkoutExerciseRepository;
import com.example.fitnessbackend.repositories.WorkoutSessionRepository;
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

  @Autowired private WorkoutSessionRepository workoutSessionRepository;

  @Autowired private WorkoutExerciseRepository workoutExerciseRepository;

  private WorkoutSession savedWorkoutSession;

  private WorkoutExercise savedWorkoutExercise;

  public  ControllerTest() {

  }

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

  protected String getAccessToken() throws Exception {
    AuthRequestDto authRequestDto = new AuthRequestDto("admin@admin.com", "admin");
    String authRequestBody = objectMapper.writeValueAsString(authRequestDto);
    AuthResponseDto authResponseDto = this.getAuthLoginResponse(authRequestBody);
    return authResponseDto.getAccessToken();
  }

  protected WorkoutSession getASavedWorkoutSession(UserModel userModel) {
    WorkoutSession workoutSession =
        WorkoutSession.builder()
            .workoutDescription("good lower body day")
            .user(userModel)
            .workoutDifficulty(5)
            .workoutNotes("Man this wasn't that bad but I still feel pretty good")
            .build();
    return workoutSessionRepository.save(workoutSession);
  }

  protected WorkoutExercise getASavedWorkoutExercise(WorkoutSession workoutSession) {
    WorkoutExercise workoutExercise =
        WorkoutExercise.builder()
            .workoutSession(workoutSession)
            .exerciseName(ExerciseName.SQUAT)
            .build();
    return workoutExerciseRepository.save(workoutExercise);
  }
}
