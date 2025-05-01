package com.example.fitnessbackend.models;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Table(name = "workout_sessions")
@Entity
@NoArgsConstructor
@Data
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @OneToMany(mappedBy = "workoutSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkoutExercise> workoutExercises;

    private LocalDate workoutDate;
    private String workoutNotes;
    private Integer workoutDifficulty;

}
