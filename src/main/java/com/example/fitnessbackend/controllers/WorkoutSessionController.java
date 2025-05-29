package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.workout.WorkoutSessionDto;
import com.example.fitnessbackend.dtos.requests.workout.WorkoutSessionUpdateRequestDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.WorkoutSessionResponseDto;
import com.example.fitnessbackend.service.WorkoutLoggerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout-session")
public class WorkoutSessionController extends Controller {
  private final WorkoutLoggerService workoutLoggerService;

  public WorkoutSessionController(WorkoutLoggerService workoutLoggerService) {
    this.workoutLoggerService = workoutLoggerService;
  }

  @PostMapping("/create")
  public ResponseEntity<ResponseDto> createWorkoutSession(
      @RequestBody WorkoutSessionDto dto) {
    WorkoutSessionResponseDto response = workoutLoggerService.createSession(dto);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/getAll")
  public ResponseEntity<ResponseDto> getUserWorkoutSessions() {
    return ResponseEntity.ok(workoutLoggerService.getAllUserWorkoutSessions());
  }

  @GetMapping("/get/{id}")
  public ResponseEntity<ResponseDto> getWorkoutSession(@PathVariable Integer id) {
    return ResponseEntity.ok(workoutLoggerService.getWorkoutSession(id));
  }

  @PostMapping("/update")
  public ResponseEntity<ResponseDto> updateWorkoutSession(
      @Valid @RequestBody WorkoutSessionUpdateRequestDto dto,
      BindingResult result) {
    if (result.hasErrors()) {
        return validateResult(result);
    }
    return ResponseEntity.ok(workoutLoggerService.updateWorkoutSession(dto));
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ResponseDto> deleteWorkoutSession(@PathVariable Integer id) {
    return ResponseEntity.ok(workoutLoggerService.deleteWorkoutSession(id));
  }
}
