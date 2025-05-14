package com.example.fitnessbackend.service;

import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.components.RecommendationCalculator;
import com.example.fitnessbackend.dtos.requests.reccomendation.SetEntryRecommendDto;
import com.example.fitnessbackend.dtos.responses.workout.SetEntryResponseDto;
import com.example.fitnessbackend.exceptions.ResourceNotFoundException;
import com.example.fitnessbackend.mappers.SetMapper;
import com.example.fitnessbackend.models.SetEntry;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.models.WorkoutExercise;
import com.example.fitnessbackend.nonPersistData.ExerciseName;
import com.example.fitnessbackend.nonPersistData.WeightMeasurementType;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.example.fitnessbackend.repositories.SetEntryRepository;
import com.example.fitnessbackend.repositories.WorkoutExerciseRepository;
import com.example.fitnessbackend.utilities.UnitConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService extends com.example.fitnessbackend.service.Service {

  private final SetEntryRepository setEntryRepository;
  private final WorkoutExerciseRepository workoutExerciseRepository;
  private final RecommendationCalculator recommendationCalculator;
  private final SetMapper setMapper;

  public RecommendationService(
      JwtTokenProvider jwtTokenProvider,
      AuthTokenRepository authTokenRepository,
      AuthenticationManager authenticationManager,
      SetEntryRepository setEntryRepository,
      WorkoutExerciseRepository workoutExerciseRepository,
      RecommendationCalculator recommendationCalculator,
      SetMapper setMapper) {
    super(jwtTokenProvider, authTokenRepository, authenticationManager);
    this.setEntryRepository = setEntryRepository;
    this.workoutExerciseRepository = workoutExerciseRepository;
    this.recommendationCalculator = recommendationCalculator;
    this.setMapper = setMapper;
  }

  public SetEntryResponseDto recommendSetEntry(SetEntryRecommendDto dto) {
    // Fetch the workout exercise using the exerciseId from the dto
    WorkoutExercise workoutExercise =
        workoutExerciseRepository
            .findById(dto.getExerciseId())
            .orElseThrow(() -> new ResourceNotFoundException("Workout exercise not found"));
    String exerciseName = workoutExercise.getExerciseName().name();

    // Fetch All the workout exercises for the user with the same exercise name
    UserModel user = this.getAuthenticatedUserModel();
    List<SetEntry> setEntries =
        workoutExerciseRepository
            .findByWorkoutSession_User_IdAndExerciseName(
                user.getId(), ExerciseName.valueOf(exerciseName))
            .stream()
            .flatMap(workoutExercise1 -> workoutExercise1.getSetEntries().stream())
            .toList();

    Integer oneRepMaxPounds =
        recommendationCalculator.predictWeighted1RM(setEntries, dto.getDifficulty());
    int weightPounds = recommendationCalculator.pickWeightPounds(setEntries, dto.getDifficulty());
    int reps = recommendationCalculator.pickNumReps(setEntries, dto.getDifficulty());
    int sets = recommendationCalculator.pickNumberOfSets(setEntries, dto.getDifficulty());
    int adjustedWeight = 0;
    if (dto.getMeasurementType() == WeightMeasurementType.KILOGRAMS) {
      adjustedWeight =
          (int)
              UnitConverter.convertLbToKg(
                  weightPounds); // This is the weight in the form the user wants
    } else {
      adjustedWeight = weightPounds;
    }
    // Create a new SetEntry object with the recommended values
    SetEntry recommendedSetEntry =
        SetEntry.builder()
            .weight(adjustedWeight)
            .numSets(sets)
            .reps(reps)
            .difficulty(dto.getDifficulty())
            .measurementType(dto.getMeasurementType())
            .workoutExercise(workoutExercise)
            .build();
    // Return the recommended set entry
    return setMapper.toSetEntryResponseDto(recommendedSetEntry, "Recommended Set Entry");
  }


}
