package com.example.fitnessbackend.dtos.requests.workout;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetEntryDto {
    private Integer workoutExerciseId;
    private Integer weight;
    private Integer numSets;
    private Integer reps;
    private Integer difficulty;
}
