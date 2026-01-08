package com.fittrackpro.app.data.model;

public class LeaderboardEntry {
    private String userId;
    private String username;
    private String displayName;
    private double totalVolume;
    private int rank;
    private boolean isCurrentUser;

    public LeaderboardEntry() {
        // Required empty constructor
    }

    public LeaderboardEntry(String userId, String username, String displayName, double totalVolume) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.totalVolume = totalVolume;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public double getTotalVolume() { return totalVolume; }
    public void setTotalVolume(double totalVolume) { this.totalVolume = totalVolume; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public boolean isCurrentUser() { return isCurrentUser; }
    public void setCurrentUser(boolean currentUser) { isCurrentUser = currentUser; }
}