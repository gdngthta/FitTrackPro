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
     * Initialize common foods in the database for quick access
     */
    public void initializeCommonFoods() {
        // Define common foods
        Object[][] commonFoods = {
            {"Chicken Breast", "", 100.0, "g", 165.0, 31.0, 0.0, 3.6, true},
            {"Eggs", "Large", 1.0, "egg", 72.0, 6.3, 0.6, 4.8, true},
            {"White Rice", "Cooked", 100.0, "g", 130.0, 2.7, 28.2, 0.3, true},
            {"Brown Rice", "Cooked", 100.0, "g", 112.0, 2.6, 23.5, 0.9, true},
            {"Oats", "Dry", 100.0, "g", 389.0, 16.9, 66.3, 6.9, true},
            {"Banana", "Medium", 1.0, "banana", 105.0, 1.3, 27.0, 0.4, true},
            {"Apple", "Medium", 1.0, "apple", 95.0, 0.5, 25.0, 0.3, true},
            {"Broccoli", "Raw", 100.0, "g", 34.0, 2.8, 7.0, 0.4, true},
            {"Salmon", "Cooked", 100.0, "g", 206.0, 22.0, 0.0, 12.4, true},
            {"Greek Yogurt", "Non-fat", 100.0, "g", 59.0, 10.2, 3.6, 0.4, true},
            {"Almonds", "", 28.0, "g", 164.0, 6.0, 6.1, 14.2, true},
            {"Sweet Potato", "Cooked", 100.0, "g", 90.0, 2.0, 20.7, 0.2, true},
            {"Ground Beef", "90% Lean", 100.0, "g", 176.0, 20.0, 0.0, 10.0, true},
            {"Whole Milk", "", 240.0, "ml", 149.0, 7.7, 11.7, 7.9, true},
            {"Peanut Butter", "", 32.0, "g", 188.0, 8.0, 7.0, 16.0, true},
            {"Bread", "Whole Wheat", 1.0, "slice", 80.0, 4.0, 14.0, 1.0, true},
            {"Pasta", "Cooked", 100.0, "g", 131.0, 5.1, 25.1, 1.1, true},
            {"Tuna", "Canned in water", 100.0, "g", 116.0, 25.5, 0.0, 0.8, true},
            {"Avocado", "Medium", 0.5, "avocado", 120.0, 1.5, 6.0, 11.0, true},
            {"Spinach", "Raw", 100.0, "g", 23.0, 2.9, 3.6, 0.4, true}
        };

        for (Object[] foodData : commonFoods) {
            String name = (String) foodData[0];
            String brand = (String) foodData[1];
            double servingSize = (double) foodData[2];
            String servingUnit = (String) foodData[3];
            double calories = (double) foodData[4];
            double protein = (double) foodData[5];
            double carbs = (double) foodData[6];
            double fats = (double) foodData[7];
            boolean verified = (boolean) foodData[8];

            // Check if food already exists
            firestore.collection("foodsDatabase")
                    .whereEqualTo("foodName", name)
                    .whereEqualTo("brand", brand)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot.isEmpty()) {
                            // Create new food
                            createCommonFood(name, brand, servingSize, servingUnit, 
                                           calories, protein, carbs, fats, verified);
                        }
                    })
                    .addOnFailureListener(e -> 
                        android.util.Log.e("NutritionRepository", "Failed to check existing food: " + name, e));
        }
    }

    /**
     * Create a common food entry
     */
    private void createCommonFood(String name, String brand, double servingSize, String servingUnit,
                                 double calories, double protein, double carbs, double fats, boolean verified) {
        String foodId = firestore.collection("foodsDatabase").document().getId();

        Food food = new Food();
        food.setFoodId(foodId);
        food.setFoodName(name);
        food.setBrand(brand);
        food.setServingSize(servingSize);
        food.setServingUnit(servingUnit);
        food.setCalories(calories);
        food.setProtein(protein);
        food.setCarbs(carbs);
        food.setFats(fats);
        food.setVerified(verified);

        firestore.collection("foodsDatabase")
                .document(foodId)
                .set(food)
                .addOnSuccessListener(aVoid -> 
                    android.util.Log.d("NutritionRepository", "Created common food: " + name))
                .addOnFailureListener(e -> 
                    android.util.Log.e("NutritionRepository", "Failed to create common food: " + name, e));
    }

    /**
     * Get common/popular foods for display before search
     */
    public LiveData<List<Food>> getCommonFoods() {
        MutableLiveData<List<Food>> result = new MutableLiveData<>();

        firestore.collection("foodsDatabase")
                .whereEqualTo("verified", true)
                .limit(20)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Food> foods = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Food food = doc.toObject(Food.class);
                        foods.add(food);
                    }
                    result.setValue(foods);
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

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
                    for (QueryDocumentSnapshot doc :  querySnapshot) {
                        Food food = doc.toObject(Food.class);
                        foods. add(food);
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