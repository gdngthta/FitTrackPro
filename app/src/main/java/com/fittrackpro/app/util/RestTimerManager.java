package com.fittrackpro.app.util;

import android.os.CountDownTimer;

/**
 * RestTimerManager handles countdown timer for rest periods between sets.
 *
 * Features:
 * - CountDownTimer based implementation
 * - Pause/resume functionality
 * - Skip/cancel functionality
 * - Thread-safe operations
 * - Callbacks for tick and finish events
 */
public class RestTimerManager {

    private CountDownTimer countDownTimer;
    private TimerCallback callback;
    private boolean isRunning;
    private boolean isPaused;
    private int remainingSeconds;
    private int totalSeconds;

    /**
     * Callback interface for timer events.
     */
    public interface TimerCallback {
        void onTick(int remainingSeconds);
        void onFinish();
    }

    /**
     * Start timer with specified duration.
     *
     * @param durationSeconds Duration in seconds
     * @param callback Callback for timer events
     */
    public synchronized void startTimer(int durationSeconds, TimerCallback callback) {
        cancelTimer(); // Cancel any existing timer

        this.totalSeconds = durationSeconds;
        this.remainingSeconds = durationSeconds;
        this.callback = callback;
        this.isPaused = false;
        this.isRunning = true;

        startCountDown(durationSeconds);
    }

    private void startCountDown(int durationSeconds) {
        countDownTimer = new CountDownTimer(durationSeconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingSeconds = (int) (millisUntilFinished / 1000);
                if (callback != null) {
                    callback.onTick(remainingSeconds);
                }
            }

            @Override
            public void onFinish() {
                remainingSeconds = 0;
                isRunning = false;
                isPaused = false;
                if (callback != null) {
                    callback.onFinish();
                }
            }
        };
        countDownTimer.start();
    }

    /**
     * Pause the timer.
     */
    public synchronized void pauseTimer() {
        if (isRunning && !isPaused && countDownTimer != null) {
            countDownTimer.cancel();
            isPaused = true;
        }
    }

    /**
     * Resume the timer.
     */
    public synchronized void resumeTimer() {
        if (isRunning && isPaused) {
            isPaused = false;
            startCountDown(remainingSeconds);
        }
    }

    /**
     * Cancel the timer completely.
     */
    public synchronized void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isRunning = false;
        isPaused = false;
        remainingSeconds = 0;
        // Nullify callback after all operations complete
        TimerCallback tempCallback = callback;
        callback = null;
    }

    /**
     * Skip the timer (same as cancel but triggers finish callback).
     */
    public synchronized void skipTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isRunning = false;
        isPaused = false;
        remainingSeconds = 0;

        if (callback != null) {
            callback.onFinish();
        }
    }

    /**
     * Check if timer is currently running.
     *
     * @return true if running, false otherwise
     */
    public synchronized boolean isRunning() {
        return isRunning;
    }

    /**
     * Check if timer is paused.
     *
     * @return true if paused, false otherwise
     */
    public synchronized boolean isPaused() {
        return isPaused;
    }

    /**
     * Get remaining seconds.
     *
     * @return Remaining seconds
     */
    public synchronized int getRemainingSeconds() {
        return remainingSeconds;
    }

    /**
     * Get total seconds of current timer.
     *
     * @return Total seconds
     */
    public synchronized int getTotalSeconds() {
        return totalSeconds;
    }
}
