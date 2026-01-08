package com.fittrackpro.app.data. model;

import com.google. firebase.Timestamp;

public class WorkoutProgram {
    private String programId;
    private String userId; // null for preset programs
    private String programName;
    private String description;
    private String difficulty; // "beginner", "intermediate", "advanced"
    private int durationWeeks;
    private int daysPerWeek;
    private boolean isPreset; // true for read-only presets
    private boolean isActive; // user can have multiple programs
    private String originalPresetId; // if duplicated from preset
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public WorkoutProgram() {
        // Required empty constructor
    }

    // Getters and setters
    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getDurationWeeks() { return durationWeeks; }
    public void setDurationWeeks(int durationWeeks) { this.durationWeeks = durationWeeks; }

    public int getDaysPerWeek() { return daysPerWeek; }
    public void setDaysPerWeek(int daysPerWeek) { this.daysPerWeek = daysPerWeek; }

    public boolean isPreset() { return isPreset; }
    public void setPreset(boolean preset) { isPreset = preset; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getOriginalPresetId() { return originalPresetId; }
    public void setOriginalPresetId(String originalPresetId) { this.originalPresetId = originalPresetId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}