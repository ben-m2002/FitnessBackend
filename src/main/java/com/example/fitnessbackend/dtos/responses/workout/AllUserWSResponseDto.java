package com.example.fitnessbackend.dtos.responses.workout;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import lombok.AllArgsConstructor;
import java.util.List;
import lombok.Data;



@SuppressWarnings("CheckStyle")
@Data
@AllArgsConstructor
public class AllUserWSResponseDto extends ResponseDto {
  private List<WorkoutSessionResponseSDto> workoutSessions;

  public AllUserWSResponseDto(String message, List<WorkoutSessionResponseSDto> workoutSessions) {
    super(message);
    this.workoutSessions = workoutSessions;
  }
}
