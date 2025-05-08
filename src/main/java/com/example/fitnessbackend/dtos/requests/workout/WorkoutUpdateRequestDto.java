package com.example.fitnessbackend.dtos.requests.workout;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.models.WorkoutExercise;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class WorkoutUpdateRequestDto {
    @NotNull
    private Integer id;
    private String workoutNotes;
    private String workoutDescription; // Optional field for a description of the workout
    @NotNull
    private Integer workoutDifficulty; // 1-5 scale

    public WorkoutUpdateRequestDto(Integer id,
                                     String workoutNotes,
                                     String workoutDescription,
                                     Integer workoutDifficulty) {
        this.id = id;
        this.workoutNotes = workoutNotes;
        this.workoutDescription = workoutDescription;
        this.workoutDifficulty = workoutDifficulty;
    }

}

