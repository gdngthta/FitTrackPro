package com.fittrackpro.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.fittrackpro.app.data.local. entity.PersonalRecordEntity;
import java.util.List;

@Dao
public interface PersonalRecordDao {

    @Insert(onConflict = OnConflictStrategy. REPLACE)
    void insertRecord(PersonalRecordEntity record);

    @Query("SELECT * FROM personal_records WHERE userId =:userId ORDER BY achievedAt DESC")
    LiveData<List<PersonalRecordEntity>> getAllRecords(String userId);

    @Query("SELECT * FROM personal_records WHERE userId = :userId AND exerciseName = :exerciseName AND recordType = :recordType ORDER BY value DESC LIMIT 1")
    PersonalRecordEntity getBestRecord(String userId, String exerciseName, String recordType);

    @Delete
    void deleteRecord(PersonalRecordEntity record);
}