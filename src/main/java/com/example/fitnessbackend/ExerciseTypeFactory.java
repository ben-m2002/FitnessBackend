package com.example.fitnessbackend;

import com.example.fitnessbackend.nonPersistData.ExerciseName;
import com.example.fitnessbackend.nonPersistData.ExerciseType;

public class ExerciseTypeFactory {

    public static ExerciseType createExercise(ExerciseName name){
        switch (name) {
            case BENCH_PRESS:
                return new ExerciseType(
                        "Bench Press",
                        "A compound exercise that targets the chest, shoulders, and triceps."
                );
            case TRICEP_EXTENSION:
                return new ExerciseType(
                        "Tricep Extension",
                        "An isolation exercise that targets the triceps."
                );
            case SQUAT:
                return new ExerciseType(
                        "Squat",
                        "A compound exercise that targets the quadriceps, hamstrings, and glutes."
                );
            case DEADLIFT:
                return new ExerciseType(
                        "Deadlift",
                        "A compound exercise that targets the back, glutes, and hamstrings."
                );
            case LEG_PRESS:
                return new ExerciseType(
                        "Leg Press",
                        "A compound exercise that targets the quadriceps, hamstrings, and glutes."
                );
            default:
                throw new IllegalArgumentException("Unknown exercise name: " + name);
        }
    }
}
