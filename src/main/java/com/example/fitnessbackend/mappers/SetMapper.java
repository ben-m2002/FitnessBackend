package com.example.fitnessbackend.mappers;


import com.example.fitnessbackend.dtos.requests.workout.SetEntryDto;
import com.example.fitnessbackend.dtos.responses.workout.SetEntryResponseDto;
import com.example.fitnessbackend.models.SetEntry;
import com.example.fitnessbackend.models.WorkoutExercise;
import com.example.fitnessbackend.repositories.WorkoutExerciseRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SetMapper {
   private final WorkoutExerciseRepository workoutExerciseRepository;

    public SetMapper(WorkoutExerciseRepository workoutExerciseRepository) {
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    public SetEntry toSetEntry(SetEntryDto dto){
        Optional<WorkoutExercise> exercise = this.workoutExerciseRepository.findById(dto.getWorkoutExerciseId());

        if(exercise.isEmpty()){
            throw new IllegalArgumentException("WorkoutExercise with id " + dto.getWorkoutExerciseId() + " not found");
        }

        return SetEntry.builder()
                .workoutExercise(exercise.get())
                .weight(dto.getWeight())
                .numSets(dto.getNumSets())
                .reps(dto.getReps())
                .difficulty(dto.getDifficulty())
                .build();
    }

    public SetEntryResponseDto toSetEntryResponseDto(SetEntry entry, String message){
        return new SetEntryResponseDto(message, entry.getId(),
                entry.getWorkoutExercise().getId(), entry.getWeight(),
                entry.getNumSets(), entry.getReps(), entry.getDifficulty());
    }
}
