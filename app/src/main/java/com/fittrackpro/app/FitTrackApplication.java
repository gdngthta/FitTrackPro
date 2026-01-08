package com.fittrackpro.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.fittrackpro.app.util.Constants;
import com.fittrackpro.app.util.PresetProgramSeeder;

/**
 * FitTrackApplication - Application class for FitTrack Pro.
 * 
 * Handles one-time initialization tasks:
 * - Seeding preset workout programs to Firestore
 */
public class FitTrackApplication extends Application {
    
    private static final String TAG = "FitTrackApp";
    private static final String PREF_KEY_PRESET_SEEDED = "preset_programs_seeded";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d(TAG, "FitTrack Application starting...");
        
        // Seed preset programs on first launch
        seedPresetProgramsIfNeeded();
    }
    
    /**
     * Seeds preset workout programs to Firestore if not already done.
     * Uses SharedPreferences to track seeding status.
     */
    private void seedPresetProgramsIfNeeded() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        boolean alreadySeeded = prefs.getBoolean(PREF_KEY_PRESET_SEEDED, false);
        
        if (alreadySeeded) {
            Log.d(TAG, "Preset programs already seeded, skipping");
            return;
        }
        
        Log.d(TAG, "Starting preset programs seeding...");
        
        PresetProgramSeeder.seedPresetPrograms((success, message) -> {
            if (success) {
                // Mark as seeded in SharedPreferences
                prefs.edit().putBoolean(PREF_KEY_PRESET_SEEDED, true).apply();
                Log.d(TAG, "Preset programs seeded successfully: " + message);
            } else {
                Log.e(TAG, "Failed to seed preset programs: " + message);
            }
        });
    }
}
