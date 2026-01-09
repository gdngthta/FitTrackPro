package com.fittrackpro. app.ui.workout;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.fittrackpro.app.data. local.AppDatabase;
import com.fittrackpro.app.data.model.WorkoutProgram;
import com.fittrackpro.app.data. repository.WorkoutRepository;

import java.util.List;

/**
 * WorkoutHubViewModel manages workout programs display.
 *
 * Shows:
 * - Recommended (preset) programs
 * - User's active programs
 */
public class WorkoutHubViewModel extends AndroidViewModel {

    private final WorkoutRepository workoutRepository;

    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MediatorLiveData<List<WorkoutProgram>> presetPrograms = new MediatorLiveData<>();
    private final MediatorLiveData<List<WorkoutProgram>> userPrograms = new MediatorLiveData<>();

    public WorkoutHubViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        this.workoutRepository = new WorkoutRepository(database);
    }

    public void setUserId(String userId) {
        this.userId.setValue(userId);
        loadPrograms(userId);
    }

    private void loadPrograms(String userId) {
        // Load preset programs
        LiveData<List<WorkoutProgram>> presetSource = workoutRepository.getPresetPrograms();
        presetPrograms.addSource(presetSource, presetPrograms::setValue);

        // Load user's active programs
        LiveData<List<WorkoutProgram>> userSource = workoutRepository.getUserActivePrograms(userId);
        userPrograms.addSource(userSource, userPrograms::setValue);
    }

    public LiveData<List<WorkoutProgram>> getPresetPrograms() {
        return presetPrograms;
    }

    public LiveData<List<WorkoutProgram>> getUserPrograms() {
        return userPrograms;
    }

    public LiveData<String> duplicatePreset(String presetId) {
        String currentUserId = userId.getValue();
        if (currentUserId != null) {
            return workoutRepository.duplicatePresetProgram(presetId, currentUserId);
        }
        return new MutableLiveData<>(null);
    }

    public LiveData<String> createCustomProgram(String programName, String description,
                                                String difficulty, int durationWeeks, int daysPerWeek) {
        String currentUserId = userId.getValue();
        if (currentUserId != null) {
            return workoutRepository.createCustomProgram(
                    currentUserId, programName, description, difficulty, durationWeeks, daysPerWeek
            );
        }
        return new MutableLiveData<>(null);
    }

    /**
     * Start a program by activating it for the current user
     * Returns LiveData<Boolean> indicating success/failure
     */
    public LiveData<Boolean> startProgram(WorkoutProgram program) {
        String currentUserId = userId.getValue();
        if (currentUserId != null) {
            if (program.isPreset()) {
                // Add preset program to user (duplicates and activates)
                return workoutRepository.addPresetProgramToUser(program.getProgramId(), currentUserId);
            } else {
                // Activate user's existing program
                workoutRepository.activateProgram(program.getProgramId(), currentUserId);
                MutableLiveData<Boolean> result = new MutableLiveData<>(true);
                return result;
            }
        }
        return new MutableLiveData<>(false);
    }

    /**
     * Initialize preset programs with correct difficulties
     */
    public void initializePresetPrograms() {
        workoutRepository.initializePresetPrograms();
    }
}