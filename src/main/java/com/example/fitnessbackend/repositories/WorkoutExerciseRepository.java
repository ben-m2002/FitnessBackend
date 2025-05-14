package com.example.fitnessbackend.repositories;

import com.example.fitnessbackend.models.WorkoutExercise;
import com.example.fitnessbackend.nonPersistData.ExerciseName;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Integer> {
    List<WorkoutExercise> findByWorkoutSession_User_IdAndExerciseName(Integer workoutSessionUserId, ExerciseName exerciseName);
}
