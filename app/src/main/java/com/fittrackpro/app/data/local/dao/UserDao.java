package com.fittrackpro.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx. room.*;
import com.fittrackpro.app.data.local. entity. UserEntity;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    @Query("SELECT * FROM users WHERE userId = :userId")
    LiveData<UserEntity> getUserById(String userId);

    @Query("SELECT * FROM users WHERE userId = :userId")
    UserEntity getUserByIdSync(String userId);

    @Update
    void updateUser(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);

    @Query("DELETE FROM users")
    void deleteAllUsers();
}