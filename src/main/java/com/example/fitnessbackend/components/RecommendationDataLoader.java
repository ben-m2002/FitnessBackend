package com.example.fitnessbackend.components;

import com.example.fitnessbackend.nonPersistData.ExerciseName;
import com.example.fitnessbackend.nonPersistData.SetEntryRecommendationSpecification;
import com.example.fitnessbackend.nonPersistData.WeightMeasurementType;
import com.example.fitnessbackend.service.RecommendationDataService;
import com.example.fitnessbackend.service.RecommendationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("test")
public class RecommendationDataLoader implements CommandLineRunner {
  private final RecommendationDataService recommendationDataService;
  private final boolean seedSetEntryData;

  public RecommendationDataLoader(
      RecommendationDataService recommendationDataService,
      @Value("${seedSetEntryData}") boolean seedSetEntryData) {
    this.recommendationDataService = recommendationDataService;
    this.seedSetEntryData = seedSetEntryData;
  }

  @Override
  public void run(String... args) {
    if (seedSetEntryData) {
      SetEntryRecommendationSpecification spec =
          SetEntryRecommendationSpecification.builder()
              .exerciseNames(List.of(ExerciseName.SQUAT, ExerciseName.BENCH_PRESS))
              .numSetEntries(5)
              .weightMeasurementType(WeightMeasurementType.POUNDS)
              .maxDifficulty(5)
              .minDifficulty(1)
              .maxWeight(300)
              .minWeight(50)
              .maxReps(15)
              .minReps(3)
              .maxNumSets(5)
              .minNumSets(1)
              .build();
      recommendationDataService.createSetEntryRecommendationData(spec);
    }
  }
}
