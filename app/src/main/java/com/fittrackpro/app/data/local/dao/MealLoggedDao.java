package com.fittrackpro.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.fittrackpro.app.data.local.entity.MealLoggedEntity;
import java.util.List;

@Dao
public interface MealLoggedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeal(MealLoggedEntity meal);

    @Query("SELECT * FROM meals_logged WHERE userId = :userId AND DATE(loggedAt/1000, 'unixepoch', 'localtime') = DATE(:date/1000, 'unixepoch', 'localtime') ORDER BY loggedAt DESC")
    LiveData<List<MealLoggedEntity>> getMealsForDate(String userId, long date);

    @Query("SELECT * FROM meals_logged WHERE userId = :userId AND synced = 0")
    List<MealLoggedEntity> getUnsyncedMeals(String userId);

    @Query("SELECT SUM(calories) FROM meals_logged WHERE userId = :userId AND DATE(loggedAt/1000, 'unixepoch', 'localtime') = DATE(:date/1000, 'unixepoch', 'localtime')")
    LiveData<Double> getTotalCaloriesForDate(String userId, long date);

    @Update
    void updateMeal(MealLoggedEntity meal);

    @Delete
    void deleteMeal(MealLoggedEntity meal);
}
