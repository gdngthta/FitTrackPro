package com.fittrackpro.app.ui.workout;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data.model.CompletedWorkout;
import com.fittrackpro.app.data.model.ProgramExercise;
import com.fittrackpro.app.data.model.WorkoutSet;
import com.fittrackpro.app.data.repository.WorkoutRepository;
import com.fittrackpro.app.util.Constants;
import com.fittrackpro.app.util.RestTimerManager;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ActiveWorkoutViewModel manages active workout session.
 *
 * Responsibilities:
 * - Track workout duration (chronometer)
 * - Manage exercise list
 * - Log sets (weight, reps, status)
 * - Calculate total volume
 * - Manage rest timer
 * - Save completed workout
 */
public class ActiveWorkoutViewModel extends AndroidViewModel {

    private final WorkoutRepository workoutRepository;
    private final RestTimerManager timerManager;

    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MutableLiveData<String> programId = new MutableLiveData<>();
    private final MutableLiveData<String> dayId = new MutableLiveData<>();
    private final MutableLiveData<String> workoutName = new MutableLiveData<>();

    private final MediatorLiveData<List<ProgramExercise>> exercises = new MediatorLiveData<>();
    private final MutableLiveData<List<WorkoutSet>> workoutSets = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Long> startTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isWorkoutActive = new MutableLiveData<>(false);

    private final MutableLiveData<Double> totalVolume = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> completedSets = new MutableLiveData<>(0);

    // Rest timer state
    private final MutableLiveData<Integer> restTimeRemaining = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> restTimerTotal = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> timerRunning = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> timerVisible = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> timerPaused = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> restCompleteNotification = new MutableLiveData<>(false);

    public ActiveWorkoutViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        this.workoutRepository = new WorkoutRepository(database);
        this.timerManager = new RestTimerManager();
    }

    public void initWorkout(String userId, String programId, String dayId, String workoutName) {
        this.userId.setValue(userId);
        this.programId.setValue(programId);
        this.dayId.setValue(dayId);
        this.workoutName. setValue(workoutName);

        // Load exercises for this workout day
        LiveData<List<ProgramExercise>> exercisesSource =
                workoutRepository.getExercisesForDay(programId, dayId);
        exercises.addSource(exercisesSource, exercises::setValue);
    }

    public void startWorkout() {
        startTime.setValue(System.currentTimeMillis());
        isWorkoutActive.setValue(true);
    }

    public void addSet(String exerciseName, double weight, int reps, String status) {
        List<WorkoutSet> currentSets = workoutSets. getValue();
        if (currentSets == null) {
            currentSets = new ArrayList<>();
        }

        WorkoutSet set = new WorkoutSet();
        set.setExerciseName(exerciseName);
        set.setSetNumber(currentSets.size() + 1);
        set.setWeight(weight);
        set.setReps(reps);
        set.setStatus(status);

        currentSets.add(set);
        workoutSets. setValue(currentSets);

        // Recalculate totals
        calculateTotals(currentSets);
    }

    public void updateSet(int index, double weight, int reps, String status) {
        List<WorkoutSet> currentSets = workoutSets.getValue();
        if (currentSets != null && index < currentSets. size()) {
            WorkoutSet set = currentSets. get(index);
            set.setWeight(weight);
            set.setReps(reps);
            set.setStatus(status);
            workoutSets.setValue(currentSets);

            // Recalculate totals
            calculateTotals(currentSets);
        }
    }

    private void calculateTotals(List<WorkoutSet> sets) {
        double volume = 0.0;
        int completed = 0;

        for (WorkoutSet set : sets) {
            if (set. getStatus().equals("completed") || set.getStatus().equals("modified")) {
                volume += set.getWeight() * set.getReps();
                completed++;
            }
        }

        totalVolume.setValue(volume);
        completedSets.setValue(completed);
    }

    public LiveData<Boolean> finishWorkout() {
        Long start = startTime.getValue();
        if (start == null) {
            return new MutableLiveData<>(false);
        }

        long endTimeMillis = System.currentTimeMillis();
        long durationSeconds = (endTimeMillis - start) / 1000;

        CompletedWorkout workout = new CompletedWorkout();
        workout.setUserId(userId.getValue());
        workout.setProgramId(programId.getValue());
        workout.setDayId(dayId.getValue());
        workout.setWorkoutName(workoutName.getValue());
        workout.setStartTime(new Timestamp(new Date(start)));
        workout.setEndTime(new Timestamp(new Date(endTimeMillis)));
        workout.setDurationSeconds(durationSeconds);
        workout.setTotalVolume(totalVolume.getValue() != null ? totalVolume.getValue() : 0.0);
        workout.setTotalSets(completedSets.getValue() != null ? completedSets.getValue() : 0);

        // Count unique exercises
        List<WorkoutSet> sets = workoutSets.getValue();
        if (sets != null) {
            long uniqueExercises = sets.stream()
                    .map(WorkoutSet::getExerciseName)
                    .distinct()
                    .count();
            workout.setTotalExercises((int) uniqueExercises);
        }

        isWorkoutActive.setValue(false);

        return workoutRepository.saveCompletedWorkout(userId.getValue(), workout,
                workoutSets.getValue() != null ? workoutSets. getValue() : new ArrayList<>());
    }

    // Getters
    public LiveData<List<ProgramExercise>> getExercises() {
        return exercises;
    }

    public LiveData<List<WorkoutSet>> getWorkoutSets() {
        return workoutSets;
    }

    public LiveData<Long> getStartTime() {
        return startTime;
    }

    public LiveData<Boolean> isWorkoutActive() {
        return isWorkoutActive;
    }

    public LiveData<Double> getTotalVolume() {
        return totalVolume;
    }

    public LiveData<Integer> getCompletedSets() {
        return completedSets;
    }

    // Rest timer methods

    /**
     * Start rest timer with specified duration.
     *
     * @param seconds Duration in seconds
     */
    public void startRestTimer(int seconds) {
        timerManager.startTimer(seconds, new RestTimerManager.TimerCallback() {
            @Override
            public void onTick(int remainingSeconds) {
                restTimeRemaining.postValue(remainingSeconds);
            }

            @Override
            public void onFinish() {
                restTimeRemaining.postValue(0);
                restTimerTotal.postValue(0);
                timerRunning.postValue(false);
                timerVisible.postValue(false);
                timerPaused.postValue(false);
                // Trigger notification
                restCompleteNotification.postValue(true);
            }
        });
        restTimerTotal.setValue(seconds);
        timerRunning.setValue(true);
        timerVisible.setValue(true);
        timerPaused.setValue(false);
    }

    /**
     * Toggle rest timer pause/resume.
     */
    public void toggleRestTimer() {
        if (timerManager.isPaused()) {
            timerManager.resumeTimer();
            timerPaused.setValue(false);
        } else if (timerManager.isRunning()) {
            timerManager.pauseTimer();
            timerPaused.setValue(true);
        }
    }

    /**
     * Skip rest timer.
     */
    public void skipRestTimer() {
        timerManager.skipTimer();
        restTimeRemaining.setValue(0);
        timerRunning.setValue(false);
        timerVisible.setValue(false);
        timerPaused.setValue(false);
    }

    /**
     * Cancel rest timer (cleanup).
     */
    public void cancelRestTimer() {
        timerManager.cancelTimer();
        restTimeRemaining.setValue(0);
        timerRunning.setValue(false);
        timerVisible.setValue(false);
        timerPaused.setValue(false);
    }

    /**
     * Reset rest complete notification flag.
     */
    public void resetRestCompleteNotification() {
        restCompleteNotification.setValue(false);
    }

    // Rest timer getters
    public LiveData<Integer> getRestTimeRemaining() {
        return restTimeRemaining;
    }

    public LiveData<Integer> getRestTimerTotal() {
        return restTimerTotal;
    }

    public LiveData<Boolean> isTimerRunning() {
        return timerRunning;
    }

    public LiveData<Boolean> isTimerVisible() {
        return timerVisible;
    }

    public LiveData<Boolean> isTimerPaused() {
        return timerPaused;
    }

    public LiveData<Boolean> getRestCompleteNotification() {
        return restCompleteNotification;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up timer when ViewModel is cleared
        timerManager.cancelTimer();
    }
}