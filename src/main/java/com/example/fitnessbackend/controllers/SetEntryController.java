package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.workout.SetEntryDto;
import com.example.fitnessbackend.dtos.requests.workout.SetEntryUDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.service.WorkoutLoggerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/set-entry")
public class SetEntryController extends Controller {
  private final WorkoutLoggerService workoutLoggerService;

  public SetEntryController(WorkoutLoggerService workoutLoggerService) {
    this.workoutLoggerService = workoutLoggerService;
  }

  @PostMapping("/create")
  public ResponseEntity<ResponseDto> createSetEntry(
      @Valid @RequestBody SetEntryDto dto, BindingResult result) {
    if (result.hasErrors()) {
      return validateResult(result);
    }
    return ResponseEntity.ok(workoutLoggerService.createSetEntry(dto));
  }

  @GetMapping("/get/all")
  public ResponseEntity<ResponseDto> getAllSetEntries() {
    return ResponseEntity.ok(workoutLoggerService.getAllSetEntries());
  }

  @GetMapping("/get/fromExercise/{id}")
  public ResponseEntity<ResponseDto> getAllSetEntriesFromExercise(
      @PathVariable("id") Integer workoutExerciseId) {
    return ResponseEntity.ok(workoutLoggerService.getAllSetEntriesFromExercise(workoutExerciseId));
  }

  @GetMapping("/get/{id}")
  public ResponseEntity<ResponseDto> getSetEntry(@PathVariable("id") Integer setEntryId) {
    return ResponseEntity.ok(workoutLoggerService.getSetEntryById(setEntryId));
  }

  @PostMapping("/update")
  public ResponseEntity<ResponseDto> updateSetEntry(
      @Valid @RequestBody SetEntryUDto dto, BindingResult result) {
    if (result.hasErrors()) {
      return validateResult(result);
    }
    return ResponseEntity.ok(workoutLoggerService.updateSetEntry(dto));
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ResponseDto> deleteSetEntry(@PathVariable("id") Integer setEntryId) {
    return ResponseEntity.ok(workoutLoggerService.deleteSetEntry(setEntryId));
  }
}
