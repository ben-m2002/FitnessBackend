package com.example.fitnessbackend.dtos.responses.workout;

import com.example.fitnessbackend.dtos.responses.ResponseDto;
import lombok.Data;

import java.util.List;


@Data
public class AllSEResponseDto extends ResponseDto {
    private List<SetEntryResponseDto> setEntries;

    public AllSEResponseDto(String message, List<SetEntryResponseDto> setEntries) {
        super(message);
        this.setEntries = setEntries;
    }
}
