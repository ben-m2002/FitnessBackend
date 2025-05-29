package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.workout.WorkoutExerciseDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.service.WorkoutLoggerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout-exercise")
public class WorkoutExerciseController extends Controller {
  private final WorkoutLoggerService workoutLoggerService;

  public WorkoutExerciseController(WorkoutLoggerService workoutLoggerService) {
    this.workoutLoggerService = workoutLoggerService;
  }

  @PostMapping("/create")
  public ResponseEntity<ResponseDto> createWorkoutExercise(
          @Valid @RequestBody WorkoutExerciseDto dto,
          BindingResult result) {
    if (result.hasErrors()) {
      return this.validateResult(result);
    }
    return ResponseEntity.ok(workoutLoggerService.createExercise(dto));
  }

  @GetMapping("/getAll")
  public ResponseEntity<ResponseDto> getAllWorkoutExercises() {
    return ResponseEntity.ok(workoutLoggerService.getAllUserWorkoutExercises());
  }

  @GetMapping("/get/AllFromSession/{sessionId}")
  public ResponseEntity<ResponseDto> getAllWorkoutExercisesFromSession(
      @PathVariable Integer sessionId) {
    return ResponseEntity.ok(workoutLoggerService.getAllSessionExercises(sessionId));
  }

  @GetMapping("get/{id}")
  public ResponseEntity<ResponseDto> getWorkoutExerciseById(
      @PathVariable Integer id) {
    return ResponseEntity.ok(workoutLoggerService.getWorkoutExerciseById(id));
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ResponseDto> deleteWorkoutExercise(@PathVariable Integer id) {
    return ResponseEntity.ok(workoutLoggerService.deleteWorkoutExercise(id));
  }
}
