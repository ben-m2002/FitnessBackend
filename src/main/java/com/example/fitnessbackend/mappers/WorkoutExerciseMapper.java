package com.example.fitnessbackend.mappers;


import com.example.fitnessbackend.dtos.requests.workout.WorkoutExerciseDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutExerciseResponseDto;
import com.example.fitnessbackend.exceptions.ResourceNotFoundException;
import com.example.fitnessbackend.models.WorkoutExercise;
import com.example.fitnessbackend.models.WorkoutSession;
import com.example.fitnessbackend.nonPersistData.ExerciseName;
import com.example.fitnessbackend.repositories.WorkoutSessionRepository;
import org.springframework.stereotype.Component;

@Component

public class WorkoutExerciseMapper {
    private final WorkoutSessionRepository workoutSessionRepository;

    public WorkoutExerciseMapper(WorkoutSessionRepository workoutSessionRepository) {
        this.workoutSessionRepository = workoutSessionRepository;
    }

    public WorkoutExercise toWorkoutExercise(WorkoutExerciseDto dto) {
        WorkoutSession session = workoutSessionRepository.findById(dto.getWorkoutSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Workout session not found"));
        return WorkoutExercise.builder()
               .workoutSession(session)
                .exerciseName(ExerciseName.valueOf(dto.getExerciseName()))
                .build();
    }

    public WorkoutExerciseResponseDto toResponseDto(WorkoutExercise exercise, String token, String message) {
        return new WorkoutExerciseResponseDto(
                token,
                message,
                exercise.getId(),
                exercise.getWorkoutSession().getId(),
                exercise.getExerciseName().name(),
                exercise.getSetEntries()
        );
    }
}
