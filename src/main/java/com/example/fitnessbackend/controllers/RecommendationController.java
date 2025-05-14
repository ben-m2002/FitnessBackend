package com.example.fitnessbackend.controllers;
import com.example.fitnessbackend.dtos.requests.reccomendation.SetEntryRecommendDto;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.SetEntryResponseDto;
import com.example.fitnessbackend.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController extends Controller {
    private final RecommendationService recommendationService;
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/set-entry")
    public ResponseEntity<ResponseDto> recommendSetEntry(
            @Valid @RequestBody SetEntryRecommendDto dto,
            BindingResult result) {
        if (result.hasErrors()) {
            return this.validateResult(result);
        }
        System.out.println(dto);
        return ResponseEntity.ok().body(recommendationService.recommendSetEntry(dto));
    }
}
