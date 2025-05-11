package com.example.fitnessbackend.models;


import com.example.fitnessbackend.nonPersistData.WeightMeasurementType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "set_entry")
@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class SetEntry {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Integer id;

    private Integer numSets;
    private Integer reps;
    private Integer weight;
    private WeightMeasurementType measurementType;
    private Integer difficulty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private WorkoutExercise workoutExercise;
}
