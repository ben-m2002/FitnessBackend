package com.example.fitnessbackend.dtos.requests.workout;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class WorkoutExerciseDto {
    private Integer workoutSessionId;
    private String exerciseName;
}
