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
 * - Save completed workout
 */
public class ActiveWorkoutViewModel extends AndroidViewModel {

    private final WorkoutRepository workoutRepository;

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

    public ActiveWorkoutViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        this.workoutRepository = new WorkoutRepository(database);
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

    public LiveData<String> getUserId() {
        return userId;
    }

    public LiveData<String> getProgramId() {
        return programId;
    }

    public LiveData<String> getDayId() {
        return dayId;
    }

    public LiveData<String> getWorkoutName() {
        return workoutName;
    }
}