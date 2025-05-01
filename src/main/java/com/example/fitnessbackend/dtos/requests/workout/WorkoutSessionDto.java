package com.example.fitnessbackend.dtos.requests.workout;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkoutSessionDto {
    private Integer userId;
    private String workoutDescription;
}
