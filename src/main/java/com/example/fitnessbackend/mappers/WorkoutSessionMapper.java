package com.example.fitnessbackend.mappers;
import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.requests.workout.WorkoutSessionDto;
import com.example.fitnessbackend.dtos.requests.workout.WorkoutUpdateRequestDto;
import com.example.fitnessbackend.dtos.responses.workout.AllUserWSResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutSessionResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutSessionResponseSDto;
import com.example.fitnessbackend.exceptions.ResourceNotFoundException;
import com.example.fitnessbackend.models.AuthToken;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.models.WorkoutSession;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.example.fitnessbackend.repositories.UserModelRepository;
import com.example.fitnessbackend.repositories.WorkoutSessionRepository;
import com.example.fitnessbackend.service.AuthService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WorkoutSessionMapper {

    private final WorkoutSessionRepository workoutSessionRepository;

    public WorkoutSessionMapper(WorkoutSessionRepository workoutSessionRepository) {
        this.workoutSessionRepository = workoutSessionRepository;
    }

    public WorkoutSession toWorkoutSession (UserModel userModel,WorkoutSessionDto dto){
        return WorkoutSession.builder().workoutDescription(dto.getWorkoutDescription())
                .user(userModel)
                .build();
    }

    public WorkoutSessionResponseDto toWSResponseDto(WorkoutSession workoutSession, String message) {
        return new WorkoutSessionResponseDto(
                message,
                workoutSession.getId(),
                workoutSession.getWorkoutDate(),
                workoutSession.getWorkoutNotes(),
                workoutSession.getWorkoutDescription(),
                workoutSession.getWorkoutDifficulty(),
                workoutSession.getUser().getId(),
                workoutSession.getWorkoutExercises()
        );
    }

    public AllUserWSResponseDto toAllUserWSResponseDto(
            List<WorkoutSession> workoutSessions,
            String message) {
        return new AllUserWSResponseDto(
                message,
                workoutSessions.stream()
                        .map(workoutSession -> new WorkoutSessionResponseSDto(
                                workoutSession.getId(),
                                workoutSession.getWorkoutDate(),
                                workoutSession.getWorkoutNotes(),
                                workoutSession.getWorkoutDescription(),
                                workoutSession.getWorkoutDifficulty(),
                                workoutSession.getUser().getId()
                        ))
                        .toList());

    }

    public WorkoutSession fromWorkoutUpdateRequestDto (WorkoutUpdateRequestDto dto){
        Optional<WorkoutSession> workoutSession = workoutSessionRepository.findById(dto.getId());
        if (workoutSession.isEmpty()){
            throw new ResourceNotFoundException("Workout session not found");
        }
        WorkoutSession session = workoutSession.get();
        session.setWorkoutNotes(dto.getWorkoutNotes());
        session.setWorkoutDescription(dto.getWorkoutDescription());
        session.setWorkoutDifficulty(dto.getWorkoutDifficulty());
        return session;
    }

}
