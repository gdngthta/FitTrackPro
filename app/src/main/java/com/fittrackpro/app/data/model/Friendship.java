package com.fittrackpro.app.data.model;

import com.google.firebase.Timestamp;

public class Friendship {
    private String friendshipId;
    private String userId;
    private String friendUserId;
    private String friendUsername;
    private String friendDisplayName;
    private Timestamp createdAt;

    public Friendship() {
        // Required empty constructor
    }

    // Getters and setters
    public String getFriendshipId() { return friendshipId; }
    public void setFriendshipId(String friendshipId) { this.friendshipId = friendshipId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFriendUserId() { return friendUserId; }
    public void setFriendUserId(String friendUserId) { this.friendUserId = friendUserId; }

    public String getFriendUsername() { return friendUsername; }
    public void setFriendUsername(String friendUsername) { this.friendUsername = friendUsername; }

    public String getFriendDisplayName() { return friendDisplayName; }
    public void setFriendDisplayName(String friendDisplayName) { this.friendDisplayName = friendDisplayName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}