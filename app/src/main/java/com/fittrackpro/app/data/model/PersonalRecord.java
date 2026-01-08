package com.fittrackpro.app.data.model;

import com.google.firebase.Timestamp;

public class PersonalRecord {
    private String recordId;
    private String userId;
    private String exerciseName;
    private String recordType; // "weight", "reps", "volume"
    private double value; // weight or volume
    private int reps; // for rep PRs
    private Timestamp achievedAt;

    public PersonalRecord() {
        // Required empty constructor
    }

    // Getters and setters
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

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

    public Timestamp getAchievedAt() { return achievedAt; }
    public void setAchievedAt(Timestamp achievedAt) { this.achievedAt = achievedAt; }
}