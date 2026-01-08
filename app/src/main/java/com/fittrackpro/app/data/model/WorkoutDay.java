package com.fittrackpro.app.data.model;

public class WorkoutDay {
    private String dayId;
    private String programId;
    private int dayNumber; // 1-7 or order in program
    private String dayName; // "Push Day", "Leg Day", etc.
    private boolean warmupEnabled;
    private boolean cooldownEnabled;

    public WorkoutDay() {
        // Required empty constructor
    }

    // Getters and setters
    public String getDayId() { return dayId; }
    public void setDayId(String dayId) { this.dayId = dayId; }

    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }

    public int getDayNumber() { return dayNumber; }
    public void setDayNumber(int dayNumber) { this.dayNumber = dayNumber; }

    public String getDayName() { return dayName; }
    public void setDayName(String dayName) { this.dayName = dayName; }

    public boolean isWarmupEnabled() { return warmupEnabled; }
    public void setWarmupEnabled(boolean warmupEnabled) { this.warmupEnabled = warmupEnabled; }

    public boolean isCooldownEnabled() { return cooldownEnabled; }
    public void setCooldownEnabled(boolean cooldownEnabled) { this.cooldownEnabled = cooldownEnabled; }
}