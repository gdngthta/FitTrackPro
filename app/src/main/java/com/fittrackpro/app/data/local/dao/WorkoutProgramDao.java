package com.fittrackpro.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx. room.*;
import com.fittrackpro.app.data. local.entity.WorkoutProgramEntity;
import java.util.List;

@Dao
public interface WorkoutProgramDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProgram(WorkoutProgramEntity program);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPrograms(List<WorkoutProgramEntity> programs);

    @Query("SELECT * FROM workout_programs WHERE userId = :userId AND isActive = 1")
    LiveData<List<WorkoutProgramEntity>> getActivePrograms(String userId);

    @Query("SELECT * FROM workout_programs WHERE isPreset = 1")
    LiveData<List<WorkoutProgramEntity>> getPresetPrograms();

    @Query("SELECT * FROM workout_programs WHERE programId = :programId")
    LiveData<WorkoutProgramEntity> getProgramById(String programId);

    @Update
    void updateProgram(WorkoutProgramEntity program);

    @Delete
    void deleteProgram(WorkoutProgramEntity program);

    @Query("DELETE FROM workout_programs WHERE userId = :userId")
    void deleteUserPrograms(String userId);

    @Query("SELECT * FROM workout_programs WHERE synced = 0")
    List<WorkoutProgramEntity> getUnsyncedPrograms();
}