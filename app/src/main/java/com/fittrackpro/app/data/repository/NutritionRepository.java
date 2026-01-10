package com.fittrackpro.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle. MutableLiveData;
import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data. local.dao.FoodDao;
import com.fittrackpro.app.data.local.entity.FoodEntity;
import com.fittrackpro.app.data.model.Food;
import com.fittrackpro.app.data.model.MealLogged;
import com.fittrackpro.app.data.model.NutritionProfile;
import com.fittrackpro.app.util.NutritionCalculator;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore. FirebaseFirestore;
import com.google.firebase.firestore. Query;
import com.google.firebase.firestore. QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent. Executors;

/**
 * NutritionRepository manages nutrition profiles, food database, and meal logging.
 *
 * Key responsibilities:
 * - Create/update nutrition profile with TDEE calculation
 * - Search food database
 * - Log meals
 * - Calculate daily macro totals
 */
public class NutritionRepository {

    private final FirebaseFirestore firestore;
    private final FoodDao foodDao;
    private final Executor executor;

    public NutritionRepository(AppDatabase database) {
        this.firestore = FirebaseFirestore.getInstance();
        this.foodDao = database.foodDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // ==================== NUTRITION PROFILE ====================

    /**
     * Get nutrition profile for user
     */
    public LiveData<NutritionProfile> getNutritionProfile(String userId) {
        MutableLiveData<NutritionProfile> result = new MutableLiveData<>();

        firestore.collection("nutritionProfiles")
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        NutritionProfile profile = querySnapshot.getDocuments().get(0).toObject(NutritionProfile.class);
                        result.setValue(profile);
                    } else {
                        result.setValue(null);
                    }
                })
                .addOnFailureListener(e -> result.setValue(null));

        return result;
    }

    /**
     * Create or update nutrition profile with calculated TDEE and macros
     */
    public LiveData<Boolean> saveNutritionProfile(String userId, double weight, double height,
                                                  int age, String gender, String activityLevel,
                                                  String goal) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        // Calculate BMR using Mifflin-St Jeor
        double bmr = NutritionCalculator.calculateBMR(weight, height, age, gender);

        // Calculate TDEE
        double tdee = NutritionCalculator.calculateTDEE(bmr, activityLevel);

        // Calculate target calories based on goal
        double targetCalories = NutritionCalculator. calculateTargetCalories(tdee, goal);

        // Calculate macros
        double[] macros = NutritionCalculator.calculateMacros(targetCalories, goal);

        String profileId = firestore.collection("nutritionProfiles").document().getId();

        NutritionProfile profile = new NutritionProfile();
        profile.setProfileId(profileId);
        profile.setUserId(userId);
        profile.setWeight(weight);
        profile.setHeight(height);
        profile.setAge(age);
        profile.setGender(gender);
        profile.setActivityLevel(activityLevel);
        profile.setGoal(goal);
        profile.setBmr(bmr);
        profile.setTdee(tdee);
        profile.setTargetCalories(targetCalories);
        profile.setTargetProtein(macros[0]);
        profile.setTargetCarbs(macros[1]);
        profile.setTargetFats(macros[2]);
        profile.setCreatedAt(Timestamp.now());
        profile.setUpdatedAt(Timestamp.now());

        firestore.collection("nutritionProfiles")
                .document(profileId)
                .set(profile)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));

        return result;
    }

    // ==================== FOOD DATABASE ====================

    /**
     * Search foods in database
     */
    public LiveData<List<Food>> searchFoods(String query) {
        MutableLiveData<List<Food>> result = new MutableLiveData<>();

        // Search in Firestore
        firestore.collection("foodsDatabase")
                .orderBy("foodName")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(50)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Food> foods = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Food food = doc.toObject(Food.class);
                        foods.add(food);
                    }
                    result.setValue(foods);

                    // Cache in Room
                    executor.execute(() -> {
                        for (Food f : foods) {
                            foodDao.insertFood(foodModelToEntity(f));
                        }
                    });
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

    /**
     * Get all foods (default list) - limited to first 50
     */
    public LiveData<List<Food>> getAllFoods() {
        MutableLiveData<List<Food>> result = new MutableLiveData<>();

        firestore.collection("foodsDatabase")
                .orderBy("foodName")
                .limit(50)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Food> foods = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Food food = doc.toObject(Food.class);
                        foods.add(food);
                    }
                    result.setValue(foods);

                    // Cache in Room
                    executor.execute(() -> {
                        for (Food f : foods) {
                            foodDao.insertFood(foodModelToEntity(f));
                        }
                    });
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

    /**
     * Get food by ID
     */
    public LiveData<Food> getFoodById(String foodId) {
        MutableLiveData<Food> result = new MutableLiveData<>();

        firestore. collection("foodsDatabase")
                .document(foodId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Food food = documentSnapshot. toObject(Food.class);
                    result.setValue(food);
                })
                .addOnFailureListener(e -> result.setValue(null));

        return result;
    }

    // ==================== MEAL LOGGING ====================

    /**
     * Log a meal
     */
    public LiveData<Boolean> logMeal(String userId, String foodId, String foodName, String mealType,
                                     double portionMultiplier, double calories, double protein,
                                     double carbs, double fats) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        String logId = firestore.collection("mealsLogged").document().getId();

        MealLogged meal = new MealLogged();
        meal.setLogId(logId);
        meal.setUserId(userId);
        meal.setFoodId(foodId);
        meal.setFoodName(foodName);
        meal.setMealType(mealType);
        meal.setPortionMultiplier(portionMultiplier);
        meal.setCalories(calories * portionMultiplier);
        meal.setProtein(protein * portionMultiplier);
        meal.setCarbs(carbs * portionMultiplier);
        meal.setFats(fats * portionMultiplier);
        meal.setLoggedAt(Timestamp.now());

        firestore.collection("mealsLogged")
                .document(logId)
                .set(meal)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));

        return result;
    }

    /**
     * Get meals for today
     */
    public LiveData<List<MealLogged>> getTodayMeals(String userId) {
        MutableLiveData<List<MealLogged>> result = new MutableLiveData<>();

        // Get start of today
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar. MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        firestore.collection("mealsLogged")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("loggedAt", new Timestamp(startOfDay))
                .orderBy("loggedAt", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<MealLogged> meals = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        MealLogged meal = doc.toObject(MealLogged.class);
                        meals.add(meal);
                    }
                    result.setValue(meals);
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

    /**
     * Get meals by meal type for today
     */
    public LiveData<List<MealLogged>> getTodayMealsByType(String userId, String mealType) {
        MutableLiveData<List<MealLogged>> result = new MutableLiveData<>();

        // Get start of today
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar. MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        firestore.collection("mealsLogged")
                .whereEqualTo("userId", userId)
                .whereEqualTo("mealType", mealType)
                .whereGreaterThanOrEqualTo("loggedAt", new Timestamp(startOfDay))
                .orderBy("loggedAt", Query.Direction. ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<MealLogged> meals = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        MealLogged meal = doc.toObject(MealLogged.class);
                        meals.add(meal);
                    }
                    result.setValue(meals);
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

    // ==================== CONVERSION HELPERS ====================

    private FoodEntity foodModelToEntity(Food food) {
        FoodEntity entity = new FoodEntity();
        entity.setFoodId(food.getFoodId());
        entity.setFoodName(food.getFoodName());
        entity.setBrand(food.getBrand());
        entity.setServingSize(food.getServingSize());
        entity.setServingUnit(food.getServingUnit());
        entity.setCalories(food.getCalories());
        entity.setProtein(food.getProtein());
        entity.setCarbs(food. getCarbs());
        entity.setFats(food.getFats());
        entity.setVerified(food.isVerified());
        return entity;
    }
}