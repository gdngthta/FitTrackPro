package com.fittrackpro.app.ui.workout;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fittrackpro.app.data.model.CompletedWorkout;
import com.fittrackpro.app.data.model.PersonalRecord;
import com.fittrackpro.app.data.model.WorkoutSet;
import com.fittrackpro.app.util.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * WorkoutSummaryViewModel manages post-workout summary data and PR detection.
 *
 * Responsibilities:
 * - Accept workout data from ActiveWorkoutFragment
 * - Calculate final statistics (total volume, duration, set count)
 * - Detect personal records by comparing against Firestore records
 * - Save completed workout to Firestore
 * - Update user's total stats (totalWorkouts++, totalVolume+=)
 * - Save new PRs to personalRecords collection
 * - Provide LiveData for UI observation
 */
public class WorkoutSummaryViewModel extends AndroidViewModel {

    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    private final Executor executor;

    private final MutableLiveData<CompletedWorkout> workoutData = new MutableLiveData<>();
    private final MutableLiveData<List<WorkoutSet>> exerciseSets = new MutableLiveData<>();
    private final MutableLiveData<List<PersonalRecord>> newPersonalRecords = new MutableLiveData<>();
    private final MutableLiveData<Boolean> savingComplete = new MutableLiveData<>();
    private final MutableLiveData<Boolean> prDetectionComplete = new MutableLiveData<>(false);

    public WorkoutSummaryViewModel(@NonNull Application application) {
        super(application);
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Set workout data from ActiveWorkoutFragment
     */
    public void setWorkoutData(String userId, String programId, String dayId, String workoutName,
                              long startTime, long endTime, List<WorkoutSet> sets) {
        // Calculate statistics
        long durationSeconds = (endTime - startTime) / 1000;
        double totalVolume = 0.0;
        int completedSetsCount = 0;

        // Calculate totals
        for (WorkoutSet set : sets) {
            if (set.getStatus().equals(Constants.SET_STATUS_COMPLETED) ||
                set.getStatus().equals(Constants.SET_STATUS_MODIFIED)) {
                totalVolume += set.getWeight() * set.getReps();
                completedSetsCount++;
            }
        }

        // Count unique exercises
        Map<String, Boolean> uniqueExercises = new HashMap<>();
        for (WorkoutSet set : sets) {
            uniqueExercises.put(set.getExerciseName(), true);
        }

        // Create CompletedWorkout object
        CompletedWorkout workout = new CompletedWorkout();
        workout.setUserId(userId);
        workout.setProgramId(programId);
        workout.setDayId(dayId);
        workout.setWorkoutName(workoutName);
        workout.setStartTime(new Timestamp(new java.util.Date(startTime)));
        workout.setEndTime(new Timestamp(new java.util.Date(endTime)));
        workout.setDurationSeconds(durationSeconds);
        workout.setTotalVolume(totalVolume);
        workout.setTotalSets(completedSetsCount);
        workout.setTotalExercises(uniqueExercises.size());
        workout.setSynced(false);

        workoutData.setValue(workout);
        exerciseSets.setValue(sets);

        // Detect PRs
        detectPersonalRecords(userId, sets);
    }

    /**
     * Detect personal records by comparing workout sets with existing PRs
     */
    private void detectPersonalRecords(String userId, List<WorkoutSet> sets) {
        executor.execute(() -> {
            List<PersonalRecord> detectedPRs = new ArrayList<>();
            
            // Group sets by exercise
            Map<String, List<WorkoutSet>> setsByExercise = groupSetsByExercise(sets);

            for (Map.Entry<String, List<WorkoutSet>> entry : setsByExercise.entrySet()) {
                String exerciseName = entry.getKey();
                List<WorkoutSet> exerciseSets = entry.getValue();

                // Check existing PRs from Firestore
                checkAndAddPRs(userId, exerciseName, exerciseSets, detectedPRs);
            }

            // Update LiveData on main thread
            newPersonalRecords.postValue(detectedPRs);
            prDetectionComplete.postValue(true);
        });
    }

    /**
     * Group workout sets by exercise name
     */
    private Map<String, List<WorkoutSet>> groupSetsByExercise(List<WorkoutSet> sets) {
        Map<String, List<WorkoutSet>> grouped = new HashMap<>();
        
        for (WorkoutSet set : sets) {
            if (!set.getStatus().equals(Constants.SET_STATUS_COMPLETED) &&
                !set.getStatus().equals(Constants.SET_STATUS_MODIFIED)) {
                continue;
            }

            String exerciseName = set.getExerciseName();
            if (!grouped.containsKey(exerciseName)) {
                grouped.put(exerciseName, new ArrayList<>());
            }
            grouped.get(exerciseName).add(set);
        }

        return grouped;
    }

    /**
     * Check for new PRs in exercise sets
     */
    private void checkAndAddPRs(String userId, String exerciseName, 
                                List<WorkoutSet> sets, List<PersonalRecord> detectedPRs) {
        try {
            // Get existing PRs for this exercise (synchronous for executor thread)
            Map<String, PersonalRecord> existingPRs = getExistingPRsSync(userId, exerciseName);

            // Find max weight
            double maxWeight = 0;
            int repsAtMaxWeight = 0;
            for (WorkoutSet set : sets) {
                if (set.getWeight() > maxWeight) {
                    maxWeight = set.getWeight();
                    repsAtMaxWeight = set.getReps();
                }
            }

            // Check weight PR
            PersonalRecord weightPR = existingPRs.get(Constants.PR_TYPE_WEIGHT);
            if (maxWeight > 0 && (weightPR == null || maxWeight > weightPR.getValue())) {
                PersonalRecord newPR = createPersonalRecord(userId, exerciseName, 
                    Constants.PR_TYPE_WEIGHT, maxWeight, repsAtMaxWeight);
                detectedPRs.add(newPR);
            }

            // Find max reps
            int maxReps = 0;
            double weightAtMaxReps = 0;
            for (WorkoutSet set : sets) {
                if (set.getReps() > maxReps) {
                    maxReps = set.getReps();
                    weightAtMaxReps = set.getWeight();
                }
            }

            // Check rep PR
            PersonalRecord repPR = existingPRs.get(Constants.PR_TYPE_REPS);
            if (maxReps > 0 && (repPR == null || maxReps > repPR.getReps())) {
                PersonalRecord newPR = createPersonalRecord(userId, exerciseName,
                    Constants.PR_TYPE_REPS, weightAtMaxReps, maxReps);
                detectedPRs.add(newPR);
            }

            // Find max single-set volume
            double maxVolume = 0;
            int repsAtMaxVolume = 0;
            for (WorkoutSet set : sets) {
                double volume = set.getWeight() * set.getReps();
                if (volume > maxVolume) {
                    maxVolume = volume;
                    repsAtMaxVolume = set.getReps();
                }
            }

            // Check volume PR
            PersonalRecord volumePR = existingPRs.get(Constants.PR_TYPE_VOLUME);
            if (maxVolume > 0 && (volumePR == null || maxVolume > volumePR.getValue())) {
                PersonalRecord newPR = createPersonalRecord(userId, exerciseName,
                    Constants.PR_TYPE_VOLUME, maxVolume, repsAtMaxVolume);
                detectedPRs.add(newPR);
            }

        } catch (Exception e) {
            // Log error but continue
            e.printStackTrace();
        }
    }

    /**
     * Get existing PRs synchronously (for use in executor thread)
     */
    private Map<String, PersonalRecord> getExistingPRsSync(String userId, String exerciseName) {
        Map<String, PersonalRecord> prs = new HashMap<>();
        
        try {
            // Query Firestore synchronously
            var task = firestore.collection(Constants.COLLECTION_PERSONAL_RECORDS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("exerciseName", exerciseName)
                .get();

            // Wait for result
            var querySnapshot = com.google.android.gms.tasks.Tasks.await(task);
            
            for (var doc : querySnapshot.getDocuments()) {
                PersonalRecord pr = doc.toObject(PersonalRecord.class);
                if (pr != null) {
                    prs.put(pr.getRecordType(), pr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return prs;
    }

    /**
     * Create a PersonalRecord object
     */
    private PersonalRecord createPersonalRecord(String userId, String exerciseName,
                                               String recordType, double value, int reps) {
        PersonalRecord pr = new PersonalRecord();
        pr.setUserId(userId);
        pr.setExerciseName(exerciseName);
        pr.setRecordType(recordType);
        pr.setValue(value);
        pr.setReps(reps);
        pr.setAchievedAt(Timestamp.now());
        return pr;
    }

    /**
     * Save workout, sets, PRs, and update user stats
     */
    public void saveWorkout() {
        CompletedWorkout workout = workoutData.getValue();
        List<WorkoutSet> sets = exerciseSets.getValue();
        List<PersonalRecord> prs = newPersonalRecords.getValue();

        if (workout == null || sets == null) {
            savingComplete.setValue(false);
            return;
        }

        String userId = workout.getUserId();
        WriteBatch batch = firestore.batch();

        // 1. Save completed workout
        String workoutId = firestore.collection(Constants.COLLECTION_COMPLETED_WORKOUTS)
            .document().getId();
        workout.setWorkoutId(workoutId);
        workout.setSynced(true);
        
        DocumentReference workoutRef = firestore.collection(Constants.COLLECTION_COMPLETED_WORKOUTS)
            .document(workoutId);
        batch.set(workoutRef, workout);

        // 2. Save workout sets as subcollection
        for (WorkoutSet set : sets) {
            if (set.getStatus().equals(Constants.SET_STATUS_SKIPPED)) {
                continue; // Don't save skipped sets
            }

            String setId = firestore.collection(Constants.COLLECTION_COMPLETED_WORKOUTS)
                .document(workoutId)
                .collection(Constants.COLLECTION_WORKOUT_SETS)
                .document().getId();
            
            set.setSetId(setId);
            set.setWorkoutId(workoutId);

            DocumentReference setRef = workoutRef.collection(Constants.COLLECTION_WORKOUT_SETS)
                .document(setId);
            batch.set(setRef, set);
        }

        // 3. Save new PRs
        if (prs != null && !prs.isEmpty()) {
            for (PersonalRecord pr : prs) {
                String prId = firestore.collection(Constants.COLLECTION_PERSONAL_RECORDS)
                    .document().getId();
                pr.setRecordId(prId);

                DocumentReference prRef = firestore.collection(Constants.COLLECTION_PERSONAL_RECORDS)
                    .document(prId);
                batch.set(prRef, pr);
            }
        }

        // 4. Update user stats
        DocumentReference userRef = firestore.collection(Constants.COLLECTION_USERS)
            .document(userId);
        batch.update(userRef,
            "totalWorkouts", FieldValue.increment(1),
            "totalVolumeLifted", FieldValue.increment(workout.getTotalVolume()));

        // Commit batch
        batch.commit()
            .addOnSuccessListener(aVoid -> savingComplete.setValue(true))
            .addOnFailureListener(e -> {
                e.printStackTrace();
                savingComplete.setValue(false);
            });
    }

    // Getters for LiveData
    public LiveData<CompletedWorkout> getWorkoutData() {
        return workoutData;
    }

    public LiveData<List<WorkoutSet>> getExerciseSets() {
        return exerciseSets;
    }

    public LiveData<List<PersonalRecord>> getNewPRs() {
        return newPersonalRecords;
    }

    public LiveData<Boolean> getSavingComplete() {
        return savingComplete;
    }

    public LiveData<Boolean> getPrDetectionComplete() {
        return prDetectionComplete;
    }
}
