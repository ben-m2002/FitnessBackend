package com.example.fitnessbackend.mappers;
import com.example.fitnessbackend.dtos.requests.workout.WorkoutSessionDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutSessionResponseDto;
import com.example.fitnessbackend.exceptions.ResourceNotFoundException;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.models.WorkoutSession;
import com.example.fitnessbackend.repositories.UserModelRepository;
import org.springframework.stereotype.Component;

@Component
public class WorkoutSessionMapper {

    private final UserModelRepository userModelRepository;

    public WorkoutSessionMapper(UserModelRepository userModelRepository) {
        this.userModelRepository = userModelRepository;
    }

    public WorkoutSession toWorkoutSession (WorkoutSessionDto dto){
        UserModel userModel = userModelRepository.findById(dto.getUserId()).orElse(null);
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
