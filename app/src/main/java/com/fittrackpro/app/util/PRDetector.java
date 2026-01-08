package com.fittrackpro.app.util;

import com.fittrackpro.app.data.model.PersonalRecord;
import com.fittrackpro.app.data.model.WorkoutSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PRDetector detects personal records from workout sets.
 *
 * Types of PRs:
 * - Weight PR:  Highest weight lifted for an exercise
 * - Rep PR: Most reps at a given weight
 * - Volume PR:  Highest single-set volume (weight × reps)
 */
public class PRDetector {

    /**
     * Detect PRs from a list of workout sets
     *
     * @param sets List of completed workout sets
     * @param existingRecords Map of exercise name → record type → current best record
     * @return List of new PRs achieved
     */
    public static List<PersonalRecord> detectPRs(List<WorkoutSet> sets,
                                                 Map<String, Map<String, PersonalRecord>> existingRecords) {
        List<PersonalRecord> newPRs = new ArrayList<>();

        for (WorkoutSet set : sets) {
            // Skip non-completed sets
            if (!set.getStatus().equals("completed") && ! set.getStatus().equals("modified")) {
                continue;
            }

            String exerciseName = set.getExerciseName();
            double weight = set.getWeight();
            int reps = set.getReps();
            double volume = weight * reps;

            Map<String, PersonalRecord> exerciseRecords = existingRecords.get(exerciseName);
            if (exerciseRecords == null) {
                exerciseRecords = new HashMap<>();
            }

            // Check weight PR
            PersonalRecord weightPR = exerciseRecords.get("weight");
            if (weightPR == null || weight > weightPR.getValue()) {
                PersonalRecord newPR = new PersonalRecord();
                newPR.setExerciseName(exerciseName);
                newPR.setRecordType("weight");
                newPR.setValue(weight);
                newPR.setReps(reps);
                newPRs.add(newPR);
            }

            // Check rep PR (at same weight)
            PersonalRecord repPR = exerciseRecords. get("reps");
            if (repPR == null || (weight >= repPR.getValue() && reps > repPR.getReps())) {
                PersonalRecord newPR = new PersonalRecord();
                newPR. setExerciseName(exerciseName);
                newPR. setRecordType("reps");
                newPR. setValue(weight);
                newPR.setReps(reps);
                newPRs. add(newPR);
            }

            // Check volume PR
            PersonalRecord volumePR = exerciseRecords.get("volume");
            if (volumePR == null || volume > volumePR.getValue()) {
                PersonalRecord newPR = new PersonalRecord();
                newPR. setExerciseName(exerciseName);
                newPR. setRecordType("volume");
                newPR.setValue(volume);
                newPR.setReps(reps);
                newPRs.add(newPR);
            }
        }

        return newPRs;
    }

    /**
     * Format PR message for display
     */
    public static String formatPRMessage(PersonalRecord pr) {
        switch (pr.getRecordType()) {
            case "weight":
                return String.format("🎉 New Weight PR: %s - %.1f kg!",
                        pr.getExerciseName(), pr.getValue());
            case "reps":
                return String. format("🎉 New Rep PR: %s - %d reps @ %.1f kg!",
                        pr.getExerciseName(), pr.getReps(), pr.getValue());
            case "volume":
                return String.format("🎉 New Volume PR: %s - %.1f kg total!",
                        pr.getExerciseName(), pr.getValue());
            default:
                return "🎉 New Personal Record!";
        }
    }
}