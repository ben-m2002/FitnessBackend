package com.example.fitnessbackend.utilities;

public class UnitConverter {
    private static final double KG_TO_LB = 2.20462;

    public static double convertKgToLb(double kg) {
        return kg * KG_TO_LB;
    }

    public static double convertLbToKg(double lb) {
        return lb / KG_TO_LB;
    }
}
