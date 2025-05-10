package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.workout.WorkoutExerciseDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.AllWEResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutExerciseResponseDto;
import com.example.fitnessbackend.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout-exercise")
public class WorkoutExerciseController extends Controller {
  private final WorkoutService workoutService;

  public WorkoutExerciseController(WorkoutService workoutService) {
    this.workoutService = workoutService;
  }

  @PostMapping("/create")
  public ResponseEntity<ResponseDto> createWorkoutExercise(
          @Valid @RequestBody WorkoutExerciseDto dto,
          BindingResult result) {
    if (result.hasErrors()) {
      return this.validateResult(result);
    }
    return ResponseEntity.ok(workoutService.createExercise(dto));
  }

  @GetMapping("/getAll")
  public ResponseEntity<ResponseDto> getAllWorkoutExercises() {
    return ResponseEntity.ok(workoutService.getAllUserWorkoutExercises());
  }

  @GetMapping("/get/AllFromSession/{sessionId}")
  public ResponseEntity<ResponseDto> getAllWorkoutExercisesFromSession(
      @PathVariable Integer sessionId) {
    return ResponseEntity.ok(workoutService.getAllSessionExercises(sessionId));
  }

  @GetMapping("get/{id}")
  public ResponseEntity<ResponseDto> getWorkoutExerciseById(
      @PathVariable Integer id) {
    return ResponseEntity.ok(workoutService.getWorkoutExerciseById(id));
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ResponseDto> deleteWorkoutExercise(@PathVariable Integer id) {
    return ResponseEntity.ok(workoutService.deleteWorkoutExercise(id));
  }
}
