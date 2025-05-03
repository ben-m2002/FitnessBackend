package com.example.fitnessbackend.models;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Table(name = "workout_sessions")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @OneToMany(mappedBy = "workoutSession", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<WorkoutExercise> workoutExercises;

    private LocalDate workoutDate;
    private String workoutNotes; // This is more so saying how the workout went
    private String workoutDescription; // Optional field for a description of the workout
    private Integer workoutDifficulty; // 1-5 scale, and this is updated later

    @PrePersist
    public void prePersist() {
        if (workoutDate == null) {
            workoutDate = LocalDate.now();
        }
    }

}
