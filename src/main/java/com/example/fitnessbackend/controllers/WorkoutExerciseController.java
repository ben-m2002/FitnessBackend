package com.example.fitnessbackend.controllers;


import com.example.fitnessbackend.dtos.requests.workout.WorkoutExerciseDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.AllWEResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutExerciseResponseDto;
import com.example.fitnessbackend.service.WorkoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout-exercise")
public class WorkoutExerciseController {
    private final WorkoutService workoutService;

    public WorkoutExerciseController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }


    @GetMapping("/getAll")
    public ResponseEntity<AllWEResponseDto> getAllWorkoutExercises() {
        return ResponseEntity.ok(workoutService.getAllUserWorkoutExercises());
    }


    @GetMapping("/get/AllFromSession/{sessionId}")
    public ResponseEntity<AllWEResponseDto> getAllWorkoutExercisesFromSession(
            @PathVariable Integer sessionId) {
        return ResponseEntity.ok(workoutService.getAllSessionExercises(sessionId));
    }

    @PostMapping("/create")
    public ResponseEntity<WorkoutExerciseResponseDto> createWorkout(
            @RequestBody WorkoutExerciseDto dto) {
        return ResponseEntity.ok(workoutService.createExercise(dto));
    }

   @GetMapping("get/{id}")
    public ResponseEntity<WorkoutExerciseResponseDto> getWorkoutExerciseById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(workoutService.getWorkoutExerciseById(id));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDto> deleteWorkoutExercise(
            @PathVariable Integer id
    ){
        return ResponseEntity.ok(workoutService.deleteWorkoutExercise(id));
    }


}
