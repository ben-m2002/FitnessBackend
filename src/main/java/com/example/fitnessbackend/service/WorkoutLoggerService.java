package com.example.fitnessbackend.service;

import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.requests.workout.*;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.*;
import com.example.fitnessbackend.entities.SqsAggregationMessages;
import com.example.fitnessbackend.exceptions.InvalidCredentialsException;
import com.example.fitnessbackend.exceptions.ResourceNotFoundException;
import com.example.fitnessbackend.mappers.SetMapper;
import com.example.fitnessbackend.mappers.WorkoutExerciseMapper;
import com.example.fitnessbackend.mappers.WorkoutSessionMapper;
import com.example.fitnessbackend.models.*;
import com.example.fitnessbackend.repositories.*;
import com.example.fitnessbackend.repositoriesMongo.SqsAggregationMessagesRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class WorkoutLoggerService extends com.example.fitnessbackend.service.Service {
  private final WorkoutSessionRepository workoutSessionRepository;
  private final WorkoutExerciseRepository workoutExerciseRepository;
  private final SetEntryRepository setEntryRepository;
  private final WorkoutSessionMapper workoutSessionMapper;
  private final WorkoutExerciseMapper workoutExerciseMapper;
  private final SetMapper setMapper;
  private final MessageSender messageSender;
  private final SqsAggregationMessagesRepo sqsAggregationMessagesRepo;

  @Value("${cloud.aws.endpoint.aggregation-queue.uri}")
  String aggregationQueueUrl;

  public WorkoutLoggerService(
      WorkoutSessionRepository workoutSessionRepository,
      WorkoutExerciseRepository workoutExerciseRepository,
      SetEntryRepository setEntryRepository,
      WorkoutSessionMapper workoutSessionMapper,
      WorkoutExerciseMapper workoutExerciseMapper,
      AuthTokenRepository authTokenRepository,
      JwtTokenProvider jwtTokenProvider,
      SetMapper setMapper,
      AuthenticationManager authenticationManager,
      MessageSender messageSender,
      SqsAggregationMessagesRepo sqsAggregationMessagesRepo) {
    super(jwtTokenProvider, authTokenRepository, authenticationManager);
    this.workoutSessionRepository = workoutSessionRepository;
    this.workoutExerciseRepository = workoutExerciseRepository;
    this.setEntryRepository = setEntryRepository;
    this.workoutSessionMapper = workoutSessionMapper;
    this.workoutExerciseMapper = workoutExerciseMapper;
    this.setMapper = setMapper;
    this.messageSender = messageSender;
    this.sqsAggregationMessagesRepo = sqsAggregationMessagesRepo;
  }

  public WorkoutSessionResponseDto createSession(WorkoutSessionDto dto) {
    UserModel userModel = this.getAuthenticatedUserModel();
    WorkoutSession workoutSession = workoutSessionMapper.toWorkoutSession(userModel, dto);
    WorkoutSession savedSession = workoutSessionRepository.save(workoutSession);
    String message = "Workout session created successfully";
    return workoutSessionMapper.toWSResponseDto(savedSession, message);
  }

  public AllUserWSResponseDto getAllUserWorkoutSessions() {
    UserModel userModel = this.getAuthenticatedUserModel();
    if (userModel == null) {
      throw new ResourceNotFoundException("User not found");
    }
    List<WorkoutSession> workoutSessions =
        workoutSessionRepository.findWorkoutSessionByUser(userModel);
    String message = "All workout sessions retrieved successfully";
    return workoutSessionMapper.toAllUserWSResponseDto(workoutSessions, message);
  }

  public WorkoutSessionResponseDto getWorkoutSession(Integer id) {
    WorkoutSession workoutSession =
        workoutSessionRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Workout session not found"));
    // make sure this session belongs to this user
    UserModel userModel = this.getAuthenticatedUserModel();
    if (!workoutSession.getUser().getId().equals(userModel.getId())) {
      throw new InvalidCredentialsException("User does not own this workout session");
    }
    String message = "Workout session retrieved successfully";
    return workoutSessionMapper.toWSResponseDto(workoutSession, message);
  }

  public WorkoutSessionResponseDto updateWorkoutSession(WorkoutSessionUpdateRequestDto dto) {
    WorkoutSession newWorkoutSession = workoutSessionMapper.fromWorkoutUpdateRequestDto(dto);
    this.validateSessionOwner(newWorkoutSession);
    WorkoutSession newSavedSession = workoutSessionRepository.save(newWorkoutSession);
    return workoutSessionMapper.toWSResponseDto(
        newSavedSession, "Workout session updated successfully");
  }

  public ResponseDto deleteWorkoutSession(Integer id) {
    WorkoutSession workoutSession =
        workoutSessionRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Workout session not found"));
    validateSessionOwner(workoutSession);
    workoutSessionRepository.delete(workoutSession);
    return new ResponseDto("Workout session deleted successfully");
  }

  public WorkoutExerciseResponseDto createExercise(WorkoutExerciseDto dto) {
    WorkoutExercise workoutExercise = workoutExerciseMapper.toWorkoutExercise(dto);
    validateExerciseOwner(workoutExercise);
    WorkoutExercise savedExercise = workoutExerciseRepository.save(workoutExercise);
    String message = "Workout exercise created successfully";
    return workoutExerciseMapper.toResponseDto(savedExercise, message);
  }

  public AllWEResponseDto getAllUserWorkoutExercises() {
    UserModel userModel = this.getAuthenticatedUserModel();
    List<WorkoutExercise> workoutExercises = getAllUserWorkoutExercises(userModel);
    return workoutExerciseMapper.toAllUserWorkoutExerciseResponseDto(
        "All workout exercises retrieved successfully", workoutExercises);
  }

  private List<WorkoutExercise> getAllUserWorkoutExercises(UserModel userModel) {
    List<WorkoutSession> workoutSessions =
        workoutSessionRepository.findWorkoutSessionByUser(userModel);
    List<WorkoutExercise> workoutExercises = new ArrayList<>();
    for (WorkoutSession workoutSession : workoutSessions) {
      workoutExercises.addAll(workoutSession.getWorkoutExercises());
    }
    return workoutExercises;
  }

  public AllWEResponseDto getAllSessionExercises(Integer sessionId) {
    WorkoutSession workoutSession =
        workoutSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Workout session not found"));
    validateSessionOwner(workoutSession);
    List<WorkoutExercise> workoutExercises = workoutSession.getWorkoutExercises();
    return workoutExerciseMapper.toAllUserWorkoutExerciseResponseDto(
        "All workout exercises retrieved successfully", workoutExercises);
  }

  public WorkoutExerciseResponseDto getWorkoutExerciseById(Integer id) {
    WorkoutExercise workoutExercise =
        workoutExerciseRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Workout exercise not found"));
    validateExerciseOwner(workoutExercise);
    String message = "Workout exercise retrieved successfully";
    return workoutExerciseMapper.toResponseDto(workoutExercise, message);
  }

  public ResponseDto deleteWorkoutExercise(Integer id) {
    WorkoutExercise workoutExercise =
        workoutExerciseRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Workout exercise not found"));
    validateExerciseOwner(workoutExercise);
    workoutExerciseRepository.delete(workoutExercise);
    return new ResponseDto("Workout exercise deleted successfully");
  }

  public SetEntryResponseDto createSetEntry(SetEntryDto dto) {
    SetEntry setEntry = setMapper.toSetEntry(dto);
    validateSetEntryOwner(setEntry);
    SetEntry savedSetEntry = setEntryRepository.save(setEntry);
    String message = "Set entry created successfully";
    SetEntryResponseDto response = setMapper.toSetEntryResponseDto(savedSetEntry, message);
    System.out.println(aggregationQueueUrl);
    try {
      messageSender.sendMessage(response, aggregationQueueUrl);
      this.ExerciseAggregationLambda(response, UUID.randomUUID().toString());
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
    return response;
  }

  public AllSEResponseDto getAllSetEntries() {
    UserModel userModel = this.getAuthenticatedUserModel();
    List<WorkoutExercise> workoutExercises = getAllUserWorkoutExercises(userModel);
    List<SetEntry> setEntries = new ArrayList<>();
    for (WorkoutExercise workoutExercise : workoutExercises) {
      setEntries.addAll(workoutExercise.getSetEntries());
    }
    String message = "All set entries retrieved successfully";
    return setMapper.toAllSEResponseDto(setEntries, message);
  }

  public AllSEResponseDto getAllSetEntriesFromExercise(Integer workoutExerciseId) {
    WorkoutExercise workoutExercise =
        workoutExerciseRepository
            .findById(workoutExerciseId)
            .orElseThrow(() -> new ResourceNotFoundException("Workout exercise not found"));
    validateExerciseOwner(workoutExercise);
    List<SetEntry> setEntries = workoutExercise.getSetEntries();
    String message = "All set entries retrieved successfully";
    return setMapper.toAllSEResponseDto(setEntries, message);
  }

  public SetEntryResponseDto getSetEntryById(Integer id) {
    SetEntry setEntry =
        setEntryRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Set entry not found"));
    validateSetEntryOwner(setEntry);
    String message = "Set entry retrieved successfully";
    return setMapper.toSetEntryResponseDto(setEntry, message);
  }

  public SetEntryResponseDto updateSetEntry(SetEntryUDto dto) {
    SetEntry updatedEntry = setMapper.fromSetEntryUDto(dto);
    validateSetEntryOwner(updatedEntry);
    SetEntry savedEntry = setEntryRepository.save(updatedEntry);
    String message = "Set entry updated successfully";
    return setMapper.toSetEntryResponseDto(savedEntry, message);
  }

  public ResponseDto deleteSetEntry(Integer id) {
    SetEntry setEntry =
        setEntryRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Set entry not found"));
    validateSetEntryOwner(setEntry);
    setEntryRepository.delete(setEntry);
    return new ResponseDto("Set entry deleted successfully");
  }

  private void validateSessionOwner(WorkoutSession workoutSession) {
    UserModel userModel = this.getAuthenticatedUserModel();
    UserModel workoutSessionUser = workoutSession.getUser();
    if (!Objects.equals(userModel.getId(), workoutSessionUser.getId())) {
      throw new InvalidCredentialsException("User does not own this workout session");
    }
  }

  private void validateExerciseOwner(WorkoutExercise workoutExercise) {
    UserModel userModel = this.getAuthenticatedUserModel();
    if (userModel == null) {
      throw new ResourceNotFoundException("User not found");
    }
    UserModel workoutSessionUser = workoutExercise.getWorkoutSession().getUser();
    if (!Objects.equals(userModel.getId(), workoutSessionUser.getId())) {
      throw new InvalidCredentialsException("User does not own this workout session");
    }
  }

  private void validateSetEntryOwner(SetEntry setEntry) {
    WorkoutExercise workoutExercise = setEntry.getWorkoutExercise();
    validateExerciseOwner(workoutExercise);
  }

  private void ExerciseAggregationLambda(SetEntryResponseDto setEntryDto, String messageId) {
    // We will check if this messageId is already in Mongo DB so we don't read a redundant message
    if (sqsAggregationMessagesRepo.existsByMessageId(messageId)) {
      System.out.println("Message with ID " + messageId + " already processed.");
    } else {
      // Save the messageId to MongoDB to avoid processing it again
      System.out.println("Processing message with ID: " + messageId);
      sqsAggregationMessagesRepo.save(
          SqsAggregationMessages.builder().messageId(messageId).build());
      // Now process the set entry by updating or creating relevant fields in ExerciseAggregation
      // collection
    }
  }
}
