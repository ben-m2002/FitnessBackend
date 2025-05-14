package com.example.fitnessbackend.nonPersistData;

import com.example.fitnessbackend.models.WorkoutExercise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SetEntryRecommendationSpecification {
    private List<ExerciseName> exerciseNames;
    private Integer numSetEntries;
    private WeightMeasurementType weightMeasurementType;
    private Integer maxDifficulty;
    private Integer minDifficulty;
    private Integer maxWeight;
    private Integer minWeight;
    private Integer maxReps;
    private Integer minReps;
    private Integer maxNumSets;
    private Integer minNumSets;
}
