package com.example.fitnessbackend.dtos.responses.workout;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class AllUserWSResponseDto extends ResponseDto {
  private List<WorkoutSessionResponseSDto> workoutSessions;

  public AllUserWSResponseDto(String message, List<WorkoutSessionResponseSDto> workoutSessions) {
    super(message);
    this.workoutSessions = workoutSessions;
  }
}
