package com.fittrackpro.app.util;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

/**
 * NotificationHelper provides utilities for vibration and sound notifications.
 *
 * Features:
 * - Device vibration (API-safe)
 * - Notification sound playback
 * - Pattern vibration support
 * - Handles API level differences
 */
public class NotificationHelper {

    /**
     * Vibrate device for specified duration.
     *
     * @param context Application context
     * @param durationMs Duration in milliseconds
     */
    public static void vibrateDevice(Context context, long durationMs) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Deprecated in API 26 but required for older devices
                vibrator.vibrate(durationMs);
            }
        }
    }

    /**
     * Vibrate device with a pattern.
     *
     * @param context Application context
     * @param pattern Pattern array: [wait, vibrate, wait, vibrate, ...]
     */
    public static void vibratePattern(Context context, long[] pattern) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
            } else {
                // Deprecated in API 26 but required for older devices
                vibrator.vibrate(pattern, -1);
            }
        }
    }

    /**
     * Play default notification sound.
     *
     * @param context Application context
     */
    public static void playNotificationSound(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
            if (ringtone != null) {
                ringtone.play();
            }
        } catch (Exception e) {
            // Silently fail if sound cannot be played
            e.printStackTrace();
        }
    }

    /**
     * Get Vibrator instance (handles API differences).
     *
     * @param context Application context
     * @return Vibrator instance or null
     */
    private static Vibrator getVibrator(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            return vibratorManager != null ? vibratorManager.getDefaultVibrator() : null;
        } else {
            return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }
}
