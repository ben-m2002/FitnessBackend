package com.example.fitnessbackend.service;
import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.requests.workout.WorkoutExerciseDto;
import com.example.fitnessbackend.dtos.requests.workout.WorkoutSessionDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutExerciseResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutSessionResponseDto;
import com.example.fitnessbackend.mappers.WorkoutExerciseMapper;
import com.example.fitnessbackend.mappers.WorkoutSessionMapper;
import com.example.fitnessbackend.models.AuthToken;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.models.WorkoutExercise;
import com.example.fitnessbackend.models.WorkoutSession;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.example.fitnessbackend.repositories.WorkoutExerciseRepository;
import com.example.fitnessbackend.repositories.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkoutService extends com.example.fitnessbackend.service.Service {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutSessionMapper workoutSessionMapper;
    private final WorkoutExerciseMapper workoutExerciseMapper;

    public WorkoutService(WorkoutSessionRepository workoutSessionRepository, WorkoutExerciseRepository workoutExerciseRepository, WorkoutSessionMapper workoutSessionMapper, WorkoutExerciseMapper workoutExerciseMapper, AuthTokenRepository authTokenRepository, JwtTokenProvider jwtTokenProvider) {
        super(jwtTokenProvider, authTokenRepository);
        this.workoutSessionRepository = workoutSessionRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.workoutSessionMapper = workoutSessionMapper;
        this.workoutExerciseMapper = workoutExerciseMapper;
    }

    public WorkoutSessionResponseDto createSession (WorkoutSessionDto dto){
        WorkoutSession workoutSession = workoutSessionMapper.toWorkoutSession(dto);
        WorkoutSession savedSession = workoutSessionRepository.save(workoutSession);
        String message = "Workout session created successfully";
        return workoutSessionMapper.toWSResponseDto(savedSession, message);
    }

    public WorkoutExerciseResponseDto createExercise(WorkoutExerciseDto dto){
        WorkoutExercise workoutExercise = workoutExerciseMapper.toWorkoutExercise(dto);
        WorkoutExercise savedExercise = workoutExerciseRepository.save(workoutExercise);
        String message = "Workout exercise created successfully";
        return workoutExerciseMapper.toResponseDto(savedExercise, message);
    }
}
