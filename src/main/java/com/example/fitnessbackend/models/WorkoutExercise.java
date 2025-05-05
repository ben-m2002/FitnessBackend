package com.example.fitnessbackend.models;


import com.example.fitnessbackend.ExerciseTypeFactory;
import com.example.fitnessbackend.nonPersistData.ExerciseName;
import com.example.fitnessbackend.nonPersistData.ExerciseType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "workout_exercise")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WorkoutExercise {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_session_id")
    @JsonBackReference
    private WorkoutSession workoutSession;

    private ExerciseName exerciseName;

    @Transient
    private ExerciseType exerciseType;


    @OneToMany(mappedBy = "workoutExercise", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<SetEntry> setEntries;

    private Date creationTimestamp;


    @PostLoad
    private void loadExerciseType() {
        this.exerciseType = ExerciseTypeFactory.createExercise(exerciseName);
    }

    @PrePersist // right before we save it to the database
    private void setCreationTimestamp() {
        this.creationTimestamp = new Date();
    }

}