package com.fittrackpro.app;

import android.app.Application;
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

        // Initialize sync on app start if user is logged in
        String userId = getCurrentUserId();
        if (userId != null) {
            SyncManager.getInstance(this).schedulePeriodicSync(userId);
        }
    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
