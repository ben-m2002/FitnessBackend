package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.workout.SetEntryDto;
import com.example.fitnessbackend.dtos.requests.workout.SetEntryUDto;
import com.example.fitnessbackend.models.SetEntry;
import com.example.fitnessbackend.models.WorkoutExercise;
import com.example.fitnessbackend.models.WorkoutSession;
import com.example.fitnessbackend.nonPersistData.ExerciseName;
import com.example.fitnessbackend.nonPersistData.WeightMeasurementType;
import com.example.fitnessbackend.repositories.SetEntryRepository;
import com.example.fitnessbackend.repositories.UserModelRepository;
import com.example.fitnessbackend.repositories.WorkoutExerciseRepository;
import com.example.fitnessbackend.repositories.WorkoutSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class SetEntryControllerTest extends ControllerTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserModelRepository userModelRepository;

  @Autowired private WorkoutSessionRepository workoutSessionRepository;

  @Autowired private WorkoutExerciseRepository workoutExerciseRepository;

  @Autowired private SetEntryRepository setEntryRepository;

  private WorkoutSession savedWorkoutSession;

  private WorkoutExercise savedWorkoutExercise;

  private SetEntry savedSetEntry;

  @BeforeEach
  public void setUp() {
    addDefaultUserToDB();
    savedWorkoutSession =
        this.getASavedWorkoutSession(userModelRepository.findByEmail("admin@admin.com"));
    savedWorkoutExercise = this.getASavedWorkoutExercise(savedWorkoutSession);
    SetEntry setEntry =
        SetEntry.builder()
            .numSets(4)
            .reps(12)
            .weight(225)
            .difficulty(3)
            .measurementType(WeightMeasurementType.POUNDS)
            .workoutExercise(savedWorkoutExercise)
            .build();
    savedSetEntry = setEntryRepository.save(setEntry);
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
  public void createSetEntry_WithValidRequest_ShouldSucceed() throws Exception {
    SetEntryDto setEntryDto =
        SetEntryDto.builder()
            .workoutExerciseId(savedWorkoutExercise.getId())
            .weight(1000)
            .numSets(100)
            .reps(100)
            .measurementType(WeightMeasurementType.POUNDS)
            .difficulty(3)
            .build();
    String setEntryRequestBody = objectMapper.writeValueAsString(setEntryDto);
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/set-entry/create")
                .contentType("application/json")
                .content(setEntryRequestBody)
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message").value("Set entry created successfully"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.workoutExerciseId")
                .value(savedWorkoutExercise.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.weight").value(1000))
        .andExpect(MockMvcResultMatchers.jsonPath("$.numSets").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("$.reps").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("$.difficulty").value(3));
  }

  @Test
  public void createSetEntry_WithInvalidRequest_ShouldReturnError() throws Exception {
    SetEntryDto setEntryDto =
        SetEntryDto.builder().weight(1000).numSets(100).reps(100).difficulty(3).build();
    String setEntryRequestBody = objectMapper.writeValueAsString(setEntryDto);
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/set-entry/create")
                .contentType("application/json")
                .content(setEntryRequestBody)
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  public void getAllSetEntries_ShouldReturnAllSetEntries() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/set-entry/get/all")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("All set entries retrieved successfully"));
  }

  @Test
  public void getAllSetEntries_InvalidAccessToken_ShouldReturnAuthError() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/set-entry/get/all")
                .header("Authorization", "Bearer " + "-9999999999"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid JWT token"));
  }

  @Test
  public void getAllSetEntriesFromExercise_ShouldReturnAllSetEntriesFromExercises()
      throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    "/api/set-entry/get/fromExercise/" + savedWorkoutExercise.getId())
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message")
                .value("All set entries retrieved successfully"));
  }

  @Test
  public void getAllSetEntriesFromExercise_InvalidExercise_ShouldReturnAllSetEntriesFromExercises()
      throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/set-entry/get/fromExercise/" + "9999")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Workout exercise not found"));
  }

  @Test
  public void getSetEntry_ShouldReturnSetEntry() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/set-entry/get/" + savedSetEntry.getId())
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message").value("Set entry retrieved successfully"));
  }

  @Test
  public void getSetEntry_InvalidId_ShouldReturnSetEntry() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/set-entry/get/" + "9999")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Set entry not found"));
  }

  @Test
  public void updateSetEntry_ShouldUpdateSetEntry() throws Exception {
    String accessToken = getAccessToken();
    SetEntryUDto setEntryUDto =
        SetEntryUDto.builder()
            .id(savedSetEntry.getId())
            .weight(10000)
            .numSets(10000)
            .reps(10000)
            .measurementType(WeightMeasurementType.POUNDS)
            .difficulty(3)
            .build();
    String setEntryRequestBody = objectMapper.writeValueAsString(setEntryUDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/set-entry/update")
                .contentType("application/json")
                .content(setEntryRequestBody)
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message").value("Set entry updated successfully"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.weight").value(10000))
        .andExpect(MockMvcResultMatchers.jsonPath("$.numSets").value(10000))
        .andExpect(MockMvcResultMatchers.jsonPath("$.reps").value(10000))
        .andExpect(MockMvcResultMatchers.jsonPath("$.difficulty").value(3));
  }

  @Test
  public void updateSetEntry_InvalidId_ShouldReturnError() throws Exception {
    String accessToken = getAccessToken();
    SetEntryUDto setEntryUDto =
        SetEntryUDto.builder()
            .id(9999)
            .weight(10000)
            .numSets(10000)
            .reps(10000)
            .difficulty(3)
            .measurementType(WeightMeasurementType.POUNDS)
            .build();
    String setEntryRequestBody = objectMapper.writeValueAsString(setEntryUDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/set-entry/update")
                .contentType("application/json")
                .content(setEntryRequestBody)
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Set entry not found"));
  }

  @Test
  public void deleteSetEntry_ShouldDeleteSetEntry() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/set-entry/delete/" + savedSetEntry.getId())
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.message").value("Set entry deleted successfully"));
  }

  @Test
  public void deleteSetEntry_InvalidId_ShouldReturnError() throws Exception {
    String accessToken = getAccessToken();
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/set-entry/delete/" + "9999")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Set entry not found"));
  }
}
