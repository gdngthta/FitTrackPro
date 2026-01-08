package com.fittrackpro. app.util;

/**
 * NutritionCalculator handles TDEE and macro calculations using Mifflin-St Jeor equation.
 */
public class NutritionCalculator {

    /**
     * Calculate BMR using Mifflin-St Jeor equation
     *
     * Men: BMR = (10 × weight in kg) + (6.25 × height in cm) - (5 × age in years) + 5
     * Women: BMR = (10 × weight in kg) + (6.25 × height in cm) - (5 × age in years) - 161
     */
    public static double calculateBMR(double weightKg, double heightCm, int age, String gender) {
        double bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age);

        if (gender. equalsIgnoreCase("male")) {
            bmr += 5;
        } else {
            bmr -= 161;
        }

        return bmr;
    }

    /**
     * Calculate TDEE based on activity level
     *
     * Activity multipliers:
     * - sedentary: 1.2
     * - light: 1.375
     * - moderate: 1.55
     * - active: 1.725
     * - very_active: 1.9
     */
    public static double calculateTDEE(double bmr, String activityLevel) {
        double multiplier;

        switch (activityLevel. toLowerCase()) {
            case "sedentary":
                multiplier = 1.2;
                break;
            case "light":
                multiplier = 1.375;
                break;
            case "moderate":
                multiplier = 1.55;
                break;
            case "active":
                multiplier = 1.725;
                break;
            case "very_active":
                multiplier = 1.9;
                break;
            default:
                multiplier = 1.2;
        }

        return bmr * multiplier;
    }

    /**
     * Calculate target calories based on goal
     *
     * Goals:
     * - lose:  -500 cal (1 lb/week loss)
     * - maintain: TDEE
     * - gain: +500 cal (1 lb/week gain)
     */
    public static double calculateTargetCalories(double tdee, String goal) {
        switch (goal.toLowerCase()) {
            case "lose":
                return tdee - 500;
            case "maintain":
                return tdee;
            case "gain":
                return tdee + 500;
            default:
                return tdee;
        }
    }

    /**
     * Calculate macros based on target calories and goal
     *
     * Returns [protein, carbs, fats] in grams
     *
     * Macro ratios:
     * - Lose: 40% protein, 30% carbs, 30% fats
     * - Maintain: 30% protein, 40% carbs, 30% fats
     * - Gain: 25% protein, 50% carbs, 25% fats
     */
    public static double[] calculateMacros(double targetCalories, String goal) {
        double proteinPercent, carbsPercent, fatsPercent;

        switch (goal.toLowerCase()) {
            case "lose":
                proteinPercent = 0.40;
                carbsPercent = 0.30;
                fatsPercent = 0.30;
                break;
            case "maintain":
                proteinPercent = 0.30;
                carbsPercent = 0.40;
                fatsPercent = 0.30;
                break;
            case "gain":
                proteinPercent = 0.25;
                carbsPercent = 0.50;
                fatsPercent = 0.25;
                break;
            default:
                proteinPercent = 0.30;
                carbsPercent = 0.40;
                fatsPercent = 0.30;
        }

        // Calories per gram:  protein=4, carbs=4, fats=9
        double proteinGrams = (targetCalories * proteinPercent) / 4;
        double carbsGrams = (targetCalories * carbsPercent) / 4;
        double fatsGrams = (targetCalories * fatsPercent) / 9;

        return new double[]{proteinGrams, carbsGrams, fatsGrams};
    }
}