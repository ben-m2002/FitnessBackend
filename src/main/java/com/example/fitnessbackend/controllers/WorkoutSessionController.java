package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.workout.WorkoutSessionDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutSessionResponseDto;
import com.example.fitnessbackend.service.WorkoutService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout-session")
public class WorkoutSessionController {
    private final WorkoutService workoutService;

    public WorkoutSessionController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping("/create")
    public ResponseEntity<WorkoutSessionResponseDto> createWorkout(
            HttpServletRequest request
            ,@RequestBody WorkoutSessionDto dto) {
        WorkoutSessionResponseDto response = workoutService.createSession(request, dto);
        return ResponseEntity.ok(response);
    }


}
