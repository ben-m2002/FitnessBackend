package com.example.fitnessbackend.dtos.requests.workout;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class WorkoutExerciseDto {
    @NotNull
    private Integer workoutSessionId;
    @NotNull @NotEmpty
    private String exerciseName;
}
