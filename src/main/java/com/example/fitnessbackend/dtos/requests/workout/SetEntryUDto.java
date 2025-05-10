package com.example.fitnessbackend.dtos.requests.workout;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetEntryUDto {
  @NotNull private Integer id;
  @NotNull private Integer weight;
  @NotNull private Integer numSets;
  @NotNull private Integer reps;
  @NotNull private Integer difficulty;
}
