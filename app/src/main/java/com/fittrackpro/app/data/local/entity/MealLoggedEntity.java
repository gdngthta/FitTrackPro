package com.fittrackpro.app.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "meals_logged")
public class MealLoggedEntity {
    @PrimaryKey
    @NonNull
    private String logId;
    private String userId;
    private String foodId;
    private String foodName;
    private String mealType;
    private double portionMultiplier;
    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    private long loggedAt;
    private boolean synced = false;
    private long lastSyncAttempt = 0;
    private int syncAttempts = 0;
    private String syncError = null;

    // Getters and setters
    @NonNull
    public String getLogId() { return logId; }
    public void setLogId(@NonNull String logId) { this.logId = logId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public double getPortionMultiplier() { return portionMultiplier; }
    public void setPortionMultiplier(double portionMultiplier) { this.portionMultiplier = portionMultiplier; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }

    public double getFats() { return fats; }
    public void setFats(double fats) { this.fats = fats; }

    public long getLoggedAt() { return loggedAt; }
    public void setLoggedAt(long loggedAt) { this.loggedAt = loggedAt; }

    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }

    public long getLastSyncAttempt() { return lastSyncAttempt; }
    public void setLastSyncAttempt(long lastSyncAttempt) { this.lastSyncAttempt = lastSyncAttempt; }

    public int getSyncAttempts() { return syncAttempts; }
    public void setSyncAttempts(int syncAttempts) { this.syncAttempts = syncAttempts; }

    public String getSyncError() { return syncError; }
    public void setSyncError(String syncError) { this.syncError = syncError; }
}
