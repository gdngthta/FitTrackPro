package com.fittrackpro.app. data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.fittrackpro.app.data.local.entity.FoodEntity;
import java.util.List;

@Dao
public interface FoodDao {

    @Insert(onConflict = OnConflictStrategy. REPLACE)
    void insertFood(FoodEntity food);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFoods(List<FoodEntity> foods);

    @Query("SELECT * FROM foods WHERE foodName LIKE '%' || :query || '%' OR brand LIKE '%' ||:query || '%' ORDER BY isVerified DESC, foodName ASC")
    LiveData<List<FoodEntity>> searchFoods(String query);

    @Query("SELECT * FROM foods WHERE foodId = :foodId")
    LiveData<FoodEntity> getFoodById(String foodId);

    @Delete
    void deleteFood(FoodEntity food);
}