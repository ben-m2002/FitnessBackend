package com.example.fitnessbackend.mappers;


import com.example.fitnessbackend.dtos.requests.workout.SetEntryDto;
import com.example.fitnessbackend.dtos.requests.workout.SetEntryUDto;
import com.example.fitnessbackend.dtos.responses.workout.AllSEResponseDto;
import com.example.fitnessbackend.dtos.responses.workout.SetEntryResponseDto;
import com.example.fitnessbackend.models.SetEntry;
import com.example.fitnessbackend.models.WorkoutExercise;
import com.example.fitnessbackend.repositories.SetEntryRepository;
import com.example.fitnessbackend.repositories.WorkoutExerciseRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SetMapper {
   private final WorkoutExerciseRepository workoutExerciseRepository;
   private final SetEntryRepository entryRepository;

    public SetMapper(WorkoutExerciseRepository workoutExerciseRepository, SetEntryRepository entryRepository) {
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.entryRepository = entryRepository;
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

    public AllSEResponseDto toAllSEResponseDto(List<SetEntry> setEntries, String message){
        List<SetEntryResponseDto> setEntryResponseDtos = setEntries.stream()
                .map(entry -> new SetEntryResponseDto(entry.getId(),
                        entry.getWorkoutExercise().getId(), entry.getWeight(),
                        entry.getNumSets(), entry.getReps(), entry.getDifficulty()))
                .toList();
        return new AllSEResponseDto(message, setEntryResponseDtos);

    }

    public SetEntry fromSetEntryUDto(SetEntryUDto dto){
        SetEntry oldEntry = entryRepository.getById(dto.getId());
        oldEntry.setWeight(dto.getWeight());
        oldEntry.setNumSets(dto.getNumSets());
        oldEntry.setReps(dto.getReps());
        oldEntry.setDifficulty(dto.getDifficulty());
        return oldEntry;
    }
}
