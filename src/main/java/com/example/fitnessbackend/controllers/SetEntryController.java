package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.requests.workout.SetEntryDto;
import com.example.fitnessbackend.dtos.requests.workout.SetEntryUDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.AllSEResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.SetEntryResponseDto;
import com.example.fitnessbackend.models.SetEntry;
import com.example.fitnessbackend.service.WorkoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/set-entry")
public class SetEntryController extends Controller {
  private final WorkoutService workoutService;

  public SetEntryController(WorkoutService workoutService) {
    this.workoutService = workoutService;
  }

  @PostMapping("/create")
  public ResponseEntity<ResponseDto> createSetEntry(
      @Valid @RequestBody SetEntryDto dto, BindingResult result) {
    if (result.hasErrors()) {
      return validateResult(result);
    }
    return ResponseEntity.ok(workoutService.createSetEntry(dto));
  }

  @GetMapping("/get/all")
  public ResponseEntity<ResponseDto> getAllSetEntries() {
    return ResponseEntity.ok(workoutService.getAllSetEntries());
  }

  @GetMapping("/get/fromExercise/{id}")
  public ResponseEntity<ResponseDto> getAllSetEntriesFromExercise(
      @PathVariable("id") Integer workoutExerciseId) {
    return ResponseEntity.ok(workoutService.getAllSetEntriesFromExercise(workoutExerciseId));
  }

  @GetMapping("/get/{id}")
  public ResponseEntity<ResponseDto> getSetEntry(@PathVariable("id") Integer setEntryId) {
    return ResponseEntity.ok(workoutService.getSetEntryById(setEntryId));
  }

  @PostMapping("/update")
  public ResponseEntity<ResponseDto> updateSetEntry(
      @Valid @RequestBody SetEntryUDto dto, BindingResult result) {
    if (result.hasErrors()) {
      return validateResult(result);
    }
    return ResponseEntity.ok(workoutService.updateSetEntry(dto));
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ResponseDto> deleteSetEntry(@PathVariable("id") Integer setEntryId) {
    return ResponseEntity.ok(workoutService.deleteSetEntry(setEntryId));
  }
}
