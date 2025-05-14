package com.example.fitnessbackend.components;

import com.example.fitnessbackend.nonPersistData.WeightMeasurementType;
import com.example.fitnessbackend.utilities.UnitConverter;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import com.example.fitnessbackend.models.SetEntry;

@Component
public class RecommendationCalculator {

    /**
     * Computes the estimated 1RM (E1RM) in pounds from a SetEntry.
     */
    private double computeE1RMInPounds(SetEntry entry) {
        double wLb;
        if (entry.getMeasurementType() == WeightMeasurementType.KILOGRAMS) {
            wLb = UnitConverter.convertKgToLb(entry.getWeight());
        } else {
            wLb = entry.getWeight();
        }
        double reps = entry.getReps();
        return wLb * (1 + reps / 30.0);
    }

    /**
     * Predicts a weighted 1RM in pounds based on history and target difficulty.
     */
    public Integer predictWeighted1RM(List<SetEntry> history, int targetDiff) {
        double sumWeightedE1RM = 0.0;
        double sumWeights = 0.0;

        if (history.isEmpty()) {
            return 0;
        }

        for (SetEntry entry : history) {
            double e1rm = computeE1RMInPounds(entry);
            int d_i = entry.getDifficulty();

            // 1) Intrinsic difficulty weight (higher diff → bigger multiplier)
            double w_diff = (double) d_i / 5.0; // maps 1→0.2, 5→1.0

            // 2) Closeness weight (closer to target → bigger influence)
            double distance = Math.abs(d_i - targetDiff);
            double w_closeness = 1.0 / (1.0 + distance);

            // Combine them
            double weight = w_diff * w_closeness;

            sumWeightedE1RM += e1rm * weight;
            sumWeights += weight;
        }

        return (int) (sumWeightedE1RM / sumWeights);
    }

    /**
     * Picks a "smart" weight in pounds based on historical SetEntry data,
     * nudged toward the requested difficulty and jittered ±10%.
     */
    public int pickWeightPounds(List<SetEntry> history, int requestedDifficulty) {
        Map<Integer, List<SetEntry>> byDiff =
                history.stream().collect(Collectors.groupingBy(SetEntry::getDifficulty));

        // 1) Exact-match average
        int avgLb = avgWeightOrNaN(byDiff.get(requestedDifficulty));
        if (avgLb != Integer.MIN_VALUE) {
            return jitterInt(avgLb, 0.10);
        }

        // 2) Nearest-neighbor fallback
        int nearest = findNearestDifficultyWithData(byDiff.keySet(), requestedDifficulty);
        avgLb = avgWeightOrNaN(byDiff.get(nearest));
        if (avgLb != Integer.MIN_VALUE) {
            int nudge = (requestedDifficulty - nearest) * 2;  // ±2 lbs per level
            return jitterInt(avgLb + nudge, 0.10);
        }

        // 3) Global default: half the max seen or 45 lbs
        int defaultWeight = history.stream()
                .mapToInt(this::toPounds)
                .max()
                .orElse(90) / 2;
        return jitterInt(defaultWeight, 0.10);
    }

    /** Picks the number of reps with light jitter. */
    public int pickNumReps(List<SetEntry> history, int reqDiff) {
        Map<Integer, List<SetEntry>> byDiff =
                history.stream().collect(Collectors.groupingBy(SetEntry::getDifficulty));

        // 1) Exact-match
        int reps = avgRepsOrNaN(byDiff.get(reqDiff));
        if (reps != Integer.MIN_VALUE) {
            return jitterInt(reps, 0.10);
        }

        // 2) Nearest-neighbor
        int nearest = findNearestDifficultyWithData(byDiff.keySet(), reqDiff);
        reps = avgRepsOrNaN(byDiff.get(nearest));
        reps = Math.round(reps + (reqDiff - nearest) * 1.0f);
        if (reps >= 3) {
            return jitterInt(reps, 0.10);
        }

        // 3) Global default
        return jitterInt(defaultRepsByDifficulty(reqDiff), 0.10);
    }

    /** Picks the number of sets with light jitter. */
    public int pickNumberOfSets(List<SetEntry> history, int reqDiff) {
        Map<Integer, List<SetEntry>> byDiff =
                history.stream().collect(Collectors.groupingBy(SetEntry::getDifficulty));

        // 1) Exact-match
        int sets = averageSetsOrDefault(byDiff.get(reqDiff));
        if (sets != Integer.MIN_VALUE) {
            return jitterInt(sets, 0.10);
        }

        // 2) Nearest-neighbor
        int nearest = findNearestDifficultyWithData(byDiff.keySet(), reqDiff);
        sets = averageSetsOrDefault(byDiff.get(nearest));
        sets = Math.round(sets + (reqDiff - nearest) * 0.5f);
        if (sets >= 2) {
            return jitterInt(sets, 0.10);
        }

        // 3) Global default
        int defaultSets = Math.max(2, Math.min(5, 1 + reqDiff));
        return jitterInt(defaultSets, 0.10);
    }

    // ----- Private helpers -----

    private int toPounds(SetEntry e) {
        return e.getMeasurementType() == WeightMeasurementType.KILOGRAMS
                ? (int) Math.round(UnitConverter.convertKgToLb(e.getWeight()))
                : e.getWeight();
    }

    private int avgWeightOrNaN(List<SetEntry> entries) {
        if (entries == null || entries.isEmpty()) return Integer.MIN_VALUE;
        double avg = entries.stream()
                .mapToDouble(this::toPounds)
                .average()
                .orElse(Double.NaN);
        return (int) Math.round(avg);
    }

    private int avgRepsOrNaN(List<SetEntry> entries) {
        if (entries == null || entries.isEmpty()) return Integer.MIN_VALUE;
        double avg = entries.stream()
                .mapToInt(SetEntry::getReps)
                .average()
                .orElse(Double.NaN);
        return (int) Math.round(avg);
    }

    private int averageSetsOrDefault(List<SetEntry> entries) {
        if (entries == null || entries.isEmpty()) return Integer.MIN_VALUE;
        double avg = entries.stream()
                .mapToInt(SetEntry::getNumSets)
                .average()
                .orElse(Double.NaN);
        return (int) Math.round(avg);
    }

    private int defaultRepsByDifficulty(int diff) {
        switch (diff) {
            case 1: return 18;
            case 2: return 15;
            case 3: return 10;
            case 4: return 7;
            case 5: return 4;
            default: return 10;
        }
    }

    private int findNearestDifficultyWithData(Set<Integer> diffs, int target) {
        return diffs.stream()
                .min(Comparator.comparingInt(d -> Math.abs(d - target)))
                .orElse(target);
    }

    private int jitterInt(int base, double pct) {
        double factor = ThreadLocalRandom.current()
                .nextDouble(1.0 - pct, 1.0 + pct);
        return Math.max(1, (int) Math.round(base * factor));
    }
}
