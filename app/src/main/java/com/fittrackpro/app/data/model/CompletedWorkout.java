package com.fittrackpro. app.data.model;

import com.google.firebase.Timestamp;

public class CompletedWorkout {
    private String workoutId;
    private String userId;
    private String programId; // nullable if quick workout
    private String dayId; // nullable
    private String workoutName;
    private Timestamp startTime;
    private Timestamp endTime;
    private long durationSeconds;
    private double totalVolume; // sum of (weight * reps) for all sets
    private int totalSets;
    private int totalExercises;
    private boolean synced; // for offline support

    public CompletedWorkout() {
        // Required empty constructor
    }

    // Getters and setters
    public String getWorkoutId() { return workoutId; }
    public void setWorkoutId(String workoutId) { this.workoutId = workoutId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }

    public String getDayId() { return dayId; }
    public void setDayId(String dayId) { this.dayId = dayId; }

    public String getWorkoutName() { return workoutName; }
    public void setWorkoutName(String workoutName) { this.workoutName = workoutName; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }

    public long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(long durationSeconds) { this.durationSeconds = durationSeconds; }

    public double getTotalVolume() { return totalVolume; }
    public void setTotalVolume(double totalVolume) { this.totalVolume = totalVolume; }

    public int getTotalSets() { return totalSets; }
    public void setTotalSets(int totalSets) { this.totalSets = totalSets; }

    public int getTotalExercises() { return totalExercises; }
    public void setTotalExercises(int totalExercises) { this.totalExercises = totalExercises; }

    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }
}