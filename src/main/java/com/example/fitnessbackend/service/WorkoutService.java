package com.example.fitnessbackend.service;
import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.requests.workout.SetEntryDto;
import com.example.fitnessbackend.dtos.requests.workout.WorkoutExerciseDto;
import com.example.fitnessbackend.dtos.requests.workout.WorkoutSessionDto;
import com.example.fitnessbackend.dtos.responses.workout.SetEntryResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutExerciseResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutSessionResponseDto;
import com.example.fitnessbackend.exceptions.InvalidCredentialsException;
import com.example.fitnessbackend.exceptions.ResourceNotFoundException;
import com.example.fitnessbackend.mappers.SetMapper;
import com.example.fitnessbackend.mappers.WorkoutExerciseMapper;
import com.example.fitnessbackend.mappers.WorkoutSessionMapper;
import com.example.fitnessbackend.models.*;
import com.example.fitnessbackend.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;




@Service
public class WorkoutService extends com.example.fitnessbackend.service.Service {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final SetEntryRepository setEntryRepository;
    private final WorkoutSessionMapper workoutSessionMapper;
    private final WorkoutExerciseMapper workoutExerciseMapper;
    private final SetMapper setMapper;
    private final UserModelRepository userModelRepository;

    public WorkoutService(WorkoutSessionRepository workoutSessionRepository, WorkoutExerciseRepository workoutExerciseRepository, SetEntryRepository setEntryRepository, WorkoutSessionMapper workoutSessionMapper, WorkoutExerciseMapper workoutExerciseMapper, AuthTokenRepository authTokenRepository, JwtTokenProvider jwtTokenProvider, SetMapper setMapper, UserModelRepository userModelRepository) {
        super(jwtTokenProvider, authTokenRepository);
        this.workoutSessionRepository = workoutSessionRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.setEntryRepository = setEntryRepository;
        this.workoutSessionMapper = workoutSessionMapper;
        this.workoutExerciseMapper = workoutExerciseMapper;
        this.setMapper = setMapper;
        this.userModelRepository = userModelRepository;
    }

    public WorkoutSessionResponseDto createSession (HttpServletRequest request, WorkoutSessionDto dto){
        String accessToken = jwtTokenProvider.extractAccessToken(request);
        WorkoutSession workoutSession = workoutSessionMapper.toWorkoutSession(accessToken, dto);
        WorkoutSession savedSession = workoutSessionRepository.save(workoutSession);
        String message = "Workout session created successfully";
        return workoutSessionMapper.toWSResponseDto(savedSession, message);
    }

    public WorkoutExerciseResponseDto createExercise(HttpServletRequest request,WorkoutExerciseDto dto){
        WorkoutExercise workoutExercise = workoutExerciseMapper.toWorkoutExercise(dto);
        String accessToken = jwtTokenProvider.extractAccessToken(request);
        validateExerciseOwner(accessToken,workoutExercise);
        WorkoutExercise savedExercise = workoutExerciseRepository.save(workoutExercise);
        String message = "Workout exercise created successfully";
        return workoutExerciseMapper.toResponseDto(savedExercise, message);
    }

    public SetEntryResponseDto createSetEntry(HttpServletRequest request,SetEntryDto dto){
        SetEntry setEntry = setMapper.toSetEntry(dto);
        String accessToken = jwtTokenProvider.extractAccessToken(request);
        validateSetEntryOwner(accessToken, setEntry);
        SetEntry savedSetEntry = setEntryRepository.save(setEntry);
        String message = "Set entry created successfully";
        return setMapper.toSetEntryResponseDto(savedSetEntry, message);
    }

    private boolean validateExerciseOwner (String accessToken, WorkoutExercise workoutExercise){
        String email = jwtTokenProvider.getEmail(accessToken);
        UserModel userModel = userModelRepository.findByEmail(email);
        if (userModel == null) {
            throw new ResourceNotFoundException("User not found");
        }
        UserModel workoutSessionUser = workoutExercise.getWorkoutSession().getUser();
        if (userModel != workoutSessionUser) {
            throw new InvalidCredentialsException("User does not own this workout session");
        }
        return true;
    }

    private boolean validateSetEntryOwner (String accessToken, SetEntry setEntry){
        WorkoutExercise workoutExercise = setEntry.getWorkoutExercise();
        return validateExerciseOwner(accessToken, workoutExercise);
    }
}
