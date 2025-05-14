package com.example.fitnessbackend.dtos.requests.reccomendation;

import com.example.fitnessbackend.models.WorkoutExercise;
import com.example.fitnessbackend.nonPersistData.WeightMeasurementType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SetEntryRecommendDto {
  @NotNull private Integer exerciseId;

  @Max(5)
  @Min(1)
  @NotNull
  private Integer difficulty;

  @NotNull private WeightMeasurementType measurementType;
}
