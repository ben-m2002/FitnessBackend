package com.example.fitnessbackend.repositories;

import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.models.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Integer> {
    List<WorkoutSession> findWorkoutSessionByUser(UserModel user);
}
