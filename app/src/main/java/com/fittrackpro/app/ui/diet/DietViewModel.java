package com.fittrackpro. app.ui.diet;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.fittrackpro.app.data. local.AppDatabase;
import com.fittrackpro.app.data.model.MealLogged;
import com.fittrackpro.app.data.model.NutritionProfile;
import com.fittrackpro.app.data. repository.NutritionRepository;

import java.util.List;

/**
 * DietViewModel manages nutrition tracking.
 *
 * Displays:
 * - Nutrition profile (TDEE, target macros)
 * - Today's meals
 * - Today's totals vs targets
 */
public class DietViewModel extends AndroidViewModel {

    private final NutritionRepository nutritionRepository;

    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MediatorLiveData<NutritionProfile> nutritionProfile = new MediatorLiveData<>();
    private final MediatorLiveData<List<MealLogged>> todayMeals = new MediatorLiveData<>();

    private final MutableLiveData<Double> todayCalories = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> todayProtein = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> todayCarbs = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> todayFats = new MutableLiveData<>(0.0);

    public DietViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        this.nutritionRepository = new NutritionRepository(database);
    }

    public void setUserId(String userId) {
        this.userId.setValue(userId);
        loadDietData(userId);
    }

    private void loadDietData(String userId) {
        // Load nutrition profile
        LiveData<NutritionProfile> profileSource = nutritionRepository.getNutritionProfile(userId);
        nutritionProfile.addSource(profileSource, nutritionProfile::setValue);

        // Load today's meals
        LiveData<List<MealLogged>> mealsSource = nutritionRepository.getTodayMeals(userId);
        todayMeals. addSource(mealsSource, meals -> {
            todayMeals.setValue(meals);
            calculateTodayTotals(meals);
        });
    }

    private void calculateTodayTotals(List<MealLogged> meals) {
        if (meals == null || meals.isEmpty()) {
            todayCalories.setValue(0.0);
            todayProtein.setValue(0.0);
            todayCarbs.setValue(0.0);
            todayFats. setValue(0.0);
            return;
        }

        double calories = 0.0;
        double protein = 0.0;
        double carbs = 0.0;
        double fats = 0.0;

        for (MealLogged meal : meals) {
            calories += meal.getCalories();
            protein += meal. getProtein();
            carbs += meal.getCarbs();
            fats += meal.getFats();
        }

        todayCalories.setValue(calories);
        todayProtein. setValue(protein);
        todayCarbs.setValue(carbs);
        todayFats.setValue(fats);
    }

    public LiveData<Boolean> createNutritionProfile(double weight, double height, int age,
                                                    String gender, String activityLevel, String goal) {
        String currentUserId = userId.getValue();
        if (currentUserId != null) {
            return nutritionRepository.saveNutritionProfile(
                    currentUserId, weight, height, age, gender, activityLevel, goal
            );
        }
        return new MutableLiveData<>(false);
    }

    public LiveData<Boolean> logMeal(String foodId, String foodName, String mealType,
                                     double portionMultiplier, double calories, double protein,
                                     double carbs, double fats) {
        String currentUserId = userId.getValue();
        if (currentUserId != null) {
            return nutritionRepository.logMeal(
                    currentUserId, foodId, foodName, mealType, portionMultiplier,
                    calories, protein, carbs, fats
            );
        }
        return new MutableLiveData<>(false);
    }

    // Getters
    public LiveData<NutritionProfile> getNutritionProfile() {
        return nutritionProfile;
    }

    public LiveData<List<MealLogged>> getTodayMeals() {
        return todayMeals;
    }

    public LiveData<Double> getTodayCalories() {
        return todayCalories;
    }

    public LiveData<Double> getTodayProtein() {
        return todayProtein;
    }

    public LiveData<Double> getTodayCarbs() {
        return todayCarbs;
    }

    public LiveData<Double> getTodayFats() {
        return todayFats;
    }
}