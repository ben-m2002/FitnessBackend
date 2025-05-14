package com.example.fitnessbackend.service;

import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.models.SetEntry;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.models.WorkoutExercise;
import com.example.fitnessbackend.models.WorkoutSession;
import com.example.fitnessbackend.nonPersistData.ExerciseName;
import com.example.fitnessbackend.nonPersistData.SetEntryRecommendationSpecification;
import com.example.fitnessbackend.nonPersistData.UserRole;
import com.example.fitnessbackend.repositories.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.concurrent.ThreadLocalRandom;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class RecommendationDataService extends Service {
  private final UserModelRepository userModelRepository;
  private final WorkoutSessionRepository workoutSessionRepository;
  private final WorkoutExerciseRepository workoutExerciseRepository;
  private final SetEntryRepository setEntryRepository;
  private final PasswordEncoder passwordEncoder;
  private List<UserModel> createdUsers = new ArrayList<>();
  private List<WorkoutSession> createdWorkoutSessions = new ArrayList<>();
  private List<WorkoutExercise> createdWorkoutExercises = new ArrayList<>();
  private List<SetEntry> createdSetEntries = new ArrayList<>();

  public RecommendationDataService(
      JwtTokenProvider jwtTokenProvider,
      AuthTokenRepository authTokenRepository,
      AuthenticationManager authenticationManager,
      UserModelRepository userModelRepository,
      WorkoutSessionRepository workoutSessionRepository,
      WorkoutExerciseRepository workoutExerciseRepository,
      SetEntryRepository setEntryRepository,
      PasswordEncoder passwordEncoder) {
    super(jwtTokenProvider, authTokenRepository, authenticationManager);
    this.userModelRepository = userModelRepository;
    this.workoutSessionRepository = workoutSessionRepository;
    this.workoutExerciseRepository = workoutExerciseRepository;
    this.setEntryRepository = setEntryRepository;
    this.passwordEncoder = passwordEncoder;
  }

  private UserModel createDefaultUser() {
    UserModel userModel =
        UserModel.builder()
            .email("admin" + "@bylt.com")
            .username("admin")
            .firstName("Default")
            .password(passwordEncoder.encode("admin"))
            .role(UserRole.USER)
            .build();
    UserModel savedUser = userModelRepository.save(userModel);
    createdUsers.add(savedUser);
    return savedUser;
  }

  private WorkoutSession createDefaultWorkoutSession(UserModel user) {
    WorkoutSession workoutSession = WorkoutSession.builder().user(user).build();
    WorkoutSession savedWorkoutSession = workoutSessionRepository.save(workoutSession);
    createdWorkoutSessions.add(savedWorkoutSession);
    return savedWorkoutSession;
  }

  private List<WorkoutExercise> createDefaultWorkoutExercise(
      WorkoutSession workoutSession, List<ExerciseName> exerciseNames) {
    List<WorkoutExercise> savedWorkoutExercises = new ArrayList<>();
    for (ExerciseName exerciseName : exerciseNames) {
      WorkoutExercise workoutExercise =
          WorkoutExercise.builder()
              .workoutSession(workoutSession)
              .exerciseName(exerciseName)
              .build();
      WorkoutExercise savedWorkoutExercise = workoutExerciseRepository.save(workoutExercise);
      createdWorkoutExercises.add(savedWorkoutExercise);
      savedWorkoutExercises.add(savedWorkoutExercise);
    }
    return savedWorkoutExercises;
  }

  private List<SetEntry> createSmartSetEntries(
      WorkoutExercise workoutExercise, SetEntryRecommendationSpecification spec) {
    List<SetEntry> savedSetEntries = new ArrayList<>();

    int minD = spec.getMinDifficulty();
    int maxD = spec.getMaxDifficulty();
    int diffSpan = maxD - minD;

    for (int i = 0; i < spec.getNumSetEntries(); i++) {
      // 1) Pick a difficulty between minD and maxD inclusive
      int difficulty = ThreadLocalRandom.current().nextInt(minD, maxD + 1);

      // 2) Map difficulty to a 0–1 ratio (0 = easiest, 1 = hardest)
      double ratio = diffSpan > 0 ? (difficulty - minD) / (double) diffSpan : 0.0;

      // 3) Interpolate weight, reps, sets based on ratio
      int baseWeight =
          spec.getMinWeight() + (int) (ratio * (spec.getMaxWeight() - spec.getMinWeight()));
      int weightJitter = ThreadLocalRandom.current().nextInt(-2, 3); // ±2 kg pseudo-random
      int weight =
          Math.max(spec.getMinWeight(), Math.min(spec.getMaxWeight(), baseWeight + weightJitter));

      int baseReps = spec.getMaxReps() - (int) (ratio * (spec.getMaxReps() - spec.getMinReps()));
      int repsJitter = ThreadLocalRandom.current().nextInt(-2, 3); // ±2 reps
      int reps = Math.max(spec.getMinReps(), Math.min(spec.getMaxReps(), baseReps + repsJitter));

      int baseSets =
          spec.getMinNumSets() + (int) (ratio * (spec.getMaxNumSets() - spec.getMinNumSets()));
      int setsJitter = ThreadLocalRandom.current().nextInt(0, 2); // 0 or +1 set
      int numSets =
          Math.max(spec.getMinNumSets(), Math.min(spec.getMaxNumSets(), baseSets + setsJitter));

      // 4) Build & save
      SetEntry entry =
          SetEntry.builder()
              .workoutExercise(workoutExercise)
              .measurementType(spec.getWeightMeasurementType())
              .difficulty(difficulty)
              .weight(weight)
              .reps(reps)
              .numSets(numSets)
              .build();

      setEntryRepository.save(entry);
      savedSetEntries.add(entry);
      createdSetEntries.add(entry);
    }

    return savedSetEntries;
  }

  public void createSetEntryRecommendationData(SetEntryRecommendationSpecification spec) {
    UserModel user = createDefaultUser();
    WorkoutSession workoutSession = createDefaultWorkoutSession(user);
    List<WorkoutExercise> workoutExercises =
        createDefaultWorkoutExercise(workoutSession, spec.getExerciseNames());
    for (WorkoutExercise workoutExercise : workoutExercises) {
      List<SetEntry> setEntries = createSmartSetEntries(workoutExercise, spec);
    }
  }
}
