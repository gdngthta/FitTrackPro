package com.fittrackpro.app.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data.local.dao.CompletedWorkoutDao;
import com.fittrackpro.app.data.local.dao.PersonalRecordDao;
import com.fittrackpro.app.data.local.dao.WorkoutProgramDao;
import com.fittrackpro.app.data.local.entity.CompletedWorkoutEntity;
import com.fittrackpro.app.data.local.entity.PersonalRecordEntity;
import com.fittrackpro.app.data.local.entity.WorkoutProgramEntity;
import com.fittrackpro.app.data.model.CompletedWorkout;
import com.fittrackpro.app.data.model.PersonalRecord;
import com.fittrackpro.app.data.model.ProgramExercise;
import com.fittrackpro.app.data.model.WorkoutDay;
import com.fittrackpro.app.data.model.WorkoutProgram;
import com.fittrackpro.app.data.model.WorkoutSet;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * WorkoutRepository manages workout programs, days, exercises, and completed workouts.
 *
 * Key responsibilities:
 * - Fetch preset programs
 * - Create/duplicate/edit user programs
 * - Manage workout days and exercises
 * - Save completed workouts
 * - Detect and save personal records
 * - Calculate stats (total volume, workout count, streak)
 */
public class WorkoutRepository {

    private final FirebaseFirestore firestore;
    private final WorkoutProgramDao programDao;
    private final CompletedWorkoutDao workoutDao;
    private final PersonalRecordDao recordDao;
    private final Executor executor;

    public WorkoutRepository(AppDatabase database) {
        this.firestore = FirebaseFirestore.getInstance();
        this.programDao = database.workoutProgramDao();
        this.workoutDao = database.completedWorkoutDao();
        this.recordDao = database.personalRecordDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // ==================== WORKOUT PROGRAMS ====================

    /**
     * Fetch all preset programs from Firestore
     */
    public LiveData<List<WorkoutProgram>> getPresetPrograms() {
        MutableLiveData<List<WorkoutProgram>> result = new MutableLiveData<>();

        firestore.collection("workoutPrograms")
                .whereEqualTo("isPreset", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<WorkoutProgram> programs = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        WorkoutProgram program = doc.toObject(WorkoutProgram.class);
                        programs.add(program);
                    }
                    result.setValue(programs);

                    // Cache in Room
                    executor.execute(() -> {
                        List<WorkoutProgramEntity> entities = new ArrayList<>();
                        for (WorkoutProgram p : programs) {
                            entities.add(programModelToEntity(p));
                        }
                        programDao.insertPrograms(entities);
                    });
                })
                .addOnFailureListener(e -> {
                    result.setValue(new ArrayList<>());
                });

        return result;
    }

    /**
     * Fetch user's active programs
     */
    public LiveData<List<WorkoutProgram>> getUserActivePrograms(String userId) {
        MutableLiveData<List<WorkoutProgram>> result = new MutableLiveData<>();

        firestore.collection("workoutPrograms")
                .whereEqualTo("userId", userId)
                .whereEqualTo("isActive", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<WorkoutProgram> programs = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        WorkoutProgram program = doc. toObject(WorkoutProgram. class);
                        programs.add(program);
                    }
                    result.setValue(programs);

                    // Cache in Room
                    executor.execute(() -> {
                        List<WorkoutProgramEntity> entities = new ArrayList<>();
                        for (WorkoutProgram p : programs) {
                            entities.add(programModelToEntity(p));
                        }
                        programDao.insertPrograms(entities);
                    });
                })
                .addOnFailureListener(e -> {
                    result. setValue(new ArrayList<>());
                });

        return result;
    }

    /**
     * Get program by ID
     */
    public LiveData<WorkoutProgram> getProgramById(String programId) {
        MutableLiveData<WorkoutProgram> result = new MutableLiveData<>();

        firestore.collection("workoutPrograms")
                .document(programId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    WorkoutProgram program = documentSnapshot.toObject(WorkoutProgram.class);
                    result.setValue(program);

                    if (program != null) {
                        executor.execute(() -> {
                            programDao.insertProgram(programModelToEntity(program));
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    result. setValue(null);
                });

        return result;
    }

    /**
     * Duplicate preset program for user editing
     */
    public LiveData<String> duplicatePresetProgram(String presetId, String userId) {
        MutableLiveData<String> result = new MutableLiveData<>();
        
        Log.d("WorkoutRepository", "duplicatePresetProgram called - presetId: " + presetId + ", userId: " + userId);

        // Fetch preset program
        firestore.collection("workoutPrograms")
                .document(presetId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("WorkoutRepository", "Successfully fetched preset document");
                    
                    WorkoutProgram preset = documentSnapshot.toObject(WorkoutProgram.class);
                    if (preset == null) {
                        Log.e("WorkoutRepository", "Preset program is null - document might not exist or has invalid data");
                        result.setValue(null);
                        return;
                    }
                    
                    Log.d("WorkoutRepository", "Preset program found: " + preset.getProgramName());

                    // Create new program document
                    String newProgramId = firestore.collection("workoutPrograms").document().getId();
                    Log.d("WorkoutRepository", "Generated new program ID: " + newProgramId);

                    WorkoutProgram newProgram = new WorkoutProgram();
                    newProgram.setProgramId(newProgramId);
                    newProgram.setUserId(userId);
                    newProgram.setProgramName(preset.getProgramName() + " (My Copy)");
                    newProgram.setDescription(preset.getDescription());
                    newProgram.setDifficulty(preset.getDifficulty());
                    newProgram.setDurationWeeks(preset.getDurationWeeks());
                    newProgram.setDaysPerWeek(preset.getDaysPerWeek());
                    newProgram.setPreset(false);
                    newProgram.setActive(true);
                    newProgram.setOriginalPresetId(presetId);
                    newProgram.setCreatedAt(Timestamp.now());
                    newProgram.setUpdatedAt(Timestamp.now());

                    Log.d("WorkoutRepository", "Saving new program to Firestore: " + newProgram.getProgramName());
                    
                    // Save new program
                    firestore.collection("workoutPrograms")
                            .document(newProgramId)
                            .set(newProgram)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("WorkoutRepository", "New program saved successfully, now duplicating workout days");
                                // Now duplicate workout days
                                duplicateWorkoutDays(presetId, newProgramId, result);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("WorkoutRepository", "Failed to save new program", e);
                                result.setValue(null);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("WorkoutRepository", "Failed to fetch preset program", e);
                    result.setValue(null);
                });

        return result;
    }

    /**
     * Helper to duplicate workout days from preset
     */
    private void duplicateWorkoutDays(String sourceId, String targetId, MutableLiveData<String> result) {
        Log.d("WorkoutRepository", "duplicateWorkoutDays - sourceId: " + sourceId + ", targetId: " + targetId);
        
        firestore.collection("workoutPrograms")
                .document(sourceId)
                .collection("workoutDays")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("WorkoutRepository", "Found " + querySnapshot.size() + " workout days to duplicate");
                    
                    if (querySnapshot.isEmpty()) {
                        Log.w("WorkoutRepository", "No workout days found, completing duplication");
                        result.setValue(targetId);
                        return;
                    }

                    int[] counter = {0};
                    int total = querySnapshot.size();

                    for (QueryDocumentSnapshot dayDoc : querySnapshot) {
                        WorkoutDay day = dayDoc.toObject(WorkoutDay.class);
                        String newDayId = firestore.collection("workoutPrograms")
                                .document(targetId)
                                .collection("workoutDays")
                                .document().getId();

                        Log.d("WorkoutRepository", "Duplicating day: " + day.getDayName() + " -> " + newDayId);

                        day.setDayId(newDayId);
                        day.setProgramId(targetId);

                        firestore.collection("workoutPrograms")
                                .document(targetId)
                                .collection("workoutDays")
                                .document(newDayId)
                                .set(day)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("WorkoutRepository", "Day saved successfully, duplicating exercises");
                                    // Duplicate exercises for this day
                                    duplicateExercises(sourceId, dayDoc.getId(), targetId, newDayId);

                                    counter[0]++;
                                    Log.d("WorkoutRepository", "Progress: " + counter[0] + "/" + total + " days duplicated");
                                    if (counter[0] == total) {
                                        Log.d("WorkoutRepository", "All workout days duplicated successfully!");
                                        result.setValue(targetId);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("WorkoutRepository", "Failed to save workout day", e);
                                    result.setValue(null);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("WorkoutRepository", "Failed to fetch workout days", e);
                    result.setValue(null);
                });
    }

    /**
     * Helper to duplicate exercises
     */
    private void duplicateExercises(String sourceProgramId, String sourceDayId,
                                    String targetProgramId, String targetDayId) {
        Log.d("WorkoutRepository", "duplicateExercises - sourceDayId: " + sourceDayId + ", targetDayId: " + targetDayId);
        
        firestore.collection("workoutPrograms")
                .document(sourceProgramId)
                .collection("workoutDays")
                .document(sourceDayId)
                .collection("programExercises")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("WorkoutRepository", "Found " + querySnapshot.size() + " exercises to duplicate");
                    
                    for (QueryDocumentSnapshot exDoc : querySnapshot) {
                        ProgramExercise exercise = exDoc.toObject(ProgramExercise.class);
                        String newExId = firestore.collection("workoutPrograms")
                                .document(targetProgramId)
                                .collection("workoutDays")
                                .document(targetDayId)
                                .collection("programExercises")
                                .document().getId();

                        Log.d("WorkoutRepository", "Duplicating exercise: " + exercise.getExerciseName() + " -> " + newExId);

                        exercise.setExerciseId(newExId);
                        exercise.setDayId(targetDayId);

                        firestore.collection("workoutPrograms")
                                .document(targetProgramId)
                                .collection("workoutDays")
                                .document(targetDayId)
                                .collection("programExercises")
                                .document(newExId)
                                .set(exercise)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("WorkoutRepository", "Exercise saved successfully: " + exercise.getExerciseName());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("WorkoutRepository", "Failed to save exercise: " + exercise.getExerciseName(), e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("WorkoutRepository", "Failed to fetch exercises for day " + sourceDayId, e);
                });
    }

    /**
     * Create custom program
     */
    public LiveData<String> createCustomProgram(String userId, String programName, String description,
                                                String difficulty, int durationWeeks, int daysPerWeek) {
        MutableLiveData<String> result = new MutableLiveData<>();

        String programId = firestore.collection("workoutPrograms").document().getId();

        WorkoutProgram program = new WorkoutProgram();
        program.setProgramId(programId);
        program.setUserId(userId);
        program.setProgramName(programName);
        program.setDescription(description);
        program.setDifficulty(difficulty);
        program.setDurationWeeks(durationWeeks);
        program.setDaysPerWeek(daysPerWeek);
        program.setPreset(false);
        program.setActive(true);
        program.setCreatedAt(Timestamp.now());
        program.setUpdatedAt(Timestamp.now());

        firestore. collection("workoutPrograms")
                .document(programId)
                .set(program)
                .addOnSuccessListener(aVoid -> result.setValue(programId))
                .addOnFailureListener(e -> result.setValue(null));

        return result;
    }

    // ==================== WORKOUT DAYS ====================

    /**
     * Get workout days for a program
     */
    public LiveData<List<WorkoutDay>> getWorkoutDays(String programId) {
        MutableLiveData<List<WorkoutDay>> result = new MutableLiveData<>();

        firestore. collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .orderBy("dayNumber")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<WorkoutDay> days = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        WorkoutDay day = doc. toObject(WorkoutDay.class);
                        days.add(day);
                    }
                    result.setValue(days);
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

    /**
     * Add workout day to program
     */
    public LiveData<Boolean> addWorkoutDay(String programId, String dayName, int dayNumber) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        String dayId = firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document().getId();

        WorkoutDay day = new WorkoutDay();
        day.setDayId(dayId);
        day.setProgramId(programId);
        day.setDayName(dayName);
        day.setDayNumber(dayNumber);
        day.setWarmupEnabled(true);
        day.setCooldownEnabled(true);

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(dayId)
                .set(day)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));

        return result;
    }

    // ==================== EXERCISES ====================

    /**
     * Get exercises for a workout day
     */
    public LiveData<List<ProgramExercise>> getExercisesForDay(String programId, String dayId) {
        MutableLiveData<List<ProgramExercise>> result = new MutableLiveData<>();

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(dayId)
                .collection("programExercises")
                .orderBy("orderIndex")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ProgramExercise> exercises = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        ProgramExercise exercise = doc.toObject(ProgramExercise.class);
                        exercises.add(exercise);
                    }
                    result.setValue(exercises);
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

    /**
     * Add exercise to workout day
     */
    public LiveData<Boolean> addExercise(String programId, String dayId, ProgramExercise exercise) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        String exerciseId = firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(dayId)
                .collection("programExercises")
                .document().getId();

        exercise.setExerciseId(exerciseId);
        exercise.setDayId(dayId);

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(dayId)
                .collection("programExercises")
                .document(exerciseId)
                .set(exercise)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));

        return result;
    }

    // ==================== COMPLETED WORKOUTS ====================

    /**
     * Save completed workout with sets
     * This also triggers PR detection
     * Offline-first: saves to Room immediately, then syncs to Firestore
     */
    public LiveData<Boolean> saveCompletedWorkout(String userId, CompletedWorkout workout,
                                                  List<WorkoutSet> sets) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        String workoutId = firestore.collection("completedWorkouts").document().getId();
        workout.setWorkoutId(workoutId);
        workout.setUserId(userId);
        workout.setSynced(false); // Mark as unsynced initially

        // Save to Room FIRST for instant UI update
        executor.execute(() -> {
            workoutDao.insertWorkout(workoutModelToEntity(workout));
            
            // Detect PRs from sets
            detectAndSavePersonalRecords(userId, sets);
            
            result.postValue(true); // Immediately return success
            
            // Then attempt to sync to Firestore in background
            firestore.collection("completedWorkouts")
                    .document(workoutId)
                    .set(workout)
                    .addOnSuccessListener(aVoid -> {
                        // Save sets as subcollection
                        saveWorkoutSets(workoutId, sets);
                        
                        // Mark as synced in Room
                        workout.setSynced(true);
                        CompletedWorkoutEntity entity = workoutModelToEntity(workout);
                        entity.setSynced(true);
                        entity.setLastSyncAttempt(System.currentTimeMillis());
                        workoutDao.updateWorkout(entity);
                    })
                    .addOnFailureListener(e -> {
                        // Sync failed, will retry later via SyncWorker
                        // Data is already in Room, so user doesn't lose it
                        Log.e("WorkoutRepository", "Failed to sync workout to Firestore", e);
                    });
        });

        return result;
    }

    /**
     * Save workout sets as subcollection
     */
    private void saveWorkoutSets(String workoutId, List<WorkoutSet> sets) {
        for (WorkoutSet set : sets) {
            if (set.getStatus().equals("skipped")) {
                continue; // Don't save skipped sets
            }

            String setId = firestore.collection("completedWorkouts")
                    .document(workoutId)
                    .collection("workoutSets")
                    .document().getId();

            set.setSetId(setId);
            set.setWorkoutId(workoutId);

            firestore.collection("completedWorkouts")
                    .document(workoutId)
                    .collection("workoutSets")
                    .document(setId)
                    .set(set);
        }
    }

    /**
     * Detect personal records from workout sets
     */
    private void detectAndSavePersonalRecords(String userId, List<WorkoutSet> sets) {
        executor.execute(() -> {
            for (WorkoutSet set : sets) {
                if (! set.getStatus().equals("completed") && !set.getStatus().equals("modified")) {
                    continue;
                }

                String exerciseName = set.getExerciseName();
                double weight = set.getWeight();
                int reps = set.getReps();
                double volume = weight * reps;

                // Check weight PR
                PersonalRecordEntity weightPR = recordDao.getBestRecord(userId, exerciseName, "weight");
                if (weightPR == null || weight > weightPR.getValue()) {
                    savePersonalRecord(userId, exerciseName, "weight", weight, reps);
                }

                // Check rep PR (at same weight)
                PersonalRecordEntity repPR = recordDao. getBestRecord(userId, exerciseName, "reps");
                if (repPR == null || (weight == repPR.getValue() && reps > repPR.getReps())) {
                    savePersonalRecord(userId, exerciseName, "reps", weight, reps);
                }

                // Check volume PR (single set)
                PersonalRecordEntity volumePR = recordDao.getBestRecord(userId, exerciseName, "volume");
                if (volumePR == null || volume > volumePR.getValue()) {
                    savePersonalRecord(userId, exerciseName, "volume", volume, reps);
                }
            }
        });
    }

    /**
     * Save personal record
     */
    private void savePersonalRecord(String userId, String exerciseName, String recordType,
                                    double value, int reps) {
        String recordId = firestore.collection("personalRecords").document().getId();

        PersonalRecord record = new PersonalRecord();
        record.setRecordId(recordId);
        record.setUserId(userId);
        record.setExerciseName(exerciseName);
        record.setRecordType(recordType);
        record.setValue(value);
        record.setReps(reps);
        record.setAchievedAt(Timestamp.now());

        firestore.collection("personalRecords")
                .document(recordId)
                .set(record)
                .addOnSuccessListener(aVoid -> {
                    executor.execute(() -> {
                        recordDao.insertRecord(recordModelToEntity(record));
                    });
                });
    }

    /**
     * Get recent workouts
     */
    public LiveData<List<CompletedWorkout>> getRecentWorkouts(String userId, int limit) {
        MutableLiveData<List<CompletedWorkout>> result = new MutableLiveData<>();

        firestore.collection("completedWorkouts")
                .whereEqualTo("userId", userId)
                .orderBy("startTime", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<CompletedWorkout> workouts = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        CompletedWorkout workout = doc.toObject(CompletedWorkout.class);
                        workouts.add(workout);
                    }
                    result.setValue(workouts);

                    // Cache in Room
                    executor.execute(() -> {
                        for (CompletedWorkout w : workouts) {
                            workoutDao.insertWorkout(workoutModelToEntity(w));
                        }
                    });
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

    /**
     * Get personal records for user
     */
    public LiveData<List<PersonalRecord>> getPersonalRecords(String userId) {
        MutableLiveData<List<PersonalRecord>> result = new MutableLiveData<>();

        firestore.collection("personalRecords")
                .whereEqualTo("userId", userId)
                .orderBy("achievedAt", Query.Direction. DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<PersonalRecord> records = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        PersonalRecord record = doc.toObject(PersonalRecord.class);
                        records. add(record);
                    }
                    result.setValue(records);
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

    // ==================== CONVERSION HELPERS ====================

    private WorkoutProgramEntity programModelToEntity(WorkoutProgram program) {
        WorkoutProgramEntity entity = new WorkoutProgramEntity();
        entity.setProgramId(program.getProgramId());
        entity.setUserId(program.getUserId());
        entity.setProgramName(program.getProgramName());
        entity.setDescription(program.getDescription());
        entity.setDifficulty(program.getDifficulty());
        entity.setDurationWeeks(program.getDurationWeeks());
        entity.setDaysPerWeek(program.getDaysPerWeek());
        entity.setPreset(program.isPreset());
        entity.setActive(program.isActive());
        entity.setOriginalPresetId(program.getOriginalPresetId());
        entity.setCreatedAt(program.getCreatedAt() != null ? program.getCreatedAt().toDate().getTime() : 0);
        entity.setUpdatedAt(program.getUpdatedAt() != null ? program.getUpdatedAt().toDate().getTime() : 0);
        return entity;
    }

    private CompletedWorkoutEntity workoutModelToEntity(CompletedWorkout workout) {
        CompletedWorkoutEntity entity = new CompletedWorkoutEntity();
        entity.setWorkoutId(workout. getWorkoutId());
        entity.setUserId(workout.getUserId());
        entity.setProgramId(workout.getProgramId());
        entity.setDayId(workout.getDayId());
        entity.setWorkoutName(workout.getWorkoutName());
        entity.setStartTime(workout.getStartTime() != null ? workout.getStartTime().toDate().getTime() : 0);
        entity.setEndTime(workout.getEndTime() != null ? workout.getEndTime().toDate().getTime() : 0);
        entity.setDurationSeconds(workout.getDurationSeconds());
        entity.setTotalVolume(workout.getTotalVolume());
        entity.setTotalSets(workout.getTotalSets());
        entity.setTotalExercises(workout.getTotalExercises());
        entity.setSynced(workout.isSynced());
        return entity;
    }

    private PersonalRecordEntity recordModelToEntity(PersonalRecord record) {
        PersonalRecordEntity entity = new PersonalRecordEntity();
        entity.setRecordId(record.getRecordId());
        entity.setUserId(record.getUserId());
        entity.setExerciseName(record.getExerciseName());
        entity.setRecordType(record.getRecordType());
        entity.setValue(record.getValue());
        entity.setReps(record.getReps());
        entity.setAchievedAt(record.getAchievedAt() != null ? record.getAchievedAt().toDate().getTime() : 0);
        return entity;
    }

    /**
     * Add preset program to user by duplicating it and activating it
     */
    public LiveData<Boolean> addPresetProgramToUser(String presetId, String userId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        // Duplicate the preset program and activate it  
        LiveData<String> duplicateResult = duplicatePresetProgram(presetId, userId);
        
        // Create a one-time observer
        androidx.lifecycle.Observer<String> observer = new androidx.lifecycle.Observer<String>() {
            @Override
            public void onChanged(String newProgramId) {
                if (newProgramId != null) {
                    Log.d("WorkoutRepository", "Program added successfully: " + newProgramId);
                    result.setValue(true);
                } else {
                    Log.e("WorkoutRepository", "Failed to add program - duplication returned null");
                    result.setValue(false);
                }
                // Remove observer after first notification to prevent memory leaks
                duplicateResult.removeObserver(this);
            }
        };
        
        duplicateResult.observeForever(observer);
        
        return result;
    }

    /**
     * Initialize preset programs with correct difficulty levels
     */
    public void initializePresetPrograms() {
        // Define preset programs with correct difficulties
        Object[][] presets = {
            {"Full Body Starter", "Beginner", 3, 12, 
                "Perfect for newcomers. Balanced full-body workouts to build foundational strength."},
            {"Push Pull Legs", "Intermediate", 4, 12,
                "Split training targeting specific muscle groups. Ideal for 6+ months of training."},
            {"Strength & Hypertrophy", "Pro", 5, 12,
                "High-volume training combining strength and muscle building for experienced lifters."},
            {"Elite Powerbuilding", "Elite", 6, 12,
                "Advanced periodized training for elite athletes. Maximum strength and size gains."}
        };

        for (Object[] preset : presets) {
            String name = (String) preset[0];
            String difficulty = (String) preset[1];
            int daysPerWeek = (int) preset[2];
            int durationWeeks = (int) preset[3];
            String description = (String) preset[4];

            // Check if program already exists
            firestore.collection("workoutPrograms")
                    .whereEqualTo("programName", name)
                    .whereEqualTo("isPreset", true)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot.isEmpty()) {
                            // Create new preset program
                            createPresetProgram(name, difficulty, daysPerWeek, durationWeeks, description);
                        } else {
                            // Update existing program's difficulty
                            for (com.google.firebase.firestore.QueryDocumentSnapshot doc : querySnapshot) {
                                firestore.collection("workoutPrograms")
                                        .document(doc.getId())
                                        .update("difficulty", difficulty, "daysPerWeek", daysPerWeek, 
                                                "description", description, "updatedAt", Timestamp.now())
                                        .addOnSuccessListener(aVoid -> 
                                            Log.d("WorkoutRepository", "Updated preset: " + name + " -> " + difficulty))
                                        .addOnFailureListener(e -> 
                                            Log.e("WorkoutRepository", "Failed to update: " + name, e));
                            }
                        }
                    })
                    .addOnFailureListener(e -> 
                        Log.e("WorkoutRepository", "Failed to check existing preset: " + name, e));
        }
    }

    /**
     * Create a new preset program
     */
    private void createPresetProgram(String name, String difficulty, int daysPerWeek, 
                                    int durationWeeks, String description) {
        String programId = firestore.collection("workoutPrograms").document().getId();

        WorkoutProgram program = new WorkoutProgram();
        program.setProgramId(programId);
        program.setUserId(null); // Preset programs have no owner
        program.setProgramName(name);
        program.setDescription(description);
        program.setDifficulty(difficulty);
        program.setDurationWeeks(durationWeeks);
        program.setDaysPerWeek(daysPerWeek);
        program.setPreset(true);
        program.setActive(false);
        program.setCreatedAt(Timestamp.now());
        program.setUpdatedAt(Timestamp.now());

        firestore.collection("workoutPrograms")
                .document(programId)
                .set(program)
                .addOnSuccessListener(aVoid -> 
                    Log.d("WorkoutRepository", "Created preset: " + name + " -> " + difficulty))
                .addOnFailureListener(e -> 
                    Log.e("WorkoutRepository", "Failed to create preset: " + name, e));
    }

    /**
     * Activate a program for the user
     */
    public void activateProgram(String programId, String userId) {
        firestore.collection("workoutPrograms")
                .document(programId)
                .update("isActive", true, "updatedAt", Timestamp.now())
                .addOnSuccessListener(aVoid -> {
                    Log.d("WorkoutRepository", "Program activated: " + programId);
                })
                .addOnFailureListener(e -> {
                    Log.e("WorkoutRepository", "Failed to activate program", e);
                });
    }
}