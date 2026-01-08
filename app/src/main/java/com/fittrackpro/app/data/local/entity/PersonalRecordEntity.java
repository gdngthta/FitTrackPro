package com.fittrackpro. app.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation. NonNull;

@Entity(tableName = "personal_records")
public class PersonalRecordEntity {
    @PrimaryKey
    @NonNull
    private String recordId;
    private String userId;
    private String exerciseName;
    private String recordType;
    private double value;
    private int reps;
    private long achievedAt;
    private boolean synced = false;
    private long lastSyncAttempt = 0;
    private int syncAttempts = 0;
    private String syncError = null;

    // Getters and setters
    @NonNull
    public String getRecordId() { return recordId; }
    public void setRecordId(@NonNull String recordId) { this.recordId = recordId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public long getAchievedAt() { return achievedAt; }
    public void setAchievedAt(long achievedAt) { this.achievedAt = achievedAt; }

    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }

    public long getLastSyncAttempt() { return lastSyncAttempt; }
    public void setLastSyncAttempt(long lastSyncAttempt) { this.lastSyncAttempt = lastSyncAttempt; }

    public int getSyncAttempts() { return syncAttempts; }
    public void setSyncAttempts(int syncAttempts) { this.syncAttempts = syncAttempts; }

    public String getSyncError() { return syncError; }
    public void setSyncError(String syncError) { this.syncError = syncError; }
}