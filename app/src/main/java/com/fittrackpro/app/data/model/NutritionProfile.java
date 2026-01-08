package com.fittrackpro. app.data.model;

import com.google.firebase.Timestamp;

public class NutritionProfile {
    private String profileId;
    private String userId;

    // User inputs
    private double weight; // in kg or lb based on user preference
    private double height; // in cm
    private int age;
    private String gender; // "male" or "female"
    private String activityLevel; // "sedentary", "light", "moderate", "active", "very_active"
    private String goal; // "lose", "maintain", "gain"

    // Calculated values (Mifflin-St Jeor)
    private double bmr; // Basal Metabolic Rate
    private double tdee; // Total Daily Energy Expenditure
    private double targetCalories;
    private double targetProtein; // in grams
    private double targetCarbs; // in grams
    private double targetFats; // in grams

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public NutritionProfile() {
        // Required empty constructor
    }

    // Getters and setters
    public String getProfileId() { return profileId; }
    public void setProfileId(String profileId) { this.profileId = profileId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public double getBmr() { return bmr; }
    public void setBmr(double bmr) { this.bmr = bmr; }

    public double getTdee() { return tdee; }
    public void setTdee(double tdee) { this.tdee = tdee; }

    public double getTargetCalories() { return targetCalories; }
    public void setTargetCalories(double targetCalories) { this.targetCalories = targetCalories; }

    public double getTargetProtein() { return targetProtein; }
    public void setTargetProtein(double targetProtein) { this.targetProtein = targetProtein; }

    public double getTargetCarbs() { return targetCarbs; }
    public void setTargetCarbs(double targetCarbs) { this.targetCarbs = targetCarbs; }

    public double getTargetFats() { return targetFats; }
    public void setTargetFats(double targetFats) { this.targetFats = targetFats; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}