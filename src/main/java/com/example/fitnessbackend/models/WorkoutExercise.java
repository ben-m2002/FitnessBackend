package com.example.fitnessbackend.models;


import com.example.fitnessbackend.ExerciseTypeFactory;
import com.example.fitnessbackend.nonPersistData.ExerciseName;
import com.example.fitnessbackend.nonPersistData.ExerciseType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "workout_exercise")
@Entity
@NoArgsConstructor
@Data
public class WorkoutExercise {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_session_id")
    private WorkoutSession workoutSession;

    private ExerciseName exerciseName;

    @Transient
    private ExerciseType exerciseType;


    @OneToMany(mappedBy = "workoutExercise", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SetEntry> setEntries = new ArrayList<>();


    @PostLoad
    private void loadExerciseType() {
        ExerciseTypeFactory.createExercise(exerciseName);
    }

}