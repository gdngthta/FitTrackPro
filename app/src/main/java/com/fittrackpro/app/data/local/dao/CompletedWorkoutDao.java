package com.fittrackpro.app.data. local.dao;

import androidx. lifecycle.LiveData;
import androidx.room.*;
import com.fittrackpro.app.data.local.entity.CompletedWorkoutEntity;
import java. util.List;

@Dao
public interface CompletedWorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWorkout(CompletedWorkoutEntity workout);

    @Query("SELECT * FROM completed_workouts WHERE userId = :userId ORDER BY startTime DESC LIMIT:limit")
    LiveData<List<CompletedWorkoutEntity>> getRecentWorkouts(String userId, int limit);

    @Query("SELECT * FROM completed_workouts WHERE userId = :userId AND synced = 0")
    List<CompletedWorkoutEntity> getUnsyncedWorkouts(String userId);

    @Query("SELECT SUM(totalVolume) FROM completed_workouts WHERE userId = :userId")
    LiveData<Double> getTotalVolume(String userId);

    @Query("SELECT COUNT(*) FROM completed_workouts WHERE userId = :userId")
    LiveData<Integer> getTotalWorkoutCount(String userId);

    @Update
    void updateWorkout(CompletedWorkoutEntity workout);

    @Delete
    void deleteWorkout(CompletedWorkoutEntity workout);
}