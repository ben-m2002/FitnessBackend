package com.example.fitnessbackend.controllers;


import com.example.fitnessbackend.dtos.requests.workout.SetEntryDto;
import com.example.fitnessbackend.dtos.responses.workout.SetEntryResponseDto;
import com.example.fitnessbackend.models.SetEntry;
import com.example.fitnessbackend.service.WorkoutService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/set-entry")
public class SetEntryController {
    private final WorkoutService workoutService;

    public SetEntryController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping("/create")
    public ResponseEntity<SetEntryResponseDto> createSetEntry(
            @RequestBody SetEntryDto dto) {
        return ResponseEntity.ok(workoutService.createSetEntry(dto));
    }

}
