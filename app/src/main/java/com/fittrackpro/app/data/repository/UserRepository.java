package com.fittrackpro.app.data. repository;

import androidx.lifecycle. LiveData;
import androidx.lifecycle. MutableLiveData;
import com.fittrackpro. app.data.local.AppDatabase;
import com.fittrackpro.app.data.local.dao.UserDao;
import com.fittrackpro. app.data.local.entity. UserEntity;
import com.fittrackpro.app.data.model.User;
import com.google.firebase.Timestamp;
import com.google.firebase. firestore.FirebaseFirestore;

import java.util.concurrent. Executor;
import java.util. concurrent.Executors;

/**
 * UserRepository manages user data sync between Firestore and Room.
 *
 * Key responsibilities:
 * - Fetch user data from Firestore
 * - Cache user data in Room
 * - Update user stats (workouts, streak, volume, active programs)
 */
public class UserRepository {

    private final FirebaseFirestore firestore;
    private final UserDao userDao;
    private final Executor executor;

    public UserRepository(AppDatabase database) {
        this.firestore = FirebaseFirestore.getInstance();
        this.userDao = database.userDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Fetch user data from Firestore and cache in Room
     */
    public LiveData<User> getUser(String userId) {
        MutableLiveData<User> result = new MutableLiveData<>();

        // First, try to get from Room cache
        executor.execute(() -> {
            UserEntity cachedUser = userDao.getUserByIdSync(userId);
            if (cachedUser != null) {
                result.postValue(entityToModel(cachedUser));
            }
        });

        // Then fetch from Firestore
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot. toObject(User.class);
                    if (user != null) {
                        result.setValue(user);

                        // Cache in Room
                        executor. execute(() -> {
                            userDao.insertUser(modelToEntity(user));
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // If Firestore fails, rely on cached data
                });

        return result;
    }

    /**
     * Update user stats in Firestore
     * Call this after completing a workout
     */
    public void updateUserStats(String userId, int totalWorkouts, int currentStreak,
                                double totalVolume, int activePrograms) {
        firestore. collection("users")
                .document(userId)
                .update(
                        "totalWorkouts", totalWorkouts,
                        "currentStreak", currentStreak,
                        "totalVolumeLifted", totalVolume,
                        "activePrograms", activePrograms,
                        "updatedAt", Timestamp.now()
                )
                .addOnSuccessListener(aVoid -> {
                    // Update Room cache
                    executor.execute(() -> {
                        UserEntity user = userDao.getUserByIdSync(userId);
                        if (user != null) {
                            user.setTotalWorkouts(totalWorkouts);
                            user.setCurrentStreak(currentStreak);
                            user.setTotalVolumeLifted(totalVolume);
                            user.setActivePrograms(activePrograms);
                            user.setUpdatedAt(System.currentTimeMillis());
                            userDao.updateUser(user);
                        }
                    });
                });
    }

    /**
     * Update user display name
     */
    public LiveData<Boolean> updateDisplayName(String userId, String displayName) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        firestore. collection("users")
                .document(userId)
                .update("displayName", displayName, "updatedAt", Timestamp.now())
                .addOnSuccessListener(aVoid -> {
                    result.setValue(true);

                    // Update Room cache
                    executor.execute(() -> {
                        UserEntity user = userDao. getUserByIdSync(userId);
                        if (user != null) {
                            user.setDisplayName(displayName);
                            user.setUpdatedAt(System.currentTimeMillis());
                            userDao.updateUser(user);
                        }
                    });
                })
                .addOnFailureListener(e -> result. setValue(false));

        return result;
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
    private UserEntity modelToEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setUserId(user.getUserId());
        entity.setEmail(user. getEmail());
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

    private User entityToModel(UserEntity entity) {
        User user = new User();
        user.setUserId(entity.getUserId());
        user.setEmail(entity.getEmail());
        user.setUsername(entity.getUsername());
        user.setDisplayName(entity.getDisplayName());
        user.setCreatedAt(new Timestamp(new java.util.Date(entity. getCreatedAt())));
        user.setUpdatedAt(new Timestamp(new java.util. Date(entity.getUpdatedAt())));
        user.setTotalWorkouts(entity.getTotalWorkouts());
        user.setCurrentStreak(entity.getCurrentStreak());
        user.setTotalVolumeLifted(entity.getTotalVolumeLifted());
        user.setActivePrograms(entity.getActivePrograms());
        return user;
    }
}