package com.example.fitnessbackend.dtos.responses.workout;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.models.WorkoutExercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSessionResponseSDto extends ResponseDto {
    private Integer id;
    private LocalDate workoutDate;
    private String workoutNotes;
    private String workoutDescription; // Optional field for a description of the workout
    private Integer workoutDifficulty; // 1-5 scale
    private Integer userId;

    public WorkoutSessionResponseSDto(String message,
                                     Integer id, LocalDate workoutDate,
                                     String workoutNotes,
                                     String workoutDescription,
                                     Integer workoutDifficulty,
                                     Integer userId
                                     ) {
        super(message);
        this.id = id;
        this.workoutDate = workoutDate;
        this.workoutNotes = workoutNotes;
        this.workoutDescription = workoutDescription;
        this.workoutDifficulty = workoutDifficulty;
        this.userId = userId;
    }
}
