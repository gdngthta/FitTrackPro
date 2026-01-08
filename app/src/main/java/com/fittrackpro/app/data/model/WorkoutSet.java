package com.fittrackpro.app.data.model;

import java.io.Serializable;

public class WorkoutSet implements Serializable {
    private String setId;
    private String workoutId;
    private String exerciseName;
    private int setNumber;
    private double weight;
    private int reps;
    private String status; // "completed", "modified", "substituted", "skipped"
    private String substitutedExercise; // if substituted
    private String notes;

    public WorkoutSet() {
        // Required empty constructor
    }

    // Getters and setters
    public String getSetId() { return setId; }
    public void setSetId(String setId) { this.setId = setId; }

    public String getWorkoutId() { return workoutId; }
    public void setWorkoutId(String workoutId) { this.workoutId = workoutId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public int getSetNumber() { return setNumber; }
    public void setSetNumber(int setNumber) { this.setNumber = setNumber; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubstitutedExercise() { return substitutedExercise; }
    public void setSubstitutedExercise(String substitutedExercise) { this.substitutedExercise = substitutedExercise; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}