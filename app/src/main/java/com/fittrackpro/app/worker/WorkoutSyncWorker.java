package com.fittrackpro.app.worker;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data.local.dao.CompletedWorkoutDao;
import com.fittrackpro.app.data.local.entity.CompletedWorkoutEntity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * WorkoutSyncWorker uploads unsynced workouts to Firestore.
 * Triggered when device regains internet connectivity.
 */
public class WorkoutSyncWorker extends Worker {

    private final FirebaseFirestore firestore;
    private final CompletedWorkoutDao workoutDao;

    public WorkoutSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.firestore = FirebaseFirestore.getInstance();
        AppDatabase database = AppDatabase.getInstance(context);
        this.workoutDao = database.completedWorkoutDao();
    }

    @NonNull
    @Override
    public Result doWork() {
        // FIX: Null-safe user ID retrieval
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.w("WorkoutSyncWorker", "User not logged in, cannot sync");
            return Result.failure();
        }
        
        String userId = currentUser.getUid();

        // Get unsynced workouts
        List<CompletedWorkoutEntity> unsyncedWorkouts = workoutDao.getUnsyncedWorkouts(userId);
        
        if (unsyncedWorkouts.isEmpty()) {
            return Result.success();
        }

        boolean allSuccess = true;
        
        for (CompletedWorkoutEntity workout : unsyncedWorkouts) {
            try {
                // Upload to Firestore (use Tasks.await for synchronous execution in Worker)
                Tasks.await(firestore.collection("completedWorkouts")
                        .document(workout.getWorkoutId())
                        .set(convertEntityToMap(workout)));
                
                // Mark as synced
                workout.setSynced(true);
                workoutDao.updateWorkout(workout);
                
            } catch (Exception e) {
                Log.e("WorkoutSyncWorker", "Failed to sync workout: " + workout.getWorkoutId(), e);
                allSuccess = false;
            }
        }

        return allSuccess ? Result.success() : Result.retry();
    }

    private java.util.Map<String, Object> convertEntityToMap(CompletedWorkoutEntity entity) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("workoutId", entity.getWorkoutId());
        map.put("userId", entity.getUserId());
        map.put("programId", entity.getProgramId());
        map.put("dayId", entity.getDayId());
        map.put("workoutName", entity.getWorkoutName());
        map.put("startTime", new com.google.firebase.Timestamp(new java.util.Date(entity.getStartTime())));
        map.put("endTime", new com.google.firebase.Timestamp(new java.util.Date(entity.getEndTime())));
        map.put("durationSeconds", entity.getDurationSeconds());
        map.put("totalVolume", entity.getTotalVolume());
        map.put("totalSets", entity.getTotalSets());
        map.put("totalExercises", entity.getTotalExercises());
        map.put("synced", true);
        return map;
    }
}