package com.fittrackpro.app.util;

import com.fittrackpro.app.data.model.ExerciseTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ExerciseLibrary provides a comprehensive list of exercises.
 */
public class ExerciseLibrary {

    public static List<ExerciseTemplate> getAllExercises() {
        List<ExerciseTemplate> exercises = new ArrayList<>();

        // Chest Exercises
        exercises.add(new ExerciseTemplate("Barbell Bench Press", "Chest", "Barbell"));
        exercises.add(new ExerciseTemplate("Incline Barbell Press", "Chest", "Barbell"));
        exercises.add(new ExerciseTemplate("Decline Barbell Press", "Chest", "Barbell"));
        exercises.add(new ExerciseTemplate("Dumbbell Bench Press", "Chest", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Incline Dumbbell Press", "Chest", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Decline Dumbbell Press", "Chest", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Dumbbell Flyes", "Chest", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Incline Dumbbell Flyes", "Chest", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Cable Flyes", "Chest", "Cable"));
        exercises.add(new ExerciseTemplate("Cable Crossover", "Chest", "Cable"));
        exercises.add(new ExerciseTemplate("Chest Press Machine", "Chest", "Machine"));
        exercises.add(new ExerciseTemplate("Pec Deck Machine", "Chest", "Machine"));
        exercises.add(new ExerciseTemplate("Push-ups", "Chest", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Decline Push-ups", "Chest", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Diamond Push-ups", "Chest", "Bodyweight"));

        // Back Exercises
        exercises.add(new ExerciseTemplate("Deadlift", "Back", "Barbell"));
        exercises.add(new ExerciseTemplate("Barbell Row", "Back", "Barbell"));
        exercises.add(new ExerciseTemplate("T-Bar Row", "Back", "Barbell"));
        exercises.add(new ExerciseTemplate("Pendlay Row", "Back", "Barbell"));
        exercises.add(new ExerciseTemplate("Dumbbell Row", "Back", "Dumbbell"));
        exercises.add(new ExerciseTemplate("One-Arm Dumbbell Row", "Back", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Cable Row", "Back", "Cable"));
        exercises.add(new ExerciseTemplate("Lat Pulldown", "Back", "Cable"));
        exercises.add(new ExerciseTemplate("Wide Grip Lat Pulldown", "Back", "Cable"));
        exercises.add(new ExerciseTemplate("Close Grip Lat Pulldown", "Back", "Cable"));
        exercises.add(new ExerciseTemplate("Face Pulls", "Back", "Cable"));
        exercises.add(new ExerciseTemplate("Seated Cable Row", "Back", "Cable"));
        exercises.add(new ExerciseTemplate("Straight Arm Pulldown", "Back", "Cable"));
        exercises.add(new ExerciseTemplate("Pull-ups", "Back", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Chin-ups", "Back", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Inverted Row", "Back", "Bodyweight"));

        // Shoulders Exercises
        exercises.add(new ExerciseTemplate("Overhead Press", "Shoulders", "Barbell"));
        exercises.add(new ExerciseTemplate("Push Press", "Shoulders", "Barbell"));
        exercises.add(new ExerciseTemplate("Behind the Neck Press", "Shoulders", "Barbell"));
        exercises.add(new ExerciseTemplate("Dumbbell Shoulder Press", "Shoulders", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Arnold Press", "Shoulders", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Lateral Raises", "Shoulders", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Front Raises", "Shoulders", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Rear Delt Flyes", "Shoulders", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Upright Row", "Shoulders", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Cable Lateral Raises", "Shoulders", "Cable"));
        exercises.add(new ExerciseTemplate("Cable Front Raises", "Shoulders", "Cable"));
        exercises.add(new ExerciseTemplate("Shoulder Press Machine", "Shoulders", "Machine"));
        exercises.add(new ExerciseTemplate("Pike Push-ups", "Shoulders", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Handstand Push-ups", "Shoulders", "Bodyweight"));

        // Legs Exercises
        exercises.add(new ExerciseTemplate("Barbell Squat", "Legs", "Barbell"));
        exercises.add(new ExerciseTemplate("Front Squat", "Legs", "Barbell"));
        exercises.add(new ExerciseTemplate("Bulgarian Split Squat", "Legs", "Barbell"));
        exercises.add(new ExerciseTemplate("Romanian Deadlift", "Legs", "Barbell"));
        exercises.add(new ExerciseTemplate("Leg Press", "Legs", "Machine"));
        exercises.add(new ExerciseTemplate("Hack Squat", "Legs", "Machine"));
        exercises.add(new ExerciseTemplate("Leg Extension", "Legs", "Machine"));
        exercises.add(new ExerciseTemplate("Leg Curl", "Legs", "Machine"));
        exercises.add(new ExerciseTemplate("Seated Leg Curl", "Legs", "Machine"));
        exercises.add(new ExerciseTemplate("Calf Raise Machine", "Legs", "Machine"));
        exercises.add(new ExerciseTemplate("Dumbbell Lunges", "Legs", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Dumbbell Step-ups", "Legs", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Goblet Squat", "Legs", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Dumbbell RDL", "Legs", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Walking Lunges", "Legs", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Pistol Squats", "Legs", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Jump Squats", "Legs", "Bodyweight"));

        // Arms Exercises
        exercises.add(new ExerciseTemplate("Barbell Curl", "Arms", "Barbell"));
        exercises.add(new ExerciseTemplate("EZ Bar Curl", "Arms", "Barbell"));
        exercises.add(new ExerciseTemplate("Close Grip Bench Press", "Arms", "Barbell"));
        exercises.add(new ExerciseTemplate("Skull Crushers", "Arms", "Barbell"));
        exercises.add(new ExerciseTemplate("Dumbbell Curl", "Arms", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Hammer Curl", "Arms", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Concentration Curl", "Arms", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Preacher Curl", "Arms", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Tricep Dumbbell Extension", "Arms", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Dumbbell Kickback", "Arms", "Dumbbell"));
        exercises.add(new ExerciseTemplate("Cable Curl", "Arms", "Cable"));
        exercises.add(new ExerciseTemplate("Cable Tricep Pushdown", "Arms", "Cable"));
        exercises.add(new ExerciseTemplate("Cable Overhead Extension", "Arms", "Cable"));
        exercises.add(new ExerciseTemplate("Cable Rope Curl", "Arms", "Cable"));
        exercises.add(new ExerciseTemplate("Dips", "Arms", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Bench Dips", "Arms", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Close Grip Push-ups", "Arms", "Bodyweight"));

        // Core Exercises
        exercises.add(new ExerciseTemplate("Plank", "Core", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Side Plank", "Core", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Russian Twists", "Core", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Bicycle Crunches", "Core", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Leg Raises", "Core", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Hanging Leg Raises", "Core", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Mountain Climbers", "Core", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Ab Wheel Rollout", "Core", "Bodyweight"));
        exercises.add(new ExerciseTemplate("Cable Crunches", "Core", "Cable"));
        exercises.add(new ExerciseTemplate("Cable Woodchoppers", "Core", "Cable"));
        exercises.add(new ExerciseTemplate("Dumbbell Side Bends", "Core", "Dumbbell"));

        return exercises;
    }

    public static List<String> getMuscleGroups() {
        return Arrays.asList("All", "Chest", "Back", "Shoulders", "Legs", "Arms", "Core");
    }

    public static List<String> getEquipmentTypes() {
        return Arrays.asList("All", "Barbell", "Dumbbell", "Cable", "Machine", "Bodyweight");
    }

    public static List<ExerciseTemplate> filterExercises(List<ExerciseTemplate> exercises, 
                                                        String muscleGroup, 
                                                        String equipment,
                                                        String searchQuery) {
        List<ExerciseTemplate> filtered = new ArrayList<>();
        
        for (ExerciseTemplate exercise : exercises) {
            boolean matchesMuscle = muscleGroup.equals("All") || 
                                  exercise.getMuscleGroup().equalsIgnoreCase(muscleGroup);
            boolean matchesEquipment = equipment.equals("All") || 
                                      exercise.getEquipment().equalsIgnoreCase(equipment);
            boolean matchesSearch = searchQuery == null || searchQuery.isEmpty() ||
                                   exercise.getName().toLowerCase().contains(searchQuery.toLowerCase());
            
            if (matchesMuscle && matchesEquipment && matchesSearch) {
                filtered.add(exercise);
            }
        }
        
        return filtered;
    }
}
