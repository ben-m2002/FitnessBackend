package com.example.fitnessbackend.models;


import com.example.fitnessbackend.nonPersistData.WeightMeasurementType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.joda.time.DateTime;

import java.util.Date;

@Table(name = "set_entry")
@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
@ToString(exclude = "workoutExercise")
public class SetEntry {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Integer id;

    private Integer numSets;
    private Integer reps;
    private Integer weight;
    private WeightMeasurementType measurementType;
    private Integer difficulty;
    private Date creationTimestamp;
    private DateTime aggregatedTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private WorkoutExercise workoutExercise;

    @PrePersist
    public void prePersist() {
        this.creationTimestamp = new Date();
    }
}
