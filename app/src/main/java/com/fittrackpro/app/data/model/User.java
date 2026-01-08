package com.fittrackpro.app.data.model;

import com.google.firebase. Timestamp;

public class User {
    private String userId;
    private String email;
    private String username; // globally unique
    private String displayName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Stats (calculated, but cached)
    private int totalWorkouts;
    private int currentStreak;
    private double totalVolumeLifted; // in kg or lb
    private int activePrograms;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String userId, String email, String username, String displayName) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.displayName = displayName;
        this. createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
        this.totalWorkouts = 0;
        this.currentStreak = 0;
        this. totalVolumeLifted = 0.0;
        this. activePrograms = 0;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public int getTotalWorkouts() { return totalWorkouts; }
    public void setTotalWorkouts(int totalWorkouts) { this.totalWorkouts = totalWorkouts; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public double getTotalVolumeLifted() { return totalVolumeLifted; }
    public void setTotalVolumeLifted(double totalVolumeLifted) { this.totalVolumeLifted = totalVolumeLifted; }

    public int getActivePrograms() { return activePrograms; }
    public void setActivePrograms(int activePrograms) { this.activePrograms = activePrograms; }
}