package com.fittrackpro.app.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "workout_programs")
public class WorkoutProgramEntity {
    @PrimaryKey
    @NonNull
    private String programId;
    private String userId;
    private String programName;
    private String description;
    private String difficulty;
    private int durationWeeks;
    private int daysPerWeek;
    private boolean isPreset;
    private boolean isActive;
    private String originalPresetId;
    private long createdAt;
    private long updatedAt;
    private boolean synced = false;
    private long lastSyncAttempt = 0;
    private int syncAttempts = 0;
    private String syncError = null;

    // Getters and setters
    @NonNull
    public String getProgramId() { return programId; }
    public void setProgramId(@NonNull String programId) { this.programId = programId; }

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

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }

    public long getLastSyncAttempt() { return lastSyncAttempt; }
    public void setLastSyncAttempt(long lastSyncAttempt) { this.lastSyncAttempt = lastSyncAttempt; }

    public int getSyncAttempts() { return syncAttempts; }
    public void setSyncAttempts(int syncAttempts) { this.syncAttempts = syncAttempts; }

    public String getSyncError() { return syncError; }
    public void setSyncError(String syncError) { this.syncError = syncError; }
}