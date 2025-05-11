package com.example.fitnessbackend.dtos.responses.workout;


import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.nonPersistData.WeightMeasurementType;
import lombok.Data;

@Data
public class SetEntryResponseDto extends ResponseDto {
    private Integer id;
    private Integer workoutExerciseId;
    private Integer weight;
    private Integer numSets;
    private Integer reps;
    private WeightMeasurementType measurementType;
    private Integer difficulty;

    public SetEntryResponseDto(String message ,Integer id,
                               Integer workoutExerciseId, Integer weight,
                               Integer numSets, Integer reps, WeightMeasurementType measurementType, Integer difficulty) {
        super(message);
        this.id = id;
        this.workoutExerciseId = workoutExerciseId;
        this.weight = weight;
        this.numSets = numSets;
        this.reps = reps;
        this.measurementType = measurementType;
        this.difficulty = difficulty;
    }

    public SetEntryResponseDto(Integer id,
                               Integer workoutExerciseId, Integer weight,
                               Integer numSets, Integer reps, WeightMeasurementType measurementType, Integer difficulty) {
        this.id = id;
        this.workoutExerciseId = workoutExerciseId;
        this.weight = weight;
        this.numSets = numSets;
        this.reps = reps;
        this.measurementType = measurementType;
        this.difficulty = difficulty;
    }

}
