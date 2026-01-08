package com.fittrackpro.app.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey
    @NonNull
    private String userId;
    private String email;
    private String username;
    private String displayName;
    private long createdAt;
    private long updatedAt;
    private int totalWorkouts;
    private int currentStreak;
    private double totalVolumeLifted;
    private int activePrograms;
    private boolean synced = false;
    private long lastSyncAttempt = 0;
    private int syncAttempts = 0;
    private String syncError = null;

    // Getters and setters
    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public int getTotalWorkouts() { return totalWorkouts; }
    public void setTotalWorkouts(int totalWorkouts) { this.totalWorkouts = totalWorkouts; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public double getTotalVolumeLifted() { return totalVolumeLifted; }
    public void setTotalVolumeLifted(double totalVolumeLifted) { this.totalVolumeLifted = totalVolumeLifted; }

    public int getActivePrograms() { return activePrograms; }
    public void setActivePrograms(int activePrograms) { this.activePrograms = activePrograms; }

    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }

    public long getLastSyncAttempt() { return lastSyncAttempt; }
    public void setLastSyncAttempt(long lastSyncAttempt) { this.lastSyncAttempt = lastSyncAttempt; }

    public int getSyncAttempts() { return syncAttempts; }
    public void setSyncAttempts(int syncAttempts) { this.syncAttempts = syncAttempts; }

    public String getSyncError() { return syncError; }
    public void setSyncError(String syncError) { this.syncError = syncError; }
}