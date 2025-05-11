package com.example.fitnessbackend.dtos.requests.workout;

import com.example.fitnessbackend.nonPersistData.WeightMeasurementType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
  @NotNull private WeightMeasurementType measurementType;

  @NotNull
  @Min(1)
  @Max(5)
  private Integer difficulty;
}
