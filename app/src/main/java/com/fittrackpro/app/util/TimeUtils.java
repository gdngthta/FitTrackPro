package com.fittrackpro.app.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * TimeUtils handles time formatting and calculations.
 */
public class TimeUtils {

    /**
     * Format duration in seconds to HH:MM:SS
     */
    public static String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String. format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
        }
    }

    /**
     * Format timestamp to readable date
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale. getDefault());
        return sdf.format(date);
    }

    /**
     * Format timestamp to readable date and time
     */
    public static String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Format timestamp to relative time (e.g., "2 hours ago", "Yesterday")
     */
    public static String formatRelativeTime(Date date) {
        long diff = System.currentTimeMillis() - date.getTime();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit. MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        if (seconds < 60) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute(s) ago";
        } else if (hours < 24) {
            return hours + " hour(s) ago";
        } else if (days == 1) {
            return "Yesterday";
        } else if (days < 7) {
            return days + " day(s) ago";
        } else {
            return formatDate(date);
        }
    }

    /**
     * Calculate streak from list of workout dates
     * Returns number of consecutive days with workouts
     */
    public static int calculateStreak(java.util.List<Date> workoutDates) {
        if (workoutDates == null || workoutDates.isEmpty()) {
            return 0;
        }

        // Sort dates in descending order
        java.util. Collections.sort(workoutDates, (d1, d2) -> d2.compareTo(d1));

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar lastWorkout = Calendar.getInstance();
        lastWorkout.setTime(workoutDates.get(0));
        lastWorkout.set(Calendar. HOUR_OF_DAY, 0);
        lastWorkout. set(Calendar.MINUTE, 0);
        lastWorkout. set(Calendar.SECOND, 0);
        lastWorkout. set(Calendar.MILLISECOND, 0);

        // If last workout was not today or yesterday, streak is broken
        long daysBetween = TimeUnit.MILLISECONDS.toDays(
                today.getTimeInMillis() - lastWorkout.getTimeInMillis()
        );

        if (daysBetween > 1) {
            return 0;
        }

        // Count consecutive days
        int streak = 0;
        Calendar current = (Calendar) today.clone();

        for (Date date : workoutDates) {
            Calendar workoutCal = Calendar.getInstance();
            workoutCal.setTime(date);
            workoutCal.set(Calendar. HOUR_OF_DAY, 0);
            workoutCal.set(Calendar.MINUTE, 0);
            workoutCal.set(Calendar. SECOND, 0);
            workoutCal.set(Calendar. MILLISECOND, 0);

            if (workoutCal.equals(current)) {
                streak++;
                current.add(Calendar.DAY_OF_MONTH, -1);
            } else if (workoutCal.before(current)) {
                // Gap found, streak ends
                break;
            }
        }

        return streak;
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return today.get(Calendar. YEAR) == cal.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR);
    }
}