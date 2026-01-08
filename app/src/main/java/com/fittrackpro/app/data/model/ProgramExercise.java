package com.fittrackpro.app.data.model;

public class ProgramExercise {
    private String exerciseId;
    private String dayId;
    private int orderIndex;
    private String exerciseName;
    private String muscleGroup; // "chest", "back", "legs", "shoulders", "arms", "core"
    private String equipment; // "barbell", "dumbbell", "cable", "bodyweight", "machine"
    private int targetSets;
    private int targetRepsMin;
    private int targetRepsMax;
    private int restSeconds; // rest time between sets in seconds
    private String notes; // form cues, etc.

    public ProgramExercise() {
        // Required empty constructor
    }

    // Getters and setters
    public String getExerciseId() { return exerciseId; }
    public void setExerciseId(String exerciseId) { this.exerciseId = exerciseId; }

    public String getDayId() { return dayId; }
    public void setDayId(String dayId) { this.dayId = dayId; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public int getTargetSets() { return targetSets; }
    public void setTargetSets(int targetSets) { this.targetSets = targetSets; }

    public int getTargetRepsMin() { return targetRepsMin; }
    public void setTargetRepsMin(int targetRepsMin) { this.targetRepsMin = targetRepsMin; }

    public int getTargetRepsMax() { return targetRepsMax; }
    public void setTargetRepsMax(int targetRepsMax) { this.targetRepsMax = targetRepsMax; }

    public int getRestSeconds() { return restSeconds; }
    public void setRestSeconds(int restSeconds) { this.restSeconds = restSeconds; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}