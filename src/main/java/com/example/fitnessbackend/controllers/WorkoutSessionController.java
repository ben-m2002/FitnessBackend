package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.workout.WorkoutSessionDto;
import com.example.fitnessbackend.dtos.requests.workout.WorkoutUpdateRequestDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.AllUserWSResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutSessionResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutSessionResponseSDto;
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
            @RequestBody WorkoutSessionDto dto) {
        WorkoutSessionResponseDto response = workoutService.createSession(dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getAll")
    public ResponseEntity<AllUserWSResponseDto> getUserWorkoutSessions() {
        return ResponseEntity.ok(workoutService.getAllUserWorkoutSessions());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<WorkoutSessionResponseDto> getWorkoutSession(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(workoutService.getWorkoutSession(id));
    }

    @PostMapping("/update")
    public ResponseEntity<WorkoutSessionResponseDto> updateWorkoutSession(
            @RequestBody WorkoutUpdateRequestDto dto
    ){
        return ResponseEntity.ok(workoutService.updateWorkoutSession(dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDto> deleteWorkoutSession(
            @PathVariable Integer id
    ){
        return ResponseEntity.ok(workoutService.deleteWorkoutSession(id));
    }

}
