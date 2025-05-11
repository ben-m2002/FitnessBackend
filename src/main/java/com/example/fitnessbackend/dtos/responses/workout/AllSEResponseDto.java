package com.example.fitnessbackend.dtos.responses.workout;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import java.util.List;
import lombok.Data;



@SuppressWarnings("CheckStyle")
@Data
public class AllSEResponseDto extends ResponseDto {
  private List<SetEntryResponseDto> setEntries;

  public AllSEResponseDto(String message, List<SetEntryResponseDto> setEntries) {
    super(message);
    this.setEntries = setEntries;
  }
}
