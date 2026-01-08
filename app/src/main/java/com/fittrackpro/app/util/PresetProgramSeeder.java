package com.fittrackpro.app.util;

import android.util.Log;

import com.fittrackpro.app.data.model.ProgramExercise;
import com.fittrackpro.app.data.model.WorkoutDay;
import com.fittrackpro.app.data.model.WorkoutProgram;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * PresetProgramSeeder seeds preset workout programs to Firestore.
 * 
 * Seeds 9 professionally designed programs:
 * - 3 Beginner programs
 * - 3 Intermediate programs  
 * - 3 Advanced programs
 */
public class PresetProgramSeeder {
    
    private static final String TAG = "PresetProgramSeeder";
    
    /**
     * Seed all preset programs to Firestore.
     * Checks if programs already exist to avoid duplicates.
     * 
     * Note: For optimal performance, ensure a Firestore index exists on the
     * 'isPreset' field in the workoutPrograms collection. Firebase will
     * typically create this automatically on first query.
     */
    public static void seedPresetPrograms(OnSeedCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Check if already seeded
        db.collection(Constants.COLLECTION_WORKOUT_PROGRAMS)
            .whereEqualTo("isPreset", true)
            .limit(1)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                if (!querySnapshot.isEmpty()) {
                    Log.d(TAG, "Preset programs already exist, skipping seed");
                    listener.onComplete(true, "Already seeded");
                    return;
                }
                
                Log.d(TAG, "Starting preset programs seeding...");
                seedAllPrograms(db, listener);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to check existing programs", e);
                listener.onComplete(false, "Failed to check existing programs");
            });
    }
    
    private static void seedAllPrograms(FirebaseFirestore db, OnSeedCompleteListener listener) {
        try {
            // Seed all programs - these are asynchronous operations
            // that will complete in the background
            seedBeginnerPrograms(db);
            seedIntermediatePrograms(db);
            seedAdvancedPrograms(db);
            
            // Note: The actual Firestore writes are asynchronous and will complete
            // in the background. The listener is called to indicate seeding has been
            // initiated. The SharedPreferences flag prevents duplicate seeding attempts.
            Log.d(TAG, "Initiated seeding of 9 preset programs");
            listener.onComplete(true, "Seeding initiated for 9 preset programs");
        } catch (Exception e) {
            Log.e(TAG, "Error initiating program seeding", e);
            listener.onComplete(false, "Error: " + e.getMessage());
        }
    }
    
    // ==================== BEGINNER PROGRAMS ====================
    
    private static void seedBeginnerPrograms(FirebaseFirestore db) {
        // Program 1: Starting Strength
        List<WorkoutDayData> startingStrengthDays = new ArrayList<>();
        
        // Day 1 - Workout A
        List<ExerciseData> dayA = new ArrayList<>();
        dayA.add(new ExerciseData("Barbell Squat", "Legs", "barbell", 3, 5, 5, 180, 1, "Focus on depth and form"));
        dayA.add(new ExerciseData("Bench Press", "Chest", "barbell", 3, 5, 5, 180, 2, "Keep elbows at 45 degrees"));
        dayA.add(new ExerciseData("Deadlift", "Back", "barbell", 1, 5, 5, 240, 3, "One heavy set"));
        startingStrengthDays.add(new WorkoutDayData("day_1", "Workout A", 1, dayA));
        
        // Day 2 - Workout B
        List<ExerciseData> dayB = new ArrayList<>();
        dayB.add(new ExerciseData("Barbell Squat", "Legs", "barbell", 3, 5, 5, 180, 1, "Focus on depth and form"));
        dayB.add(new ExerciseData("Overhead Press", "Shoulders", "barbell", 3, 5, 5, 180, 2, "Strict press, no leg drive"));
        dayB.add(new ExerciseData("Barbell Row", "Back", "barbell", 3, 5, 5, 180, 3, "Pull to lower chest"));
        startingStrengthDays.add(new WorkoutDayData("day_2", "Workout B", 2, dayB));
        
        createProgram(db, Constants.PRESET_STARTING_STRENGTH, "Starting Strength",
                "Classic beginner program focused on compound lifts. Build foundational strength with progressive overload on the big three lifts.",
                Constants.DIFFICULTY_BEGINNER, 8, 3, startingStrengthDays);
        
        // Program 2: Full Body Basics
        List<WorkoutDayData> fullBodyDays = new ArrayList<>();
        
        // Day 1
        List<ExerciseData> fbDay1 = new ArrayList<>();
        fbDay1.add(new ExerciseData("Goblet Squat", "Legs", "dumbbell", 3, 10, 10, 90, 1, "Hold dumbbell at chest"));
        fbDay1.add(new ExerciseData("Push-ups", "Chest", "bodyweight", 3, 10, 10, 60, 2, "Full range of motion"));
        fbDay1.add(new ExerciseData("Dumbbell Row", "Back", "dumbbell", 3, 10, 10, 90, 3, "One arm at a time"));
        fbDay1.add(new ExerciseData("Plank", "Core", "bodyweight", 3, 30, 30, 60, 4, "30 seconds, keep body straight"));
        fullBodyDays.add(new WorkoutDayData("day_1", "Full Body Day 1", 1, fbDay1));
        
        // Day 2
        List<ExerciseData> fbDay2 = new ArrayList<>();
        fbDay2.add(new ExerciseData("Leg Press", "Legs", "machine", 3, 12, 12, 90, 1, "Full range of motion"));
        fbDay2.add(new ExerciseData("Dumbbell Chest Press", "Chest", "dumbbell", 3, 10, 10, 90, 2, "Press at 45-degree angle"));
        fbDay2.add(new ExerciseData("Lat Pulldown", "Back", "cable", 3, 10, 10, 90, 3, "Pull to upper chest"));
        fbDay2.add(new ExerciseData("Bicycle Crunches", "Core", "bodyweight", 3, 15, 15, 60, 4, "Alternate sides"));
        fullBodyDays.add(new WorkoutDayData("day_2", "Full Body Day 2", 2, fbDay2));
        
        // Day 3
        List<ExerciseData> fbDay3 = new ArrayList<>();
        fbDay3.add(new ExerciseData("Romanian Deadlift", "Legs", "barbell", 3, 10, 10, 120, 1, "Keep back straight"));
        fbDay3.add(new ExerciseData("Shoulder Press", "Shoulders", "dumbbell", 3, 10, 10, 90, 2, "Press overhead"));
        fbDay3.add(new ExerciseData("Cable Row", "Back", "cable", 3, 10, 10, 90, 3, "Pull to lower chest"));
        fbDay3.add(new ExerciseData("Mountain Climbers", "Core", "bodyweight", 3, 20, 20, 60, 4, "10 per side"));
        fullBodyDays.add(new WorkoutDayData("day_3", "Full Body Day 3", 3, fbDay3));
        
        createProgram(db, Constants.PRESET_FULL_BODY, "Full Body Basics",
                "Beginner-friendly full body workout hitting all major muscle groups. Perfect for building a fitness foundation.",
                Constants.DIFFICULTY_BEGINNER, 6, 3, fullBodyDays);
        
        // Program 3: Bodyweight Beginner
        List<WorkoutDayData> bodyweightDays = new ArrayList<>();
        
        // Day 1 - Upper
        List<ExerciseData> bwDay1 = new ArrayList<>();
        bwDay1.add(new ExerciseData("Push-ups", "Chest", "bodyweight", 3, 8, 8, 60, 1, "Full range of motion"));
        bwDay1.add(new ExerciseData("Pike Push-ups", "Shoulders", "bodyweight", 3, 8, 8, 60, 2, "Hips high, push vertical"));
        bwDay1.add(new ExerciseData("Inverted Rows", "Back", "bodyweight", 3, 8, 8, 60, 3, "Use table or bar"));
        bwDay1.add(new ExerciseData("Plank", "Core", "bodyweight", 3, 30, 30, 60, 4, "30 seconds hold"));
        bodyweightDays.add(new WorkoutDayData("day_1", "Upper Body", 1, bwDay1));
        
        // Day 2 - Lower
        List<ExerciseData> bwDay2 = new ArrayList<>();
        bwDay2.add(new ExerciseData("Bodyweight Squats", "Legs", "bodyweight", 3, 15, 15, 60, 1, "Full depth"));
        bwDay2.add(new ExerciseData("Lunges", "Legs", "bodyweight", 3, 10, 10, 60, 2, "10 per leg"));
        bwDay2.add(new ExerciseData("Glute Bridges", "Legs", "bodyweight", 3, 15, 15, 60, 3, "Squeeze at top"));
        bwDay2.add(new ExerciseData("Calf Raises", "Legs", "bodyweight", 3, 20, 20, 45, 4, "Full range"));
        bodyweightDays.add(new WorkoutDayData("day_2", "Lower Body", 2, bwDay2));
        
        // Day 3 - Full Body
        List<ExerciseData> bwDay3 = new ArrayList<>();
        bwDay3.add(new ExerciseData("Burpees", "Full Body", "bodyweight", 3, 10, 10, 90, 1, "Full movement"));
        bwDay3.add(new ExerciseData("Jump Squats", "Legs", "bodyweight", 3, 10, 10, 90, 2, "Explosive movement"));
        bwDay3.add(new ExerciseData("Diamond Push-ups", "Chest", "bodyweight", 3, 8, 8, 60, 3, "Hands together"));
        bwDay3.add(new ExerciseData("Leg Raises", "Core", "bodyweight", 3, 12, 12, 60, 4, "Control the descent"));
        bodyweightDays.add(new WorkoutDayData("day_3", "Full Body", 3, bwDay3));
        
        // Day 4 - Core
        List<ExerciseData> bwDay4 = new ArrayList<>();
        bwDay4.add(new ExerciseData("Plank", "Core", "bodyweight", 3, 45, 45, 60, 1, "45 seconds hold"));
        bwDay4.add(new ExerciseData("Side Plank", "Core", "bodyweight", 3, 30, 30, 60, 2, "30 seconds each side"));
        bwDay4.add(new ExerciseData("Dead Bug", "Core", "bodyweight", 3, 12, 12, 60, 3, "Opposite arm and leg"));
        bwDay4.add(new ExerciseData("Superman", "Core", "bodyweight", 3, 15, 15, 60, 4, "Hold at top"));
        bodyweightDays.add(new WorkoutDayData("day_4", "Core Focus", 4, bwDay4));
        
        createProgram(db, Constants.PRESET_BODYWEIGHT, "Bodyweight Beginner",
                "No equipment needed! Build strength using your bodyweight with progressive variations.",
                Constants.DIFFICULTY_BEGINNER, 4, 4, bodyweightDays);
    }
    
    // ==================== INTERMEDIATE PROGRAMS ====================
    
    private static void seedIntermediatePrograms(FirebaseFirestore db) {
        // Program 4: Push Pull Legs (PPL)
        List<WorkoutDayData> pplDays = new ArrayList<>();
        
        // Day 1 - Push
        List<ExerciseData> push = new ArrayList<>();
        push.add(new ExerciseData("Bench Press", "Chest", "barbell", 4, 8, 8, 180, 1, "Control the descent"));
        push.add(new ExerciseData("Overhead Press", "Shoulders", "barbell", 3, 10, 10, 120, 2, "Strict press"));
        push.add(new ExerciseData("Incline Dumbbell Press", "Chest", "dumbbell", 3, 10, 10, 120, 3, "30-45 degree angle"));
        push.add(new ExerciseData("Tricep Dips", "Arms", "bodyweight", 3, 12, 12, 90, 4, "Lean forward for chest"));
        push.add(new ExerciseData("Lateral Raises", "Shoulders", "dumbbell", 3, 15, 15, 60, 5, "Controlled movement"));
        pplDays.add(new WorkoutDayData("day_1", "Push", 1, push));
        
        // Day 2 - Pull
        List<ExerciseData> pull = new ArrayList<>();
        pull.add(new ExerciseData("Deadlift", "Back", "barbell", 4, 6, 6, 240, 1, "Keep back neutral"));
        pull.add(new ExerciseData("Pull-ups", "Back", "bodyweight", 3, 8, 8, 180, 2, "Full range of motion"));
        pull.add(new ExerciseData("Barbell Row", "Back", "barbell", 3, 10, 10, 120, 3, "Pull to lower chest"));
        pull.add(new ExerciseData("Face Pulls", "Shoulders", "cable", 3, 15, 15, 60, 4, "Pull to face level"));
        pull.add(new ExerciseData("Bicep Curls", "Arms", "dumbbell", 3, 12, 12, 90, 5, "Control the eccentric"));
        pplDays.add(new WorkoutDayData("day_2", "Pull", 2, pull));
        
        // Day 3 - Legs
        List<ExerciseData> legs = new ArrayList<>();
        legs.add(new ExerciseData("Barbell Squat", "Legs", "barbell", 4, 8, 8, 180, 1, "Depth below parallel"));
        legs.add(new ExerciseData("Romanian Deadlift", "Legs", "barbell", 3, 10, 10, 120, 2, "Feel the hamstring stretch"));
        legs.add(new ExerciseData("Leg Press", "Legs", "machine", 3, 12, 12, 90, 3, "Full range of motion"));
        legs.add(new ExerciseData("Leg Curl", "Legs", "machine", 3, 12, 12, 90, 4, "Squeeze at top"));
        legs.add(new ExerciseData("Calf Raises", "Legs", "machine", 4, 15, 15, 60, 5, "Full stretch at bottom"));
        pplDays.add(new WorkoutDayData("day_3", "Legs", 3, legs));
        
        // Days 4-6 repeat the pattern
        pplDays.add(new WorkoutDayData("day_4", "Push", 4, push));
        pplDays.add(new WorkoutDayData("day_5", "Pull", 5, pull));
        pplDays.add(new WorkoutDayData("day_6", "Legs", 6, legs));
        
        createProgram(db, Constants.PRESET_PPL, "Push Pull Legs (PPL)",
                "Popular intermediate split dividing workouts by movement patterns. Increase training volume and frequency.",
                Constants.DIFFICULTY_INTERMEDIATE, 12, 6, pplDays);
        
        // Program 5: Upper Lower Split
        List<WorkoutDayData> ulDays = new ArrayList<>();
        
        // Day 1 - Upper A
        List<ExerciseData> upperA = new ArrayList<>();
        upperA.add(new ExerciseData("Bench Press", "Chest", "barbell", 4, 6, 6, 180, 1, "Progressive overload"));
        upperA.add(new ExerciseData("Barbell Row", "Back", "barbell", 4, 8, 8, 120, 2, "Pull explosively"));
        upperA.add(new ExerciseData("Overhead Press", "Shoulders", "barbell", 3, 8, 8, 120, 3, "Strict form"));
        upperA.add(new ExerciseData("Lat Pulldown", "Back", "cable", 3, 10, 10, 90, 4, "Wide grip"));
        upperA.add(new ExerciseData("Tricep Extensions", "Arms", "cable", 3, 12, 12, 60, 5, "Full stretch"));
        ulDays.add(new WorkoutDayData("day_1", "Upper A", 1, upperA));
        
        // Day 2 - Lower A
        List<ExerciseData> lowerA = new ArrayList<>();
        lowerA.add(new ExerciseData("Barbell Squat", "Legs", "barbell", 4, 6, 6, 180, 1, "Deep and controlled"));
        lowerA.add(new ExerciseData("Romanian Deadlift", "Legs", "barbell", 3, 8, 8, 120, 2, "Hip hinge movement"));
        lowerA.add(new ExerciseData("Leg Press", "Legs", "machine", 3, 10, 10, 90, 3, "Full range"));
        lowerA.add(new ExerciseData("Leg Curl", "Legs", "machine", 3, 12, 12, 90, 4, "Controlled tempo"));
        lowerA.add(new ExerciseData("Ab Wheel", "Core", "equipment", 3, 10, 10, 90, 5, "From knees if needed"));
        ulDays.add(new WorkoutDayData("day_2", "Lower A", 2, lowerA));
        
        // Day 3 - Upper B
        List<ExerciseData> upperB = new ArrayList<>();
        upperB.add(new ExerciseData("Incline Bench Press", "Chest", "barbell", 4, 8, 8, 180, 1, "30-45 degrees"));
        upperB.add(new ExerciseData("Pull-ups", "Back", "bodyweight", 4, 6, 6, 180, 2, "Add weight if possible"));
        upperB.add(new ExerciseData("Dumbbell Shoulder Press", "Shoulders", "dumbbell", 3, 10, 10, 90, 3, "Neutral grip"));
        upperB.add(new ExerciseData("Cable Row", "Back", "cable", 3, 10, 10, 90, 4, "Squeeze shoulder blades"));
        upperB.add(new ExerciseData("Bicep Curls", "Arms", "dumbbell", 3, 12, 12, 60, 5, "Supinated grip"));
        ulDays.add(new WorkoutDayData("day_3", "Upper B", 3, upperB));
        
        // Day 4 - Lower B
        List<ExerciseData> lowerB = new ArrayList<>();
        lowerB.add(new ExerciseData("Deadlift", "Back", "barbell", 4, 5, 5, 240, 1, "Heavy and explosive"));
        lowerB.add(new ExerciseData("Front Squat", "Legs", "barbell", 3, 8, 8, 180, 2, "Keep chest up"));
        lowerB.add(new ExerciseData("Lunges", "Legs", "dumbbell", 3, 10, 10, 90, 3, "10 per leg"));
        lowerB.add(new ExerciseData("Leg Extensions", "Legs", "machine", 3, 12, 12, 60, 4, "Squeeze at top"));
        lowerB.add(new ExerciseData("Planks", "Core", "bodyweight", 3, 45, 45, 60, 5, "45 seconds hold"));
        ulDays.add(new WorkoutDayData("day_4", "Lower B", 4, lowerB));
        
        createProgram(db, Constants.PRESET_UPPER_LOWER, "Upper Lower 4-Day Split",
                "Efficient 4-day split alternating upper and lower body. Great for strength and muscle building.",
                Constants.DIFFICULTY_INTERMEDIATE, 10, 4, ulDays);
        
        // Program 6: PHAT
        List<WorkoutDayData> phatDays = new ArrayList<>();
        
        // Day 1 - Upper Power
        List<ExerciseData> upperPower = new ArrayList<>();
        upperPower.add(new ExerciseData("Bench Press", "Chest", "barbell", 3, 3, 3, 300, 1, "Heavy weight, low reps"));
        upperPower.add(new ExerciseData("Weighted Pull-ups", "Back", "bodyweight", 3, 5, 5, 240, 2, "Add weight"));
        upperPower.add(new ExerciseData("Barbell Row", "Back", "barbell", 3, 5, 5, 180, 3, "Explosive pull"));
        upperPower.add(new ExerciseData("Overhead Press", "Shoulders", "barbell", 3, 5, 5, 180, 4, "Strict press"));
        phatDays.add(new WorkoutDayData("day_1", "Upper Power", 1, upperPower));
        
        // Day 2 - Lower Power
        List<ExerciseData> lowerPower = new ArrayList<>();
        lowerPower.add(new ExerciseData("Barbell Squat", "Legs", "barbell", 3, 3, 3, 300, 1, "Heavy, focus on form"));
        lowerPower.add(new ExerciseData("Deadlift", "Back", "barbell", 3, 3, 3, 300, 2, "Maximal effort"));
        lowerPower.add(new ExerciseData("Leg Press", "Legs", "machine", 3, 5, 5, 180, 3, "Heavy load"));
        lowerPower.add(new ExerciseData("Leg Curl", "Legs", "machine", 3, 5, 5, 120, 4, "Controlled tempo"));
        phatDays.add(new WorkoutDayData("day_2", "Lower Power", 2, lowerPower));
        
        // Day 3 - Chest/Arms Hypertrophy
        List<ExerciseData> chestArms = new ArrayList<>();
        chestArms.add(new ExerciseData("Incline Dumbbell Press", "Chest", "dumbbell", 4, 10, 10, 90, 1, "Higher volume"));
        chestArms.add(new ExerciseData("Dumbbell Flyes", "Chest", "dumbbell", 3, 12, 12, 90, 2, "Full stretch"));
        chestArms.add(new ExerciseData("Tricep Extensions", "Arms", "cable", 4, 12, 12, 60, 3, "Pump focus"));
        chestArms.add(new ExerciseData("Bicep Curls", "Arms", "dumbbell", 4, 12, 12, 60, 4, "Squeeze at top"));
        phatDays.add(new WorkoutDayData("day_3", "Chest/Arms Hypertrophy", 3, chestArms));
        
        // Day 4 - Back/Shoulders Hypertrophy
        List<ExerciseData> backShoulders = new ArrayList<>();
        backShoulders.add(new ExerciseData("Lat Pulldown", "Back", "cable", 4, 10, 10, 90, 1, "Wide grip"));
        backShoulders.add(new ExerciseData("Cable Row", "Back", "cable", 4, 10, 10, 90, 2, "Squeeze scapula"));
        backShoulders.add(new ExerciseData("Lateral Raises", "Shoulders", "dumbbell", 4, 12, 12, 60, 3, "Pump work"));
        backShoulders.add(new ExerciseData("Face Pulls", "Shoulders", "cable", 3, 15, 15, 60, 4, "High reps"));
        phatDays.add(new WorkoutDayData("day_4", "Back/Shoulders Hypertrophy", 4, backShoulders));
        
        // Day 5 - Legs Hypertrophy
        List<ExerciseData> legsHypertrophy = new ArrayList<>();
        legsHypertrophy.add(new ExerciseData("Leg Press", "Legs", "machine", 4, 12, 12, 90, 1, "Volume focus"));
        legsHypertrophy.add(new ExerciseData("Romanian Deadlift", "Legs", "barbell", 4, 10, 10, 120, 2, "Feel the stretch"));
        legsHypertrophy.add(new ExerciseData("Leg Extensions", "Legs", "machine", 3, 15, 15, 60, 3, "Pump work"));
        legsHypertrophy.add(new ExerciseData("Leg Curl", "Legs", "machine", 3, 15, 15, 60, 4, "High reps"));
        legsHypertrophy.add(new ExerciseData("Calf Raises", "Legs", "machine", 4, 15, 15, 60, 5, "Full range"));
        phatDays.add(new WorkoutDayData("day_5", "Legs Hypertrophy", 5, legsHypertrophy));
        
        createProgram(db, Constants.PRESET_PHAT, "PHAT",
                "Combines powerlifting and bodybuilding. 2 power days, 3 hypertrophy days per week.",
                Constants.DIFFICULTY_INTERMEDIATE, 12, 5, phatDays);
    }
    
    // ==================== ADVANCED PROGRAMS ====================
    
    private static void seedAdvancedPrograms(FirebaseFirestore db) {
        // Program 7: 5/3/1 Wendler
        List<WorkoutDayData> wendlerDays = new ArrayList<>();
        
        // Day 1 - Squat
        List<ExerciseData> squatDay = new ArrayList<>();
        squatDay.add(new ExerciseData("Barbell Squat", "Legs", "barbell", 3, 3, 5, 240, 1, "5/3/1 progression scheme"));
        squatDay.add(new ExerciseData("Leg Press", "Legs", "machine", 5, 10, 10, 90, 2, "Boring But Big accessory"));
        squatDay.add(new ExerciseData("Leg Curl", "Legs", "machine", 3, 12, 12, 60, 3, "Volume work"));
        squatDay.add(new ExerciseData("Ab Wheel", "Core", "equipment", 3, 15, 15, 60, 4, "Core work"));
        wendlerDays.add(new WorkoutDayData("day_1", "Squat 5/3/1", 1, squatDay));
        
        // Day 2 - Bench
        List<ExerciseData> benchDay = new ArrayList<>();
        benchDay.add(new ExerciseData("Bench Press", "Chest", "barbell", 3, 3, 5, 240, 1, "5/3/1 progression scheme"));
        benchDay.add(new ExerciseData("Incline Bench Press", "Chest", "barbell", 5, 10, 10, 90, 2, "Boring But Big accessory"));
        benchDay.add(new ExerciseData("Tricep Extensions", "Arms", "cable", 3, 12, 12, 60, 3, "Arm work"));
        benchDay.add(new ExerciseData("Face Pulls", "Shoulders", "cable", 3, 15, 15, 60, 4, "Rear delt health"));
        wendlerDays.add(new WorkoutDayData("day_2", "Bench 5/3/1", 2, benchDay));
        
        // Day 3 - Deadlift
        List<ExerciseData> deadliftDay = new ArrayList<>();
        deadliftDay.add(new ExerciseData("Deadlift", "Back", "barbell", 3, 3, 5, 300, 1, "5/3/1 progression scheme"));
        deadliftDay.add(new ExerciseData("Romanian Deadlift", "Legs", "barbell", 5, 10, 10, 90, 2, "Boring But Big accessory"));
        deadliftDay.add(new ExerciseData("Pull-ups", "Back", "bodyweight", 3, 0, 0, 120, 3, "Max reps each set"));
        deadliftDay.add(new ExerciseData("Hanging Leg Raises", "Core", "bodyweight", 3, 15, 15, 60, 4, "Core work"));
        wendlerDays.add(new WorkoutDayData("day_3", "Deadlift 5/3/1", 3, deadliftDay));
        
        // Day 4 - OHP
        List<ExerciseData> ohpDay = new ArrayList<>();
        ohpDay.add(new ExerciseData("Overhead Press", "Shoulders", "barbell", 3, 3, 5, 240, 1, "5/3/1 progression scheme"));
        ohpDay.add(new ExerciseData("Dumbbell Shoulder Press", "Shoulders", "dumbbell", 5, 10, 10, 90, 2, "Boring But Big accessory"));
        ohpDay.add(new ExerciseData("Lateral Raises", "Shoulders", "dumbbell", 3, 15, 15, 60, 3, "Pump work"));
        ohpDay.add(new ExerciseData("Bicep Curls", "Arms", "dumbbell", 3, 12, 12, 60, 4, "Arm work"));
        wendlerDays.add(new WorkoutDayData("day_4", "OHP 5/3/1", 4, ohpDay));
        
        createProgram(db, Constants.PRESET_531_WENDLER, "5/3/1 Wendler",
                "Legendary strength program with monthly progression on main lifts. Includes deload weeks.",
                Constants.DIFFICULTY_ADVANCED, 16, 4, wendlerDays);
        
        // Program 8: German Volume Training (GVT)
        List<WorkoutDayData> gvtDays = new ArrayList<>();
        
        // Day 1 - Chest/Back
        List<ExerciseData> gvtChestBack = new ArrayList<>();
        gvtChestBack.add(new ExerciseData("Bench Press", "Chest", "barbell", 10, 10, 10, 90, 1, "10x10 at 60% 1RM"));
        gvtChestBack.add(new ExerciseData("Barbell Row", "Back", "barbell", 10, 10, 10, 90, 2, "10x10 at 60% 1RM"));
        gvtChestBack.add(new ExerciseData("Dumbbell Flyes", "Chest", "dumbbell", 3, 12, 12, 60, 3, "Finishing work"));
        gvtChestBack.add(new ExerciseData("Lat Pulldown", "Back", "cable", 3, 12, 12, 60, 4, "Finishing work"));
        gvtDays.add(new WorkoutDayData("day_1", "Chest/Back GVT", 1, gvtChestBack));
        
        // Day 2 - Legs
        List<ExerciseData> gvtLegs = new ArrayList<>();
        gvtLegs.add(new ExerciseData("Barbell Squat", "Legs", "barbell", 10, 10, 10, 90, 1, "10x10 at 60% 1RM"));
        gvtLegs.add(new ExerciseData("Leg Curl", "Legs", "machine", 10, 10, 10, 60, 2, "10x10 moderate weight"));
        gvtLegs.add(new ExerciseData("Calf Raises", "Legs", "machine", 3, 20, 20, 60, 3, "Finishing work"));
        gvtLegs.add(new ExerciseData("Planks", "Core", "bodyweight", 3, 20, 20, 60, 4, "Ab work"));
        gvtDays.add(new WorkoutDayData("day_2", "Legs GVT", 2, gvtLegs));
        
        // Day 3 - Shoulders/Arms
        List<ExerciseData> gvtShouldersArms = new ArrayList<>();
        gvtShouldersArms.add(new ExerciseData("Overhead Press", "Shoulders", "barbell", 10, 10, 10, 90, 1, "10x10 at 60% 1RM"));
        gvtShouldersArms.add(new ExerciseData("Dips", "Arms", "bodyweight", 10, 10, 10, 60, 2, "10x10 bodyweight"));
        gvtShouldersArms.add(new ExerciseData("Lateral Raises", "Shoulders", "dumbbell", 3, 15, 15, 60, 3, "Finishing work"));
        gvtShouldersArms.add(new ExerciseData("Bicep Curls", "Arms", "dumbbell", 3, 15, 15, 60, 4, "Finishing work"));
        gvtDays.add(new WorkoutDayData("day_3", "Shoulders/Arms GVT", 3, gvtShouldersArms));
        
        // Day 4 - Full Body Power
        List<ExerciseData> gvtPower = new ArrayList<>();
        gvtPower.add(new ExerciseData("Deadlift", "Back", "barbell", 5, 5, 5, 240, 1, "Heavy power day"));
        gvtPower.add(new ExerciseData("Pull-ups", "Back", "bodyweight", 5, 5, 5, 180, 2, "Weighted if possible"));
        gvtPower.add(new ExerciseData("Front Squat", "Legs", "barbell", 5, 5, 5, 180, 3, "Power work"));
        gvtPower.add(new ExerciseData("Push Press", "Shoulders", "barbell", 5, 5, 5, 180, 4, "Explosive"));
        gvtDays.add(new WorkoutDayData("day_4", "Full Body Power", 4, gvtPower));
        
        createProgram(db, Constants.PRESET_GVT, "German Volume Training",
                "High volume program with 10x10 scheme. Extreme muscle growth focus. Not for beginners!",
                Constants.DIFFICULTY_ADVANCED, 6, 4, gvtDays);
        
        // Program 9: Arnold Split
        List<WorkoutDayData> arnoldDays = new ArrayList<>();
        
        // Day 1 - Chest/Back
        List<ExerciseData> arnoldChestBack = new ArrayList<>();
        arnoldChestBack.add(new ExerciseData("Bench Press", "Chest", "barbell", 4, 10, 10, 120, 1, "Classic mass builder"));
        arnoldChestBack.add(new ExerciseData("Barbell Row", "Back", "barbell", 4, 10, 10, 120, 2, "Superset with bench"));
        arnoldChestBack.add(new ExerciseData("Incline Dumbbell Press", "Chest", "dumbbell", 4, 10, 10, 90, 3, "Upper chest focus"));
        arnoldChestBack.add(new ExerciseData("Pull-ups", "Back", "bodyweight", 4, 10, 10, 120, 4, "Wide grip"));
        arnoldChestBack.add(new ExerciseData("Dumbbell Flyes", "Chest", "dumbbell", 3, 12, 12, 60, 5, "Pump work"));
        arnoldChestBack.add(new ExerciseData("Cable Row", "Back", "cable", 3, 12, 12, 60, 6, "Squeeze hard"));
        arnoldDays.add(new WorkoutDayData("day_1", "Chest/Back", 1, arnoldChestBack));
        
        // Day 2 - Shoulders/Arms
        List<ExerciseData> arnoldShouldersArms = new ArrayList<>();
        arnoldShouldersArms.add(new ExerciseData("Military Press", "Shoulders", "barbell", 4, 10, 10, 120, 1, "Strict form"));
        arnoldShouldersArms.add(new ExerciseData("Barbell Curls", "Arms", "barbell", 4, 10, 10, 90, 2, "Superset with press"));
        arnoldShouldersArms.add(new ExerciseData("Lateral Raises", "Shoulders", "dumbbell", 4, 12, 12, 60, 3, "Side delts"));
        arnoldShouldersArms.add(new ExerciseData("Tricep Extensions", "Arms", "cable", 4, 10, 10, 90, 4, "Overhead"));
        arnoldShouldersArms.add(new ExerciseData("Front Raises", "Shoulders", "dumbbell", 3, 12, 12, 60, 5, "Front delts"));
        arnoldShouldersArms.add(new ExerciseData("Hammer Curls", "Arms", "dumbbell", 3, 12, 12, 60, 6, "Brachialis work"));
        arnoldDays.add(new WorkoutDayData("day_2", "Shoulders/Arms", 2, arnoldShouldersArms));
        
        // Day 3 - Legs
        List<ExerciseData> arnoldLegs = new ArrayList<>();
        arnoldLegs.add(new ExerciseData("Barbell Squat", "Legs", "barbell", 4, 10, 10, 180, 1, "Go deep"));
        arnoldLegs.add(new ExerciseData("Leg Press", "Legs", "machine", 4, 12, 12, 120, 2, "High volume"));
        arnoldLegs.add(new ExerciseData("Romanian Deadlift", "Legs", "barbell", 4, 10, 10, 120, 3, "Hamstrings"));
        arnoldLegs.add(new ExerciseData("Leg Extensions", "Legs", "machine", 3, 15, 15, 60, 4, "Pump work"));
        arnoldLegs.add(new ExerciseData("Leg Curl", "Legs", "machine", 3, 15, 15, 60, 5, "Hamstring isolation"));
        arnoldLegs.add(new ExerciseData("Calf Raises", "Legs", "machine", 5, 15, 15, 60, 6, "High volume calves"));
        arnoldDays.add(new WorkoutDayData("day_3", "Legs", 3, arnoldLegs));
        
        // Days 4-6 repeat the pattern
        arnoldDays.add(new WorkoutDayData("day_4", "Chest/Back", 4, arnoldChestBack));
        arnoldDays.add(new WorkoutDayData("day_5", "Shoulders/Arms", 5, arnoldShouldersArms));
        arnoldDays.add(new WorkoutDayData("day_6", "Legs", 6, arnoldLegs));
        
        createProgram(db, Constants.PRESET_ARNOLD, "Arnold Schwarzenegger Split",
                "Classic 6-day bodybuilding split used by Arnold. High volume, high frequency for serious lifters.",
                Constants.DIFFICULTY_ADVANCED, 12, 6, arnoldDays);
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Create a program with workout days and exercises.
     */
    private static void createProgram(FirebaseFirestore db, String programId, String name,
                                     String description, String difficulty, int durationWeeks,
                                     int daysPerWeek, List<WorkoutDayData> days) {
        // Create program document
        WorkoutProgram program = new WorkoutProgram();
        program.setProgramId(programId);
        program.setUserId(null); // Preset programs have no user
        program.setProgramName(name);
        program.setDescription(description);
        program.setDifficulty(difficulty);
        program.setDurationWeeks(durationWeeks);
        program.setDaysPerWeek(daysPerWeek);
        program.setPreset(true);
        program.setActive(false);
        program.setCreatedAt(Timestamp.now());
        program.setUpdatedAt(Timestamp.now());
        
        db.collection(Constants.COLLECTION_WORKOUT_PROGRAMS)
            .document(programId)
            .set(program)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Created program: " + name);
                
                // Create workout days
                for (WorkoutDayData dayData : days) {
                    createWorkoutDay(db, programId, dayData);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to create program: " + name, e);
            });
    }
    
    /**
     * Create a workout day with exercises.
     */
    private static void createWorkoutDay(FirebaseFirestore db, String programId, WorkoutDayData dayData) {
        WorkoutDay day = new WorkoutDay();
        day.setDayId(dayData.dayId);
        day.setProgramId(programId);
        day.setDayName(dayData.dayName);
        day.setDayNumber(dayData.dayNumber);
        day.setWarmupEnabled(true);
        day.setCooldownEnabled(true);
        
        db.collection(Constants.COLLECTION_WORKOUT_PROGRAMS)
            .document(programId)
            .collection(Constants.COLLECTION_WORKOUT_DAYS)
            .document(dayData.dayId)
            .set(day)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Created day: " + dayData.dayName);
                
                // Create exercises
                for (ExerciseData exData : dayData.exercises) {
                    createExercise(db, programId, dayData.dayId, exData);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to create day: " + dayData.dayName, e);
            });
    }
    
    /**
     * Create an exercise.
     */
    private static void createExercise(FirebaseFirestore db, String programId, String dayId, ExerciseData exData) {
        ProgramExercise exercise = new ProgramExercise();
        exercise.setExerciseId(db.collection(Constants.COLLECTION_WORKOUT_PROGRAMS)
                .document(programId)
                .collection(Constants.COLLECTION_WORKOUT_DAYS)
                .document(dayId)
                .collection(Constants.COLLECTION_PROGRAM_EXERCISES)
                .document().getId());
        exercise.setDayId(dayId);
        exercise.setExerciseName(exData.name);
        exercise.setMuscleGroup(exData.muscleGroup);
        exercise.setEquipment(exData.equipment);
        exercise.setTargetSets(exData.sets);
        exercise.setTargetRepsMin(exData.repsMin);
        exercise.setTargetRepsMax(exData.repsMax);
        exercise.setRestSeconds(exData.restSeconds); // Add rest seconds
        exercise.setOrderIndex(exData.orderIndex);
        exercise.setNotes(exData.notes);
        
        db.collection(Constants.COLLECTION_WORKOUT_PROGRAMS)
            .document(programId)
            .collection(Constants.COLLECTION_WORKOUT_DAYS)
            .document(dayId)
            .collection(Constants.COLLECTION_PROGRAM_EXERCISES)
            .document(exercise.getExerciseId())
            .set(exercise)
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to create exercise: " + exData.name, e);
            });
    }
    
    // ==================== DATA CLASSES ====================
    
    /**
     * Helper class for workout day data.
     */
    private static class WorkoutDayData {
        String dayId;
        String dayName;
        int dayNumber;
        List<ExerciseData> exercises;
        
        WorkoutDayData(String dayId, String dayName, int dayNumber, List<ExerciseData> exercises) {
            this.dayId = dayId;
            this.dayName = dayName;
            this.dayNumber = dayNumber;
            this.exercises = exercises;
        }
    }
    
    /**
     * Helper class for exercise data.
     */
    private static class ExerciseData {
        String name;
        String muscleGroup;
        String equipment;
        int sets;
        int repsMin;
        int repsMax;
        int restSeconds;
        int orderIndex;
        String notes;
        
        ExerciseData(String name, String muscleGroup, String equipment, int sets, int repsMin, 
                    int repsMax, int restSeconds, int orderIndex, String notes) {
            this.name = name;
            this.muscleGroup = muscleGroup;
            this.equipment = equipment;
            this.sets = sets;
            this.repsMin = repsMin;
            this.repsMax = repsMax;
            this.restSeconds = restSeconds;
            this.orderIndex = orderIndex;
            this.notes = notes;
        }
    }
    
    /**
     * Callback interface for seeding completion.
     */
    public interface OnSeedCompleteListener {
        void onComplete(boolean success, String message);
    }
}
