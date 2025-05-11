package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.workout.WorkoutExerciseDto;
import com.example.fitnessbackend.models.WorkoutExercise;
import com.example.fitnessbackend.models.WorkoutSession;
import com.example.fitnessbackend.nonPersistData.ExerciseName;
import com.example.fitnessbackend.repositories.UserModelRepository;
import com.example.fitnessbackend.repositories.WorkoutExerciseRepository;
import com.example.fitnessbackend.repositories.WorkoutSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.C;
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
public class WorkoutExerciseControllerTest extends ControllerTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserModelRepository userModelRepository;

  @Autowired private WorkoutSessionRepository workoutSessionRepository;

  @Autowired private WorkoutExerciseRepository workoutExerciseRepository;

  private WorkoutSession savedWorkoutSession;

  private WorkoutExercise savedWorkoutExercise;

  @BeforeEach
  public void setUp() {
    addDefaultUserToDB();
    savedWorkoutSession =
        this.getASavedWorkoutSession(userModelRepository.findByEmail("admin@admin.com"));
    savedWorkoutExercise = this.getASavedWorkoutExercise(savedWorkoutSession);
  }

  @AfterEach
  public void tearDown() {
    userModelRepository.deleteAll();
    workoutSessionRepository.deleteAll();
    workoutExerciseRepository.deleteAll();
    savedWorkoutSession = null;
    savedWorkoutExercise = null;
  }

  @Test
  public void createWorkoutExercise_WithValidRequest_ShouldSucceed() throws Exception {
    WorkoutExerciseDto dto =
        new WorkoutExerciseDto(savedWorkoutSession.getId(), ExerciseName.BENCH_PRESS.name());
    String workoutExerciseRequestBody = objectMapper.writeValueAsString(dto);
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/workout-exercise/create")
                .contentType("application/json")
                .content(workoutExerciseRequestBody)
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("Workout exercise created successfully"));
  }

  @Test
  public void createWorkoutExercise_WithInvalidAuthToken_ShouldThrowError() throws Exception {
    WorkoutExerciseDto dto =
        new WorkoutExerciseDto(savedWorkoutExercise.getId(), ExerciseName.BENCH_PRESS.name());
    String workoutExerciseRequestBody = objectMapper.writeValueAsString(dto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/workout-exercise/create")
                .contentType("application/json")
                .content(workoutExerciseRequestBody))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  public void getAllWorkoutExercises_ShouldReturnAllExercises() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/workout-exercise/getAll")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("All workout exercises retrieved successfully"));
  }

  @Test
  public void getAllWorkoutExercises_InvalidToken_ShouldReturnUnAuthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/workout-exercise/getAll")
                .header("Authorization", "Bearer " + "asdsadsadsadsadsad"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid JWT token"));
  }

  @Test
  public void getAllWorkoutExercisesFromSession_ShouldReturnAllExercises() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    "/api/workout-exercise/get/AllFromSession/" + savedWorkoutSession.getId())
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("All workout exercises retrieved successfully"));
  }

  @Test
  public void getAllWorkoutExercisesFromSession_InvalidSessionId_ShouldReturnBadRequest()
      throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/workout-exercise/get/AllFromSession/" + "99999")
                .header("Authorization", "Bearer " + getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Workout session not found"));
  }

  @Test
  public void getWorkoutExerciseById_ShouldReturnWorkoutExercise() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/workout-exercise/get/" + savedWorkoutExercise.getId())
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("Workout exercise retrieved successfully"));
  }

  @Test
  public void getWorkoutExerciseById_InvalidId_ShouldReturnBadRequest() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/workout-exercise/get/" + "99999")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Workout exercise not found"));
  }

  @Test
  public void deleteWorkoutExercise_ShouldDeleteWorkoutExercise() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                    "/api/workout-exercise/delete/" + savedWorkoutExercise.getId())
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("Workout exercise deleted successfully"));
  }

  @Test
  public void deleteWorkoutExercise_InvalidId_ShouldReturnBadRequest() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/workout-exercise/delete/" + "99999")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Workout exercise not found"));
  }

  @Test
  public void deleteWorkoutExercise_InvalidAuthToken_ShouldReturnUnauthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                    "/api/workout-exercise/delete/" + savedWorkoutExercise.getId())
                .header("Authorization", "Bearer " + "invalid_token"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid JWT token"));
  }
}
