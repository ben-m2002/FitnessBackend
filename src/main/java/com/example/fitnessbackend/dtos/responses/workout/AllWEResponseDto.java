package com.example.fitnessbackend.dtos.responses.workout;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AllWEResponseDto extends ResponseDto {
    private List<WorkoutExerciseResponseDto> workoutExercises;
    public AllWEResponseDto(String message, List<WorkoutExerciseResponseDto> workoutExercises) {
        super(message);
        this.workoutExercises = workoutExercises;
    }
}
