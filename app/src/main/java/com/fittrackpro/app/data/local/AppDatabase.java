package com.fittrackpro. app.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.fittrackpro.app.data.local.dao.*;
import com.fittrackpro.app.data.local. entity.*;

@Database(
        entities = {
                UserEntity.class,
                WorkoutProgramEntity.class,
                CompletedWorkoutEntity.class,
                PersonalRecordEntity.class,
                FoodEntity.class,
                MealLoggedEntity.class
        },
        version = 2,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract WorkoutProgramDao workoutProgramDao();
    public abstract CompletedWorkoutDao completedWorkoutDao();
    public abstract PersonalRecordDao personalRecordDao();
    public abstract FoodDao foodDao();
    public abstract MealLoggedDao mealLoggedDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "fittrack_database"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}