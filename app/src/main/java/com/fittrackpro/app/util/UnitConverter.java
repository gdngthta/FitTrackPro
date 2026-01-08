package com.fittrackpro.app.util;

/**
 * UnitConverter handles conversion between kg and lb.
 */
public class UnitConverter {

    private static final double KG_TO_LB = 2.20462;
    private static final double LB_TO_KG = 0.453592;

    public static double kgToLb(double kg) {
        return kg * KG_TO_LB;
    }

    public static double lbToKg(double lb) {
        return lb * LB_TO_KG;
    }

    public static String formatWeight(double weight, boolean isKg) {
        if (isKg) {
            return String.format("%.1f kg", weight);
        } else {
            return String.format("%.1f lb", weight);
        }
    }

    public static double convertWeight(double weight, boolean fromKg, boolean toKg) {
        if (fromKg == toKg) {
            return weight;
        }

        if (fromKg) {
            return kgToLb(weight);
        } else {
            return lbToKg(weight);
        }
    }
}