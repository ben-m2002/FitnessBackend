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
public class WorkoutSessionResponseDto extends ResponseDto {
    private Integer id;
    private LocalDate workoutDate;
    private String workoutNotes;
    private String workoutDescription; // Optional field for a description of the workout
    private Integer workoutDifficulty; // 1-5 scale
    private Integer userId;
    private List<WorkoutExercise> workoutExercises;

    public WorkoutSessionResponseDto(String token, String message,
                              Integer id, LocalDate workoutDate,
                              String workoutNotes,
                                String workoutDescription,
                              Integer workoutDifficulty,
                              Integer userId,
                                     List<WorkoutExercise> workoutExercises) {
        super(token, message);
        this.id = id;
        this.workoutDate = workoutDate;
        this.workoutNotes = workoutNotes;
        this.workoutDescription = workoutDescription;
        this.workoutDifficulty = workoutDifficulty;
        this.userId = userId;
        this.workoutExercises = workoutExercises;
    }

}
