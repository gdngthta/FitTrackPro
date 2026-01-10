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
        // Full Body Starter - Beginner
        String programId = "preset_full_body_starter";
        WorkoutProgram program = new WorkoutProgram();
        program.setProgramId(programId);
        program.setUserId(null); // Preset programs have no user
        program.setProgramName("Full Body Starter");
        program.setDescription("Perfect for beginners. Full body workouts 3x per week.");
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

        // Add Day 1 - Full Body A
        String day1Id = "fbs_day_1";
        WorkoutDay day1 = new WorkoutDay();
        day1.setDayId(day1Id);
        day1.setProgramId(programId);
        day1.setDayNumber(1);
        day1.setDayName("Day 1 - Full Body A");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(day1Id)
                .set(day1);

        // Add exercises for Day 1
        addExercise(programId, day1Id, "ex1", "Barbell Back Squat", "Legs", "Barbell", 3, 8, 12, 120, 1);
        addExercise(programId, day1Id, "ex2", "Barbell Bench Press", "Chest", "Barbell", 3, 8, 12, 120, 2);
        addExercise(programId, day1Id, "ex3", "Barbell Row", "Back", "Barbell", 3, 8, 12, 90, 3);
        addExercise(programId, day1Id, "ex4", "Overhead Press", "Shoulders", "Barbell", 2, 8, 12, 90, 4);

        // Add Day 2 - Full Body B
        String day2Id = "fbs_day_2";
        WorkoutDay day2 = new WorkoutDay();
        day2.setDayId(day2Id);
        day2.setProgramId(programId);
        day2.setDayNumber(2);
        day2.setDayName("Day 2 - Full Body B");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(day2Id)
                .set(day2);

        // Add exercises for Day 2
        addExercise(programId, day2Id, "ex1", "Romanian Deadlift", "Legs", "Barbell", 3, 8, 12, 120, 1);
        addExercise(programId, day2Id, "ex2", "Incline Dumbbell Press", "Chest", "Dumbbell", 3, 8, 12, 90, 2);
        addExercise(programId, day2Id, "ex3", "Pull-Ups", "Back", "Bodyweight", 3, 5, 10, 120, 3);
        addExercise(programId, day2Id, "ex4", "Lateral Raises", "Shoulders", "Dumbbell", 2, 12, 15, 60, 4);

        // Add Day 3 - Full Body C
        String day3Id = "fbs_day_3";
        WorkoutDay day3 = new WorkoutDay();
        day3.setDayId(day3Id);
        day3.setProgramId(programId);
        day3.setDayNumber(3);
        day3.setDayName("Day 3 - Full Body C");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(day3Id)
                .set(day3);

        // Add exercises for Day 3
        addExercise(programId, day3Id, "ex1", "Leg Press", "Legs", "Machine", 3, 10, 15, 90, 1);
        addExercise(programId, day3Id, "ex2", "Dumbbell Bench Press", "Chest", "Dumbbell", 3, 8, 12, 90, 2);
        addExercise(programId, day3Id, "ex3", "Lat Pulldown", "Back", "Cable", 3, 10, 12, 90, 3);
        addExercise(programId, day3Id, "ex4", "Dumbbell Shoulder Press", "Shoulders", "Dumbbell", 2, 8, 12, 90, 4);
    }

    private void seedIntermediatePrograms() {
        // Push Pull Legs - Intermediate
        String programId = "preset_push_pull_legs";
        WorkoutProgram program = new WorkoutProgram();
        program.setProgramId(programId);
        program.setUserId(null);
        program.setProgramName("Push Pull Legs");
        program.setDescription("Split training for intermediate lifters.");
        program.setDifficulty("Intermediate");
        program.setDurationWeeks(12);
        program.setDaysPerWeek(4);
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

        // Upper Day (4th day for 4x/week split)
        String upperDayId = "ppl_upper";
        WorkoutDay upperDay = new WorkoutDay();
        upperDay.setDayId(upperDayId);
        upperDay.setProgramId(programId);
        upperDay.setDayNumber(4);
        upperDay.setDayName("Upper Day");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(upperDayId)
                .set(upperDay);

        addExercise(programId, upperDayId, "ex1", "Incline Barbell Bench Press", "Chest", "Barbell", 4, 8, 10, 120, 1);
        addExercise(programId, upperDayId, "ex2", "Cable Row", "Back", "Cable", 4, 10, 12, 90, 2);
        addExercise(programId, upperDayId, "ex3", "Dumbbell Shoulder Press", "Shoulders", "Dumbbell", 3, 10, 12, 90, 3);
        addExercise(programId, upperDayId, "ex4", "Dumbbell Flyes", "Chest", "Dumbbell", 3, 12, 15, 60, 4);
    }

    private void seedAdvancedPrograms() {
        // Strength & Hypertrophy - Pro
        String programId = "preset_strength_hypertrophy";
        WorkoutProgram program = new WorkoutProgram();
        program.setProgramId(programId);
        program.setUserId(null);
        program.setProgramName("Strength & Hypertrophy");
        program.setDescription("Advanced programming for experienced athletes.");
        program.setDifficulty("Pro");
        program.setDurationWeeks(16);
        program.setDaysPerWeek(5);
        program.setPreset(true);
        program.setActive(false);
        program.setCreatedAt(Timestamp.now());
        program.setUpdatedAt(Timestamp.now());

        firestore.collection("workoutPrograms")
                .document(programId)
                .set(program);

        // Day 1 - Heavy Lower
        String day1Id = "sh_day_1";
        WorkoutDay day1 = new WorkoutDay();
        day1.setDayId(day1Id);
        day1.setProgramId(programId);
        day1.setDayNumber(1);
        day1.setDayName("Heavy Lower");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(day1Id)
                .set(day1);

        addExercise(programId, day1Id, "ex1", "Barbell Back Squat", "Legs", "Barbell", 5, 3, 5, 240, 1);
        addExercise(programId, day1Id, "ex2", "Romanian Deadlift", "Legs", "Barbell", 4, 6, 8, 180, 2);
        addExercise(programId, day1Id, "ex3", "Leg Press", "Legs", "Machine", 4, 10, 12, 120, 3);
        addExercise(programId, day1Id, "ex4", "Leg Curl", "Legs", "Machine", 3, 10, 12, 90, 4);
        addExercise(programId, day1Id, "ex5", "Calf Raises", "Legs", "Machine", 4, 12, 15, 60, 5);

        // Day 2 - Heavy Upper
        String day2Id = "sh_day_2";
        WorkoutDay day2 = new WorkoutDay();
        day2.setDayId(day2Id);
        day2.setProgramId(programId);
        day2.setDayNumber(2);
        day2.setDayName("Heavy Upper");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(day2Id)
                .set(day2);

        addExercise(programId, day2Id, "ex1", "Barbell Bench Press", "Chest", "Barbell", 5, 3, 5, 240, 1);
        addExercise(programId, day2Id, "ex2", "Barbell Row", "Back", "Barbell", 5, 3, 5, 240, 2);
        addExercise(programId, day2Id, "ex3", "Overhead Press", "Shoulders", "Barbell", 4, 6, 8, 180, 3);
        addExercise(programId, day2Id, "ex4", "Pull-Ups", "Back", "Bodyweight", 3, 8, 10, 120, 4);

        // Day 3 - Hypertrophy Lower
        String day3Id = "sh_day_3";
        WorkoutDay day3 = new WorkoutDay();
        day3.setDayId(day3Id);
        day3.setProgramId(programId);
        day3.setDayNumber(3);
        day3.setDayName("Hypertrophy Lower");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(day3Id)
                .set(day3);

        addExercise(programId, day3Id, "ex1", "Front Squat", "Legs", "Barbell", 4, 8, 10, 120, 1);
        addExercise(programId, day3Id, "ex2", "Bulgarian Split Squat", "Legs", "Dumbbell", 3, 10, 12, 90, 2);
        addExercise(programId, day3Id, "ex3", "Leg Extension", "Legs", "Machine", 3, 12, 15, 60, 3);
        addExercise(programId, day3Id, "ex4", "Hamstring Curl", "Legs", "Machine", 3, 12, 15, 60, 4);

        // Day 4 - Hypertrophy Upper
        String day4Id = "sh_day_4";
        WorkoutDay day4 = new WorkoutDay();
        day4.setDayId(day4Id);
        day4.setProgramId(programId);
        day4.setDayNumber(4);
        day4.setDayName("Hypertrophy Upper");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(day4Id)
                .set(day4);

        addExercise(programId, day4Id, "ex1", "Incline Dumbbell Press", "Chest", "Dumbbell", 4, 8, 12, 90, 1);
        addExercise(programId, day4Id, "ex2", "Cable Row", "Back", "Cable", 4, 10, 12, 90, 2);
        addExercise(programId, day4Id, "ex3", "Lateral Raises", "Shoulders", "Dumbbell", 4, 12, 15, 60, 3);
        addExercise(programId, day4Id, "ex4", "Tricep Pushdown", "Arms", "Cable", 3, 12, 15, 60, 4);

        // Day 5 - Full Body Pump
        String day5Id = "sh_day_5";
        WorkoutDay day5 = new WorkoutDay();
        day5.setDayId(day5Id);
        day5.setProgramId(programId);
        day5.setDayNumber(5);
        day5.setDayName("Full Body Pump");

        firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(day5Id)
                .set(day5);

        addExercise(programId, day5Id, "ex1", "Hack Squat", "Legs", "Machine", 3, 12, 15, 90, 1);
        addExercise(programId, day5Id, "ex2", "Dumbbell Bench Press", "Chest", "Dumbbell", 3, 12, 15, 90, 2);
        addExercise(programId, day5Id, "ex3", "Lat Pulldown", "Back", "Cable", 3, 12, 15, 90, 3);
        addExercise(programId, day5Id, "ex4", "Face Pulls", "Shoulders", "Cable", 3, 15, 20, 60, 4);

        // Elite Powerbuilding - Elite
        String eliteProgramId = "preset_elite_powerbuilding";
        WorkoutProgram eliteProgram = new WorkoutProgram();
        eliteProgram.setProgramId(eliteProgramId);
        eliteProgram.setUserId(null);
        eliteProgram.setProgramName("Elite Powerbuilding");
        eliteProgram.setDescription("High-volume training for elite lifters.");
        eliteProgram.setDifficulty("Elite");
        eliteProgram.setDurationWeeks(16);
        eliteProgram.setDaysPerWeek(6);
        eliteProgram.setPreset(true);
        eliteProgram.setActive(false);
        eliteProgram.setCreatedAt(Timestamp.now());
        eliteProgram.setUpdatedAt(Timestamp.now());

        firestore.collection("workoutPrograms")
                .document(eliteProgramId)
                .set(eliteProgram);

        // Add 6 workout days for Elite program
        for (int i = 1; i <= 6; i++) {
            String dayId = "elite_day_" + i;
            WorkoutDay day = new WorkoutDay();
            day.setDayId(dayId);
            day.setProgramId(eliteProgramId);
            day.setDayNumber(i);
            
            String dayName;
            switch(i) {
                case 1: dayName = "Heavy Squat + Accessories"; break;
                case 2: dayName = "Heavy Bench + Accessories"; break;
                case 3: dayName = "Heavy Deadlift + Accessories"; break;
                case 4: dayName = "Volume Upper"; break;
                case 5: dayName = "Volume Lower"; break;
                case 6: dayName = "Conditioning + Accessories"; break;
                default: dayName = "Day " + i; break;
            }
            day.setDayName(dayName);

            firestore.collection("workoutPrograms")
                    .document(eliteProgramId)
                    .collection("workoutDays")
                    .document(dayId)
                    .set(day);

            // Add sample exercises for each day
            if (i == 1) {
                addExercise(eliteProgramId, dayId, "ex1", "Barbell Back Squat", "Legs", "Barbell", 5, 1, 3, 300, 1);
                addExercise(eliteProgramId, dayId, "ex2", "Front Squat", "Legs", "Barbell", 4, 6, 8, 180, 2);
                addExercise(eliteProgramId, dayId, "ex3", "Leg Press", "Legs", "Machine", 4, 10, 12, 120, 3);
            } else if (i == 2) {
                addExercise(eliteProgramId, dayId, "ex1", "Barbell Bench Press", "Chest", "Barbell", 5, 1, 3, 300, 1);
                addExercise(eliteProgramId, dayId, "ex2", "Incline Bench Press", "Chest", "Barbell", 4, 6, 8, 180, 2);
                addExercise(eliteProgramId, dayId, "ex3", "Dumbbell Flyes", "Chest", "Dumbbell", 3, 12, 15, 90, 3);
            } else if (i == 3) {
                addExercise(eliteProgramId, dayId, "ex1", "Deadlift", "Back", "Barbell", 5, 1, 3, 300, 1);
                addExercise(eliteProgramId, dayId, "ex2", "Romanian Deadlift", "Back", "Barbell", 4, 6, 8, 180, 2);
                addExercise(eliteProgramId, dayId, "ex3", "Pull-Ups", "Back", "Bodyweight", 4, 8, 10, 120, 3);
            }
        }
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
                .collection("programExercises")
                .document(exerciseId)
                .set(exercise);
    }
}
