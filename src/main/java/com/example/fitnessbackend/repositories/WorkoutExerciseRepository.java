package com.example.fitnessbackend.repositories;

import com.example.fitnessbackend.models.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Integer> {

}
