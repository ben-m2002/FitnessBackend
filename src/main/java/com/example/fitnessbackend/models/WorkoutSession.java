package com.example.fitnessbackend.models;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    private LocalDate workoutDate;
    private String workoutNotes;
    private Integer workoutDifficulty;
}
