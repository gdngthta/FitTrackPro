package com.fittrackpro.app.data.repository;

import com.fittrackpro.app.data.model.ProgramExercise;
import com.fittrackpro.app.data.model.WorkoutDay;
import com.fittrackpro.app.data.model.WorkoutProgram;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Utility class to seed preset workout programs into Firestore
 * This should be run once during initial setup or for testing
 */
public class PresetProgramSeeder {

    private final FirebaseFirestore firestore;
    private final Executor executor;

    public PresetProgramSeeder() {
        this.firestore = FirebaseFirestore.getInstance();
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Seed all preset programs
     */
    public void seedPresetPrograms() {
        executor.execute(() -> {
            seedBeginnerPrograms();
            seedIntermediatePrograms();
            seedAdvancedPrograms();
        });
    }

    private void seedBeginnerPrograms() {
        // Starting Strength - Beginner
        String programId = "preset_starting_strength";
        WorkoutProgram program = new WorkoutProgram();
        program.setProgramId(programId);
        program.setUserId(null); // Preset programs have no user
        program.setProgramName("Starting Strength");
        program.setDescription("Perfect for newcomers to strength training. Focus on compound movements with progressive overload. Build foundational strength and learn proper form.");
        program.setDifficulty("Beginner");
        program.setDurationWeeks(12);
        program.setDaysPerWeek(3);
        program.setPreset(true);
        program.setActive(false);
        program.setCreatedAt(Timestamp.now());
        program.setUpdatedAt(Timestamp.now());

        firestore.collection("workoutPrograms")
                .document(programId)
                .set(program);

        // Add Day A
        String dayAId = "ss_day_a";
        WorkoutDay dayA = new WorkoutDay();
        dayA.setDayId(dayAId);
        dayA.setProgramId(programId);
        dayA.setDayNumber(1);
        dayA.setDayName("Day A - Squat Day");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(dayAId)
                .set(dayA);

        // Add exercises for Day A
        addExercise(programId, dayAId, "ex1", "Barbell Back Squat", "Legs", "Barbell", 3, 5, 5, 180, 1);
        addExercise(programId, dayAId, "ex2", "Barbell Bench Press", "Chest", "Barbell", 3, 5, 5, 180, 2);
        addExercise(programId, dayAId, "ex3", "Barbell Deadlift", "Back", "Barbell", 1, 5, 5, 240, 3);

        // Add Day B
        String dayBId = "ss_day_b";
        WorkoutDay dayB = new WorkoutDay();
        dayB.setDayId(dayBId);
        dayB.setProgramId(programId);
        dayB.setDayNumber(2);
        dayB.setDayName("Day B - Press Day");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(dayBId)
                .set(dayB);

        // Add exercises for Day B
        addExercise(programId, dayBId, "ex1", "Barbell Back Squat", "Legs", "Barbell", 3, 5, 5, 180, 1);
        addExercise(programId, dayBId, "ex2", "Overhead Press", "Shoulders", "Barbell", 3, 5, 5, 180, 2);
        addExercise(programId, dayBId, "ex3", "Barbell Row", "Back", "Barbell", 3, 5, 5, 180, 3);

        // Full Body Foundation - Beginner
        String fbfProgramId = "preset_full_body_foundation";
        WorkoutProgram fbfProgram = new WorkoutProgram();
        fbfProgram.setProgramId(fbfProgramId);
        fbfProgram.setUserId(null);
        fbfProgram.setProgramName("Full Body Foundation");
        fbfProgram.setDescription("Balanced full-body workouts with higher volume for building muscle endurance and size. Perfect for building a solid fitness base.");
        fbfProgram.setDifficulty("Beginner");
        fbfProgram.setDurationWeeks(8);
        fbfProgram.setDaysPerWeek(4);
        fbfProgram.setPreset(true);
        fbfProgram.setActive(false);
        fbfProgram.setCreatedAt(Timestamp.now());
        fbfProgram.setUpdatedAt(Timestamp.now());

        firestore.collection("workoutPrograms")
                .document(fbfProgramId)
                .set(fbfProgram);
    }

    private void seedIntermediatePrograms() {
        // Push Pull Legs - Intermediate
        String programId = "preset_push_pull_legs";
        WorkoutProgram program = new WorkoutProgram();
        program.setProgramId(programId);
        program.setUserId(null);
        program.setProgramName("Push Pull Legs (PPL)");
        program.setDescription("Split training targeting specific muscle groups. Increase training volume and intensity. Ideal for those with 6+ months of consistent training.");
        program.setDifficulty("Intermediate");
        program.setDurationWeeks(12);
        program.setDaysPerWeek(6);
        program.setPreset(true);
        program.setActive(false);
        program.setCreatedAt(Timestamp.now());
        program.setUpdatedAt(Timestamp.now());

        firestore.collection("workoutPrograms")
                .document(programId)
                .set(program);

        // Push Day
        String pushDayId = "ppl_push";
        WorkoutDay pushDay = new WorkoutDay();
        pushDay.setDayId(pushDayId);
        pushDay.setProgramId(programId);
        pushDay.setDayNumber(1);
        pushDay.setDayName("Push Day");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(pushDayId)
                .set(pushDay);

        addExercise(programId, pushDayId, "ex1", "Barbell Bench Press", "Chest", "Barbell", 4, 8, 8, 120, 1);
        addExercise(programId, pushDayId, "ex2", "Overhead Press", "Shoulders", "Barbell", 3, 10, 10, 90, 2);
        addExercise(programId, pushDayId, "ex3", "Incline Dumbbell Press", "Chest", "Dumbbell", 3, 12, 12, 90, 3);
        addExercise(programId, pushDayId, "ex4", "Lateral Raises", "Shoulders", "Dumbbell", 3, 15, 15, 60, 4);
        addExercise(programId, pushDayId, "ex5", "Tricep Dips", "Arms", "Bodyweight", 3, 10, 10, 90, 5);
        addExercise(programId, pushDayId, "ex6", "Tricep Extension", "Arms", "Cable", 3, 12, 12, 60, 6);

        // Pull Day
        String pullDayId = "ppl_pull";
        WorkoutDay pullDay = new WorkoutDay();
        pullDay.setDayId(pullDayId);
        pullDay.setProgramId(programId);
        pullDay.setDayNumber(2);
        pullDay.setDayName("Pull Day");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(pullDayId)
                .set(pullDay);

        addExercise(programId, pullDayId, "ex1", "Deadlift", "Back", "Barbell", 4, 6, 6, 180, 1);
        addExercise(programId, pullDayId, "ex2", "Pull-Ups", "Back", "Bodyweight", 3, 8, 8, 120, 2);
        addExercise(programId, pullDayId, "ex3", "Barbell Row", "Back", "Barbell", 4, 10, 10, 90, 3);
        addExercise(programId, pullDayId, "ex4", "Face Pulls", "Shoulders", "Cable", 3, 15, 15, 60, 4);
        addExercise(programId, pullDayId, "ex5", "Barbell Curl", "Arms", "Barbell", 3, 10, 10, 60, 5);
        addExercise(programId, pullDayId, "ex6", "Hammer Curl", "Arms", "Dumbbell", 3, 12, 12, 60, 6);

        // Leg Day
        String legDayId = "ppl_legs";
        WorkoutDay legDay = new WorkoutDay();
        legDay.setDayId(legDayId);
        legDay.setProgramId(programId);
        legDay.setDayNumber(3);
        legDay.setDayName("Leg Day");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(legDayId)
                .set(legDay);

        addExercise(programId, legDayId, "ex1", "Barbell Back Squat", "Legs", "Barbell", 4, 8, 8, 180, 1);
        addExercise(programId, legDayId, "ex2", "Romanian Deadlift", "Legs", "Barbell", 3, 10, 10, 120, 2);
        addExercise(programId, legDayId, "ex3", "Leg Press", "Legs", "Machine", 3, 12, 12, 90, 3);
        addExercise(programId, legDayId, "ex4", "Leg Curl", "Legs", "Machine", 3, 12, 12, 60, 4);
        addExercise(programId, legDayId, "ex5", "Calf Raises", "Legs", "Machine", 4, 15, 15, 60, 5);

        // Upper Lower Split - Intermediate
        String ulProgramId = "preset_upper_lower";
        WorkoutProgram ulProgram = new WorkoutProgram();
        ulProgram.setProgramId(ulProgramId);
        ulProgram.setUserId(null);
        ulProgram.setProgramName("Upper Lower Split");
        ulProgram.setDescription("Alternating upper and lower body focus with high volume. Great for balanced strength and muscle development.");
        ulProgram.setDifficulty("Intermediate");
        ulProgram.setDurationWeeks(10);
        ulProgram.setDaysPerWeek(4);
        ulProgram.setPreset(true);
        ulProgram.setActive(false);
        ulProgram.setCreatedAt(Timestamp.now());
        ulProgram.setUpdatedAt(Timestamp.now());

        firestore.collection("workoutPrograms")
                .document(ulProgramId)
                .set(ulProgram);
    }

    private void seedAdvancedPrograms() {
        // 5/3/1 Wendler - Advanced
        String programId = "preset_531_wendler";
        WorkoutProgram program = new WorkoutProgram();
        program.setProgramId(programId);
        program.setUserId(null);
        program.setProgramName("5/3/1 Wendler");
        program.setDescription("High-volume periodized training for experienced lifters. Complex programming with percentage-based loading. Maximize strength and muscle gains.");
        program.setDifficulty("Advanced");
        program.setDurationWeeks(16);
        program.setDaysPerWeek(4);
        program.setPreset(true);
        program.setActive(false);
        program.setCreatedAt(Timestamp.now());
        program.setUpdatedAt(Timestamp.now());

        firestore.collection("workoutPrograms")
                .document(programId)
                .set(program);

        // PHAT - Advanced
        String phatProgramId = "preset_phat";
        WorkoutProgram phatProgram = new WorkoutProgram();
        phatProgram.setProgramId(phatProgramId);
        phatProgram.setUserId(null);
        phatProgram.setProgramName("PHAT");
        phatProgram.setDescription("Combining power and hypertrophy across upper/lower splits with varying rep ranges. Advanced program for experienced athletes.");
        phatProgram.setDifficulty("Advanced");
        phatProgram.setDurationWeeks(12);
        phatProgram.setDaysPerWeek(5);
        phatProgram.setPreset(true);
        phatProgram.setActive(false);
        phatProgram.setCreatedAt(Timestamp.now());
        phatProgram.setUpdatedAt(Timestamp.now());

        firestore.collection("workoutPrograms")
                .document(phatProgramId)
                .set(phatProgram);
    }

    private void addExercise(String programId, String dayId, String exerciseId,
                            String name, String muscleGroup, String equipment,
                            int sets, int repsMin, int repsMax, int rest, int order) {
        ProgramExercise exercise = new ProgramExercise();
        exercise.setExerciseId(exerciseId);
        exercise.setDayId(dayId);
        exercise.setExerciseName(name);
        exercise.setMuscleGroup(muscleGroup);
        exercise.setEquipment(equipment);
        exercise.setTargetSets(sets);
        exercise.setTargetRepsMin(repsMin);
        exercise.setTargetRepsMax(repsMax);
        exercise.setRestSeconds(rest);
        exercise.setOrderIndex(order);

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(dayId)
                .collection("exercises")
                .document(exerciseId)
                .set(exercise);
    }
}
