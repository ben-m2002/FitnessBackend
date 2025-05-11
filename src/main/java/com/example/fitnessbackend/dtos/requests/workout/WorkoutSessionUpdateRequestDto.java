package com.example.fitnessbackend.dtos.requests.workout;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class WorkoutSessionUpdateRequestDto {
    @NotNull
    private Integer id;
    private String workoutNotes;
    private String workoutDescription; // Optional field for a description of the workout
    @NotNull
    @Min(1)
    @Max(5)
    private Integer workoutDifficulty; // 1-5 scale

    public WorkoutSessionUpdateRequestDto(Integer id,
                                          String workoutNotes,
                                          String workoutDescription,
                                          Integer workoutDifficulty) {
        this.id = id;
        this.workoutNotes = workoutNotes;
        this.workoutDescription = workoutDescription;
        this.workoutDifficulty = workoutDifficulty;
    }

}

