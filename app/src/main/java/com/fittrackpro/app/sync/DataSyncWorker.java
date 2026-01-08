package com.fittrackpro.app.sync;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data.local.dao.*;
import com.fittrackpro.app.data.local.entity.*;
import com.fittrackpro.app.util.Constants;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataSyncWorker handles background synchronization of unsynced data to Firestore.
 * Processes all data types: users, workouts, PRs, meals, and programs.
 */
public class DataSyncWorker extends Worker {

    private static final String TAG = "DataSyncWorker";
    private static final int MAX_SYNC_ATTEMPTS = 3;

    private final AppDatabase db;
    private final FirebaseFirestore firestore;

    public DataSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.db = AppDatabase.getInstance(context);
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        String userId = getInputData().getString("userId");
        if (userId == null) {
            Log.e(TAG, "No userId provided");
            return Result.failure();
        }

        try {
            syncAllData(userId);
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Sync failed", e);
            if (getRunAttemptCount() < MAX_SYNC_ATTEMPTS) {
                return Result.retry();
            }
            return Result.failure();
        }
    }

    private void syncAllData(String userId) {
        Log.d(TAG, "Starting sync for user: " + userId);

        // Sync completed workouts
        syncCompletedWorkouts(userId);

        // Sync user data
        syncUserData();

        // Sync personal records
        syncPersonalRecords(userId);

        // Sync meal logs
        syncMealLogs(userId);

        // Sync custom programs
        syncCustomPrograms(userId);

        Log.d(TAG, "Sync completed for user: " + userId);
    }

    private void syncCompletedWorkouts(String userId) {
        CompletedWorkoutDao dao = db.completedWorkoutDao();
        List<CompletedWorkoutEntity> unsyncedWorkouts = dao.getUnsyncedWorkouts(userId);

        Log.d(TAG, "Syncing " + unsyncedWorkouts.size() + " completed workouts");

        for (CompletedWorkoutEntity workout : unsyncedWorkouts) {
            if (workout.getSyncAttempts() >= MAX_SYNC_ATTEMPTS) {
                Log.w(TAG, "Skipping workout after " + MAX_SYNC_ATTEMPTS + " attempts: " + workout.getWorkoutId());
                continue;
            }

            try {
                Map<String, Object> data = new HashMap<>();
                data.put("workoutId", workout.getWorkoutId());
                data.put("userId", workout.getUserId());
                data.put("programId", workout.getProgramId());
                data.put("dayId", workout.getDayId());
                data.put("workoutName", workout.getWorkoutName());
                data.put("startTime", new Timestamp(new Date(workout.getStartTime())));
                data.put("endTime", new Timestamp(new Date(workout.getEndTime())));
                data.put("durationSeconds", workout.getDurationSeconds());
                data.put("totalVolume", workout.getTotalVolume());
                data.put("totalSets", workout.getTotalSets());
                data.put("totalExercises", workout.getTotalExercises());
                data.put("synced", true);

                Tasks.await(firestore.collection(Constants.COLLECTION_COMPLETED_WORKOUTS)
                        .document(workout.getWorkoutId())
                        .set(data));

                // Mark as synced in Room
                workout.setSynced(true);
                workout.setLastSyncAttempt(System.currentTimeMillis());
                workout.setSyncError(null);
                dao.updateWorkout(workout);

                Log.d(TAG, "Successfully synced workout: " + workout.getWorkoutId());

            } catch (Exception e) {
                Log.e(TAG, "Failed to sync workout: " + workout.getWorkoutId(), e);
                workout.setSyncAttempts(workout.getSyncAttempts() + 1);
                workout.setLastSyncAttempt(System.currentTimeMillis());
                workout.setSyncError(e.getMessage());
                dao.updateWorkout(workout);
            }
        }
    }

    private void syncUserData() {
        UserDao dao = db.userDao();
        List<UserEntity> unsyncedUsers = dao.getUnsyncedUsers();

        Log.d(TAG, "Syncing " + unsyncedUsers.size() + " user records");

        for (UserEntity user : unsyncedUsers) {
            if (user.getSyncAttempts() >= MAX_SYNC_ATTEMPTS) {
                Log.w(TAG, "Skipping user after " + MAX_SYNC_ATTEMPTS + " attempts: " + user.getUserId());
                continue;
            }

            try {
                Map<String, Object> data = new HashMap<>();
                data.put("userId", user.getUserId());
                data.put("email", user.getEmail());
                data.put("username", user.getUsername());
                data.put("displayName", user.getDisplayName());
                data.put("createdAt", new Timestamp(new Date(user.getCreatedAt())));
                data.put("updatedAt", new Timestamp(new Date(user.getUpdatedAt())));
                data.put("totalWorkouts", user.getTotalWorkouts());
                data.put("currentStreak", user.getCurrentStreak());
                data.put("totalVolumeLifted", user.getTotalVolumeLifted());
                data.put("activePrograms", user.getActivePrograms());

                Tasks.await(firestore.collection(Constants.COLLECTION_USERS)
                        .document(user.getUserId())
                        .set(data));

                // Mark as synced
                user.setSynced(true);
                user.setLastSyncAttempt(System.currentTimeMillis());
                user.setSyncError(null);
                dao.updateUser(user);

                Log.d(TAG, "Successfully synced user: " + user.getUserId());

            } catch (Exception e) {
                Log.e(TAG, "Failed to sync user: " + user.getUserId(), e);
                user.setSyncAttempts(user.getSyncAttempts() + 1);
                user.setLastSyncAttempt(System.currentTimeMillis());
                user.setSyncError(e.getMessage());
                dao.updateUser(user);
            }
        }
    }

    private void syncPersonalRecords(String userId) {
        PersonalRecordDao dao = db.personalRecordDao();
        List<PersonalRecordEntity> unsyncedRecords = dao.getUnsyncedRecords();

        Log.d(TAG, "Syncing " + unsyncedRecords.size() + " personal records");

        for (PersonalRecordEntity record : unsyncedRecords) {
            if (record.getSyncAttempts() >= MAX_SYNC_ATTEMPTS) {
                Log.w(TAG, "Skipping PR after " + MAX_SYNC_ATTEMPTS + " attempts: " + record.getRecordId());
                continue;
            }

            try {
                Map<String, Object> data = new HashMap<>();
                data.put("recordId", record.getRecordId());
                data.put("userId", record.getUserId());
                data.put("exerciseName", record.getExerciseName());
                data.put("recordType", record.getRecordType());
                data.put("value", record.getValue());
                data.put("reps", record.getReps());
                data.put("achievedAt", new Timestamp(new Date(record.getAchievedAt())));

                Tasks.await(firestore.collection(Constants.COLLECTION_PERSONAL_RECORDS)
                        .document(record.getRecordId())
                        .set(data));

                // Mark as synced
                record.setSynced(true);
                record.setLastSyncAttempt(System.currentTimeMillis());
                record.setSyncError(null);
                dao.insertRecord(record); // Using insert with REPLACE conflict strategy

                Log.d(TAG, "Successfully synced PR: " + record.getRecordId());

            } catch (Exception e) {
                Log.e(TAG, "Failed to sync PR: " + record.getRecordId(), e);
                record.setSyncAttempts(record.getSyncAttempts() + 1);
                record.setLastSyncAttempt(System.currentTimeMillis());
                record.setSyncError(e.getMessage());
                dao.insertRecord(record); // Using insert with REPLACE conflict strategy
            }
        }
    }

    private void syncMealLogs(String userId) {
        MealLoggedDao dao = db.mealLoggedDao();
        List<MealLoggedEntity> unsyncedMeals = dao.getUnsyncedMeals(userId);

        Log.d(TAG, "Syncing " + unsyncedMeals.size() + " meal logs");

        for (MealLoggedEntity meal : unsyncedMeals) {
            if (meal.getSyncAttempts() >= MAX_SYNC_ATTEMPTS) {
                Log.w(TAG, "Skipping meal after " + MAX_SYNC_ATTEMPTS + " attempts: " + meal.getLogId());
                continue;
            }

            try {
                Map<String, Object> data = new HashMap<>();
                data.put("logId", meal.getLogId());
                data.put("userId", meal.getUserId());
                data.put("foodId", meal.getFoodId());
                data.put("foodName", meal.getFoodName());
                data.put("mealType", meal.getMealType());
                data.put("portionMultiplier", meal.getPortionMultiplier());
                data.put("calories", meal.getCalories());
                data.put("protein", meal.getProtein());
                data.put("carbs", meal.getCarbs());
                data.put("fats", meal.getFats());
                data.put("loggedAt", new Timestamp(new Date(meal.getLoggedAt())));

                Tasks.await(firestore.collection(Constants.COLLECTION_MEALS_LOGGED)
                        .document(meal.getLogId())
                        .set(data));

                // Mark as synced
                meal.setSynced(true);
                meal.setLastSyncAttempt(System.currentTimeMillis());
                meal.setSyncError(null);
                dao.updateMeal(meal);

                Log.d(TAG, "Successfully synced meal: " + meal.getLogId());

            } catch (Exception e) {
                Log.e(TAG, "Failed to sync meal: " + meal.getLogId(), e);
                meal.setSyncAttempts(meal.getSyncAttempts() + 1);
                meal.setLastSyncAttempt(System.currentTimeMillis());
                meal.setSyncError(e.getMessage());
                dao.updateMeal(meal);
            }
        }
    }

    private void syncCustomPrograms(String userId) {
        WorkoutProgramDao dao = db.workoutProgramDao();
        List<WorkoutProgramEntity> unsyncedPrograms = dao.getUnsyncedPrograms();

        Log.d(TAG, "Syncing " + unsyncedPrograms.size() + " custom programs");

        for (WorkoutProgramEntity program : unsyncedPrograms) {
            // Skip preset programs (they shouldn't need syncing)
            if (program.isPreset()) {
                continue;
            }

            if (program.getSyncAttempts() >= MAX_SYNC_ATTEMPTS) {
                Log.w(TAG, "Skipping program after " + MAX_SYNC_ATTEMPTS + " attempts: " + program.getProgramId());
                continue;
            }

            try {
                Map<String, Object> data = new HashMap<>();
                data.put("programId", program.getProgramId());
                data.put("userId", program.getUserId());
                data.put("programName", program.getProgramName());
                data.put("description", program.getDescription());
                data.put("difficulty", program.getDifficulty());
                data.put("durationWeeks", program.getDurationWeeks());
                data.put("daysPerWeek", program.getDaysPerWeek());
                data.put("isPreset", program.isPreset());
                data.put("isActive", program.isActive());
                data.put("originalPresetId", program.getOriginalPresetId());
                data.put("createdAt", new Timestamp(new Date(program.getCreatedAt())));
                data.put("updatedAt", new Timestamp(new Date(program.getUpdatedAt())));

                Tasks.await(firestore.collection(Constants.COLLECTION_WORKOUT_PROGRAMS)
                        .document(program.getProgramId())
                        .set(data));

                // Mark as synced
                program.setSynced(true);
                program.setLastSyncAttempt(System.currentTimeMillis());
                program.setSyncError(null);
                dao.updateProgram(program);

                Log.d(TAG, "Successfully synced program: " + program.getProgramId());

            } catch (Exception e) {
                Log.e(TAG, "Failed to sync program: " + program.getProgramId(), e);
                program.setSyncAttempts(program.getSyncAttempts() + 1);
                program.setLastSyncAttempt(System.currentTimeMillis());
                program.setSyncError(e.getMessage());
                dao.updateProgram(program);
            }
        }
    }
}
