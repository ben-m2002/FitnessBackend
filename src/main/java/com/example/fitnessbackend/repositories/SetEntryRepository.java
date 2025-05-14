package com.example.fitnessbackend.repositories;

import com.example.fitnessbackend.models.SetEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetEntryRepository extends JpaRepository<SetEntry, Integer> {

}
