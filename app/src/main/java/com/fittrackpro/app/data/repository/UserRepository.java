package com.fittrackpro.app.data.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data.local.dao.UserDao;
import com.fittrackpro.app.data.local.entity.UserEntity;
import com.fittrackpro.app.data.model.User;
import com.fittrackpro.app.sync.SyncManager;
import com.fittrackpro.app.util.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * UserRepository manages user data sync between Firestore and Room.
 *
 * Offline-first: reads from Room immediately, syncs with Firestore in background.
 * Writes go to Room immediately, then sync to Firestore when online.
 */
public class UserRepository {

    private final FirebaseFirestore firestore;
    private final UserDao userDao;
    private final Executor executor;
    private final SyncManager syncManager;

    public UserRepository(AppDatabase database, Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        this.userDao = database.userDao();
        this.executor = Executors.newSingleThreadExecutor();
        this.syncManager = SyncManager.getInstance(context);
    }

    /**
     * Get user - reads from Room, syncs from Firestore in background
     */
    public LiveData<User> getUser(String userId) {
        // Return Room LiveData immediately for instant UI
        LiveData<UserEntity> localData = userDao.getUserById(userId);

        // Fetch from Firestore in background and update Room
        fetchUserFromFirestore(userId);

        // Transform Entity to Model
        return Transformations.map(localData, this::convertToModel);
    }

    private void fetchUserFromFirestore(String userId) {
        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            // Update Room with fresh data
                            UserEntity entity = convertToEntity(user);
                            entity.setSynced(true);
                            executor.execute(() -> userDao.insertUser(entity));
                        }
                    }
                });
    }

    /**
     * Update user - writes to Room immediately, schedules Firestore sync
     */
    public void updateUser(User user) {
        UserEntity entity = convertToEntity(user);
        entity.setSynced(false);
        
        new Thread(() -> {
            try {
                userDao.updateUser(entity);
                syncManager.syncNow(user.getUserId());
            } catch (Exception e) {
                Log.e("UserRepository", "Failed to update user in Room", e);
                // Still attempt Firestore sync
                syncManager.syncNow(user.getUserId());
            }
        }).start();
    }

    /**
     * Update user display name
     */
    public void updateDisplayName(String userId, String displayName, OnCompleteListener listener) {
        executor.execute(() -> {
            UserEntity user = userDao.getUserByIdSync(userId);
            if (user != null) {
                user.setDisplayName(displayName);
                user.setUpdatedAt(System.currentTimeMillis());
                user.setSynced(false);
                userDao.updateUser(user);

                // Try to sync to Firestore
                firestore.collection(Constants.COLLECTION_USERS)
                        .document(userId)
                        .update("displayName", displayName, "updatedAt", Timestamp.now())
                        .addOnSuccessListener(aVoid -> {
                            user.setSynced(true);
                            userDao.updateUser(user);
                            if (listener != null) listener.onSuccess();
                        })
                        .addOnFailureListener(e -> {
                            // Still in Room, will sync later
                            syncManager.syncNow(userId);
                            if (listener != null) listener.onFailure(e);
                        });
            } else {
                if (listener != null) listener.onFailure(new Exception("User not found"));
            }
        });
    }

    /**
     * Update user stats in Firestore
     * Call this after completing a workout
     */
    public void updateUserStats(String userId, int totalWorkouts, int currentStreak,
                                double totalVolume, int activePrograms) {
        executor.execute(() -> {
            // Update Room first
            UserEntity user = userDao.getUserByIdSync(userId);
            if (user != null) {
                user.setTotalWorkouts(totalWorkouts);
                user.setCurrentStreak(currentStreak);
                user.setTotalVolumeLifted(totalVolume);
                user.setActivePrograms(activePrograms);
                user.setUpdatedAt(System.currentTimeMillis());
                user.setSynced(false);
                userDao.updateUser(user);

                // Try to sync to Firestore
                firestore.collection(Constants.COLLECTION_USERS)
                        .document(userId)
                        .update(
                                "totalWorkouts", totalWorkouts,
                                "currentStreak", currentStreak,
                                "totalVolumeLifted", totalVolume,
                                "activePrograms", activePrograms,
                                "updatedAt", Timestamp.now()
                        )
                        .addOnSuccessListener(aVoid -> {
                            user.setSynced(true);
                            userDao.updateUser(user);
                        })
                        .addOnFailureListener(e -> {
                            // Will sync later
                            syncManager.syncNow(userId);
                        });
            }
        });
    }

    /**
     * Create user - writes to Room first, then attempts Firestore
     */
    public void createUser(User user, OnCompleteListener listener) {
        UserEntity entity = convertToEntity(user);
        
        new Thread(() -> {
            try {
                userDao.insertUser(entity);
                
                // Write to Firestore
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(user.getUserId())
                    .set(user)
                    .addOnSuccessListener(aVoid -> {
                        entity.setSynced(true);
                        try {
                            userDao.updateUser(entity);
                            if (listener != null) listener.onSuccess();
                        } catch (Exception e) {
                            Log.e("UserRepository", "Failed to mark as synced", e);
                            if (listener != null) listener.onSuccess(); // Still success since Firestore worked
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UserRepository", "Firestore write failed", e);
                        // Still in Room, will sync later
                        syncManager.syncNow(user.getUserId());
                        if (listener != null) listener.onFailure(e);
                    });
            } catch (Exception e) {
                Log.e("UserRepository", "Failed to insert user in Room", e);
                if (listener != null) listener.onFailure(e);
            }
        }).start();
    }

    /**
     * Clear local cache (on logout)
     */
    public void clearCache() {
        executor.execute(() -> {
            userDao.deleteAllUsers();
        });
    }

    // Conversion helpers
    private UserEntity convertToEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setUserId(user.getUserId());
        entity.setEmail(user.getEmail());
        entity.setUsername(user.getUsername());
        entity.setDisplayName(user.getDisplayName());
        entity.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toDate().getTime() : 0);
        entity.setUpdatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toDate().getTime() : 0);
        entity.setTotalWorkouts(user.getTotalWorkouts());
        entity.setCurrentStreak(user.getCurrentStreak());
        entity.setTotalVolumeLifted(user.getTotalVolumeLifted());
        entity.setActivePrograms(user.getActivePrograms());
        return entity;
    }

    private User convertToModel(UserEntity entity) {
        if (entity == null) return null;
        User user = new User();
        user.setUserId(entity.getUserId());
        user.setEmail(entity.getEmail());
        user.setUsername(entity.getUsername());
        user.setDisplayName(entity.getDisplayName());
        user.setCreatedAt(new Timestamp(new java.util.Date(entity.getCreatedAt())));
        user.setUpdatedAt(new Timestamp(new java.util.Date(entity.getUpdatedAt())));
        user.setTotalWorkouts(entity.getTotalWorkouts());
        user.setCurrentStreak(entity.getCurrentStreak());
        user.setTotalVolumeLifted(entity.getTotalVolumeLifted());
        user.setActivePrograms(entity.getActivePrograms());
        return user;
    }

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }
}
