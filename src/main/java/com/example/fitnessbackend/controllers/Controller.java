package com.example.fitnessbackend.controllers;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

public abstract class Controller {

    protected ResponseEntity<ResponseDto> validateResult(BindingResult result) {
        List<String> errors = result.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(
                new ResponseDto("Invalid input: " + String.join(", ", errors) + " " +
                        Map.of("errors", errors))
        );
    }
}
