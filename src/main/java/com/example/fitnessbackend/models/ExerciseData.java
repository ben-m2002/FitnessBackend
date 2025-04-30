package com.example.fitnessbackend.models;


import com.example.fitnessbackend.ExerciseTypeFactory;
import com.example.fitnessbackend.nonPersistData.ExerciseName;
import com.example.fitnessbackend.nonPersistData.ExerciseType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "exercise_data")
@Entity
@NoArgsConstructor
@Data
public class ExerciseData {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserModel user;

    @Transient
    private ExerciseType exerciseType;

    private Long totalSets;
    private Long totalReps;
    private BigDecimal avgSets;
    private BigDecimal avgReps;
    private BigDecimal best1rm;
    private LocalDateTime lastUpdated;
    private ExerciseName exerciseName;

    @PostLoad
    private void loadExerciseType() {
       this.exerciseType = ExerciseTypeFactory.createExercise(exerciseName);
    }
}
