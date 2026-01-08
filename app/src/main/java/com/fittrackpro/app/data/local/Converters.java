package com.fittrackpro.app.data.local;

import androidx.room.TypeConverter;
import com.google.firebase.Timestamp;

public class Converters {

    @TypeConverter
    public static Long fromTimestamp(Timestamp timestamp) {
        return timestamp == null ?  null : timestamp.toDate().getTime();
    }

    @TypeConverter
    public static Timestamp toTimestamp(Long value) {
        return value == null ? null : new Timestamp(new java.util.Date(value));
    }
}