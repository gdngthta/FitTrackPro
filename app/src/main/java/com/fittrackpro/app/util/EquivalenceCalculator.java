package com.fittrackpro. app.util;

/**
 * EquivalenceCalculator converts workout volume to real-world equivalences.
 * This is optional and toggleable in settings.
 *
 * Examples:
 * - Volume lifted = weight of X elephants
 * - Volume lifted = weight of X cars
 */
public class EquivalenceCalculator {

    // Reference weights in kg
    private static final double ELEPHANT_KG = 5000.0;
    private static final double CAR_KG = 1500.0;
    private static final double PIANO_KG = 350.0;
    private static final double BEAR_KG = 200.0;
    private static final double PERSON_KG = 70.0;

    /**
     * Get human-readable equivalence for volume
     * Returns the most appropriate equivalence based on volume magnitude
     */
    public static String getEquivalence(double volumeKg) {
        if (volumeKg >= ELEPHANT_KG) {
            double count = volumeKg / ELEPHANT_KG;
            return String.format("%.1f elephant(s)", count);
        } else if (volumeKg >= CAR_KG) {
            double count = volumeKg / CAR_KG;
            return String.format("%.1f car(s)", count);
        } else if (volumeKg >= PIANO_KG) {
            double count = volumeKg / PIANO_KG;
            return String.format("%. 1f piano(s)", count);
        } else if (volumeKg >= BEAR_KG) {
            double count = volumeKg / BEAR_KG;
            return String.format("%.1f bear(s)", count);
        } else if (volumeKg >= PERSON_KG) {
            double count = volumeKg / PERSON_KG;
            return String.format("%.1f person(s)", count);
        } else {
            return String.format("%.1f kg", volumeKg);
        }
    }
}