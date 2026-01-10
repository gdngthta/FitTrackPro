package com.fittrackpro.app;

import android.app.Application;
import android.content.SharedPreferences;

import com.fittrackpro.app.data.repository.PresetProgramSeeder;
import com.fittrackpro.app.sync.SyncManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

/**
 * Application class for FitTrackPro.
 * Handles global initialization including Firestore offline persistence.
 */
public class FitTrackApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Firestore offline persistence
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        firestore.setFirestoreSettings(settings);

        // Seed preset programs on first launch
        seedPresetProgramsIfNeeded();
        
        // Seed common foods on first launch
        seedCommonFoodsIfNeeded();

        // Initialize sync on app start if user is logged in
        String userId = getCurrentUserId();
        if (userId != null) {
            SyncManager.getInstance(this).schedulePeriodicSync(userId);
        }
    }

    private void seedPresetProgramsIfNeeded() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean hasSeeded = prefs.getBoolean("preset_programs_seeded", false);

        if (!hasSeeded) {
            // Seed preset programs
            PresetProgramSeeder seeder = new PresetProgramSeeder();
            seeder.seedPresetPrograms();

            // Mark as seeded
            prefs.edit().putBoolean("preset_programs_seeded", true).apply();
        }
    }

    private void seedCommonFoodsIfNeeded() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean hasSeeded = prefs.getBoolean("common_foods_seeded", false);

        if (!hasSeeded) {
            // Seed common foods
            com.fittrackpro.app.data.local.AppDatabase database = 
                com.fittrackpro.app.data.local.AppDatabase.getInstance(this);
            com.fittrackpro.app.data.repository.NutritionRepository nutritionRepo = 
                new com.fittrackpro.app.data.repository.NutritionRepository(database);
            nutritionRepo.initializeCommonFoods();

            // Mark as seeded
            prefs.edit().putBoolean("common_foods_seeded", true).apply();
        }
    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
