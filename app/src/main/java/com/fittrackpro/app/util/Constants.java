package com.fittrackpro.app.util;

/**
 * Constants used throughout the app.
 *
 * IMPORTANT: No hardcoded user data here - only configuration constants.
 */
public class Constants {

    // Firestore collection names
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_NUTRITION_PROFILES = "nutritionProfiles";
    public static final String COLLECTION_WORKOUT_PROGRAMS = "workoutPrograms";
    public static final String COLLECTION_WORKOUT_DAYS = "workoutDays";
    public static final String COLLECTION_PROGRAM_EXERCISES = "programExercises";
    public static final String COLLECTION_COMPLETED_WORKOUTS = "completedWorkouts";
    public static final String COLLECTION_WORKOUT_SETS = "workoutSets";
    public static final String COLLECTION_PERSONAL_RECORDS = "personalRecords";
    public static final String COLLECTION_FOODS_DATABASE = "foodsDatabase";
    public static final String COLLECTION_MEALS_LOGGED = "mealsLogged";
    public static final String COLLECTION_FRIENDSHIPS = "friendships";

    // SharedPreferences keys
    public static final String PREF_NAME = "FitTrackPrefs";
    public static final String PREF_WEIGHT_UNIT = "weight_unit"; // "kg" or "lb"
    public static final String PREF_SHOW_EQUIVALENCE = "show_equivalence";
    public static final String PREF_USER_ID = "user_id";

    // Weight units
    public static final String UNIT_KG = "kg";
    public static final String UNIT_LB = "lb";

    // Workout set status
    public static final String SET_STATUS_COMPLETED = "completed";
    public static final String SET_STATUS_MODIFIED = "modified";
    public static final String SET_STATUS_SUBSTITUTED = "substituted";
    public static final String SET_STATUS_SKIPPED = "skipped";

    // Meal types
    public static final String MEAL_BREAKFAST = "breakfast";
    public static final String MEAL_LUNCH = "lunch";
    public static final String MEAL_DINNER = "dinner";
    public static final String MEAL_SNACKS = "snacks";

    // Activity levels
    public static final String ACTIVITY_SEDENTARY = "sedentary";
    public static final String ACTIVITY_LIGHT = "light";
    public static final String ACTIVITY_MODERATE = "moderate";
    public static final String ACTIVITY_ACTIVE = "active";
    public static final String ACTIVITY_VERY_ACTIVE = "very_active";

    // Goals
    public static final String GOAL_LOSE = "lose";
    public static final String GOAL_MAINTAIN = "maintain";
    public static final String GOAL_GAIN = "gain";

    // Gender
    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";

    // Difficulty levels
    public static final String DIFFICULTY_BEGINNER = "beginner";
    public static final String DIFFICULTY_INTERMEDIATE = "intermediate";
    public static final String DIFFICULTY_ADVANCED = "advanced";

    // PR types
    public static final String PR_TYPE_WEIGHT = "weight";
    public static final String PR_TYPE_REPS = "reps";
    public static final String PR_TYPE_VOLUME = "volume";

    // Default values
    public static final int DEFAULT_REST_TIMER_SECONDS = 90;
    public static final int MIN_REST_TIMER_SECONDS = 30;
    public static final int MAX_REST_TIMER_SECONDS = 300;
    public static final long VIBRATION_DURATION_MS = 500;
    public static final int LEADERBOARD_DEFAULT_LIMIT = 50;
    public static final int RECENT_WORKOUTS_LIMIT = 10;

    // Validation
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MIN_PASSWORD_LENGTH = 6;
}