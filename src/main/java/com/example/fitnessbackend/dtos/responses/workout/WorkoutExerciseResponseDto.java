package com.example.fitnessbackend.dtos.responses.workout;


import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.models.SetEntry;
import lombok.Data;

import java.util.List;

@Data
public class WorkoutExerciseResponseDto extends ResponseDto {
    private Integer id;
    private Integer sessionId;
    private String exerciseName;
    private List<SetEntry> setEntries;

    public WorkoutExerciseResponseDto(String token, String message, Integer id, Integer sessionId,
                                      String exerciseName,
                                      List<SetEntry> setEntries) {
        super(token, message);
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseName = exerciseName;
        this.setEntries = setEntries;
    }

}
