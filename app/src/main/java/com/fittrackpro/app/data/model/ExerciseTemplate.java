package com.fittrackpro.app.data.model;

/**
 * ExerciseTemplate represents an exercise in the library with its basic info.
 */
public class ExerciseTemplate {
    private String name;
    private String muscleGroup; // "Chest", "Back", "Shoulders", "Legs", "Arms", "Core"
    private String equipment; // "Barbell", "Dumbbell", "Cable", "Machine", "Bodyweight"

    public ExerciseTemplate() {
        // Required empty constructor
    }

    public ExerciseTemplate(String name, String muscleGroup, String equipment) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
}
