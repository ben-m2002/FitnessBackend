package com.example.fitnessbackend.repositories;

import com.example.fitnessbackend.models.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Integer> {

}
