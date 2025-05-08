package com.example.fitnessbackend.dtos.requests.workout;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetEntryDto {
    @NotNull @Positive
    private Integer workoutExerciseId;
    @NotNull @Positive
    private Integer weight;
    @NotNull @Positive
    private Integer numSets;
    @NotNull @Positive
    private Integer reps;
    @NotNull @Positive
    private Integer difficulty;
}
