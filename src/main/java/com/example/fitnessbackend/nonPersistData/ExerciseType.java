package com.example.fitnessbackend.nonPersistData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


// Create some type of factory class to create exercise types

@Data
@Builder
@AllArgsConstructor
public class ExerciseType {
    private String name;
    private String description;
}
