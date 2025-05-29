package com.example.fitnessbackend.entities;


import com.example.fitnessbackend.nonPersistData.ExerciseName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("exercise_aggregation")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExerciseAggregation {
    private Integer userId;
    private ExerciseName exerciseName;

    private Integer numLogged;

    private Integer totalSets;
    private Double averageSets;

    private Integer totalReps;
    private Double averageReps;

    private Double totalVolume;

    private Integer oneRepMax;
}
