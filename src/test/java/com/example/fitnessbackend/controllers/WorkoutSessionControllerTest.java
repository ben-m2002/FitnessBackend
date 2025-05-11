package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.workout.WorkoutSessionDto;
import com.example.fitnessbackend.dtos.requests.workout.WorkoutSessionUpdateRequestDto;
import com.example.fitnessbackend.models.WorkoutSession;
import com.example.fitnessbackend.repositories.UserModelRepository;
import com.example.fitnessbackend.repositories.WorkoutSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WorkoutSessionControllerTest extends ControllerTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserModelRepository userModelRepository;

  @Autowired private WorkoutSessionRepository workoutSessionRepository;

  private WorkoutSession savedWorkoutSession;

  @BeforeEach
  public void setUp() {
    addDefaultUserToDB();
    savedWorkoutSession =
        this.getASavedWorkoutSession(userModelRepository.findByEmail("admin@admin.com"));
  }

  @AfterEach
  public void tearDown() {
    userModelRepository.deleteAll();
    workoutSessionRepository.deleteAll();
    savedWorkoutSession = null;
  }

  @Test
  public void createWorkoutSession_WithValidRequest_ShouldSucceed() throws Exception {
    WorkoutSessionDto dto = new WorkoutSessionDto("Pretty chill upper body day");
    String sessionRequestBody = objectMapper.writeValueAsString(dto);
    String token = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/workout-session/create")
                .contentType("application/json")
                .content(sessionRequestBody)
                .header("Authorization", "Bearer " + token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("Workout session created successfully"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.workoutDescription")
                .value(dto.getWorkoutDescription()));
  }

  @Test
  public void createWorkoutSession_InvalidAuthToken_ShouldReturnUnauthorized() throws Exception {
    WorkoutSessionDto dto = new WorkoutSessionDto("Pretty chill upper body day");
    String sessionRequestBody = objectMapper.writeValueAsString(dto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/workout-session/create")
                .contentType("application/json")
                .content(sessionRequestBody)
                .header("Authorization", "Bearer invalid_token"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid JWT token"));
  }

  @Test
  public void getUserWorkoutSessions_ValidAuthToken_ShouldReturnAllSessions() throws Exception {
    String token = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/workout-session/getAll")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void getUserWorkoutSessions_InvalidAuthToken_ShouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/workout-session/getAll")
                .contentType("application/json")
                .header("Authorization", "Bearer invalid_token"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid JWT token"));
  }

  @Test
  public void getWorkoutSession_ValidAuthToken_ShouldReturnDefaultSession() throws Exception {
    String token = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/workout-session/get/" + savedWorkoutSession.getId())
                .contentType("application/json")
                .header("Authorization", "Bearer " + token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("Workout session retrieved successfully"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.workoutDescription").value("good lower body day"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedWorkoutSession.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.workoutNotes")
                .value("Man this wasn't that bad but I still feel pretty good"));
  }

  @Test
  public void getWorkoutSession_InvalidAuthToken_ShouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/workout-session/get/1")
                .contentType("application/json")
                .header("Authorization", "Bearer invalid_token"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid JWT token"));
  }

  @Test
  public void getWorkoutSession_InvalidSessionId_ShouldReturnUnauthorized() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/workout-session/get/9999")
                .contentType("application/json")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Workout session not found"));
  }

  @Test
  public void updateWorkoutSession_ValidAuthToken_ShouldUpdateSession() throws Exception {
    String token = getAccessToken();
    WorkoutSessionUpdateRequestDto requestDto =
        WorkoutSessionUpdateRequestDto.builder()
            .id(savedWorkoutSession.getId())
            .workoutNotes("Updated notes")
            .workoutDescription("Updated description")
            .workoutDifficulty(3)
            .build();
    String requestBody = objectMapper.writeValueAsString(requestDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/workout-session/update")
                .contentType("application/json")
                .content(requestBody)
                .header("Authorization", "Bearer " + token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("Workout session updated successfully"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.workoutDescription").value("Updated description"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.workoutNotes").value("Updated notes"));
  }

  @Test
  public void updateWorkoutSession_InvalidAuthToken_ShouldReturnUnauthorized() throws Exception {
    WorkoutSessionUpdateRequestDto requestDto =
        WorkoutSessionUpdateRequestDto.builder()
            .id(savedWorkoutSession.getId())
            .workoutNotes("Updated notes")
            .workoutDescription("Updated description")
            .workoutDifficulty(3)
            .build();
    String requestBody = objectMapper.writeValueAsString(requestDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/workout-session/update")
                .contentType("application/json")
                .content(requestBody)
                .header("Authorization", "Bearer " + "Invalid token"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  public void deleteWorkoutSession_ValidAuthToken_ShouldDeleteSession() throws Exception {
    String token = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                    "/api/workout-session/delete/" + savedWorkoutSession.getId())
                .contentType("application/json")
                .header("Authorization", "Bearer " + token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("Workout session deleted successfully"));
  }

  @Test
  public void deleteWorkoutSession_InvalidWorkoutSessionId_ShouldReturnA400Error()
      throws Exception {
    String token = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/workout-session/delete/9999")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Workout session not found"));
  }
}
