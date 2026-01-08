package com.fittrackpro.app.data.model;

import com.google.firebase.Timestamp;

public class MealLogged {
    private String logId;
    private String userId;
    private String foodId;
    private String foodName;
    private String mealType; // "breakfast", "lunch", "dinner", "snacks"
    private double portionMultiplier; // 1.0 = 1 serving, 1.5 = 1.5 servings
    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    private Timestamp loggedAt;

    public MealLogged() {
        // Required empty constructor
    }

    // Getters and setters
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

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

    public Timestamp getLoggedAt() { return loggedAt; }
    public void setLoggedAt(Timestamp loggedAt) { this.loggedAt = loggedAt; }
}