package com.example.fitnessbackend.mappers;
import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.requests.workout.WorkoutSessionDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutSessionResponseDto;
import com.example.fitnessbackend.exceptions.ResourceNotFoundException;
import com.example.fitnessbackend.models.AuthToken;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.models.WorkoutSession;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.example.fitnessbackend.repositories.UserModelRepository;
import com.example.fitnessbackend.service.AuthService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WorkoutSessionMapper {

    private final UserModelRepository userModelRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public WorkoutSessionMapper(UserModelRepository userModelRepository, AuthTokenRepository authTokenRepository, JwtTokenProvider jwtTokenProvider) {
        this.userModelRepository = userModelRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public WorkoutSession toWorkoutSession (String accessToken,WorkoutSessionDto dto){
        if (accessToken == null) {
            throw new IllegalArgumentException("Refresh token is required");
        }
        UserModel userModel = userModelRepository.findByEmail(jwtTokenProvider.getEmail(accessToken));
        if (userModel == null) {
            throw new ResourceNotFoundException("User for this workout session not found");
        }
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

}
