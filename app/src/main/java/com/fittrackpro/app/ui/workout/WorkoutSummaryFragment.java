package com.fittrackpro.app.ui.workout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.R;
import com.fittrackpro.app.data.model.CompletedWorkout;
import com.fittrackpro.app.data.model.PersonalRecord;
import com.fittrackpro.app.data.model.WorkoutSet;
import com.fittrackpro.app.databinding.FragmentWorkoutSummaryBinding;
import com.fittrackpro.app.ui.workout.adapter.PRAdapter;
import com.fittrackpro.app.ui.workout.adapter.SummaryExerciseAdapter;
import com.fittrackpro.app.util.Constants;
import com.fittrackpro.app.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * WorkoutSummaryFragment displays post-workout summary with PRs and stats.
 */
public class WorkoutSummaryFragment extends Fragment {

    private FragmentWorkoutSummaryBinding binding;
    private WorkoutSummaryViewModel viewModel;
    private PRAdapter prAdapter;
    private SummaryExerciseAdapter exerciseAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutSummaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WorkoutSummaryViewModel.class);

        setupRecyclerViews();
        setupObservers();
        setupListeners();

        // Get arguments and set workout data
        if (getArguments() != null) {
            String userId = getArguments().getString("userId");
            String programId = getArguments().getString("programId");
            String dayId = getArguments().getString("dayId");
            String workoutName = getArguments().getString("workoutName");
            long startTime = getArguments().getLong("startTime", 0);
            long endTime = getArguments().getLong("endTime", 0);
            
            // Get workout sets - handle serialization
            ArrayList<WorkoutSet> sets = (ArrayList<WorkoutSet>) getArguments().getSerializable("sets");
            if (sets == null) {
                sets = new ArrayList<>();
            }

            // Set workout data in ViewModel
            viewModel.setWorkoutData(userId, programId, dayId, workoutName, startTime, endTime, sets);
        }
    }

    private void setupRecyclerViews() {
        // Setup PR RecyclerView
        prAdapter = new PRAdapter();
        binding.recyclerPersonalRecords.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPersonalRecords.setAdapter(prAdapter);

        // Setup Exercise RecyclerView
        exerciseAdapter = new SummaryExerciseAdapter();
        binding.recyclerExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerExercises.setAdapter(exerciseAdapter);
    }

    private void setupObservers() {
        // Observe workout data
        viewModel.getWorkoutData().observe(getViewLifecycleOwner(), this::displayWorkoutStats);

        // Observe exercise sets
        viewModel.getExerciseSets().observe(getViewLifecycleOwner(), this::displayExerciseBreakdown);

        // Observe personal records
        viewModel.getNewPRs().observe(getViewLifecycleOwner(), this::displayPersonalRecords);

        // Observe saving completion
        viewModel.getSavingComplete().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(requireContext(), "Workout saved successfully!", Toast.LENGTH_SHORT).show();
                    // Navigate back to dashboard
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                } else {
                    Toast.makeText(requireContext(), "Failed to save workout", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupListeners() {
        binding.buttonShare.setOnClickListener(v -> shareWorkout());
        binding.buttonDone.setOnClickListener(v -> saveWorkoutAndNavigate());
    }

    /**
     * Display workout statistics in UI
     */
    private void displayWorkoutStats(CompletedWorkout workout) {
        if (workout == null) return;

        // Format and display duration
        binding.textDuration.setText(TimeUtils.formatDuration(workout.getDurationSeconds()));

        // Format and display volume
        binding.textVolume.setText(String.format("%.1f kg", workout.getTotalVolume()));

        // Display exercises count
        binding.textExercises.setText(String.valueOf(workout.getTotalExercises()));

        // Display sets count
        binding.textSets.setText(String.valueOf(workout.getTotalSets()));
    }

    /**
     * Display exercise breakdown
     */
    private void displayExerciseBreakdown(List<WorkoutSet> sets) {
        if (sets == null || sets.isEmpty()) return;

        exerciseAdapter.submitList(sets);
    }

    /**
     * Display personal records with highlighting
     */
    private void displayPersonalRecords(List<PersonalRecord> newPRs) {
        if (newPRs == null || newPRs.isEmpty()) {
            binding.cardPersonalRecords.setVisibility(View.GONE);
            return;
        }

        binding.cardPersonalRecords.setVisibility(View.VISIBLE);
        prAdapter.submitList(newPRs);
    }

    /**
     * Share workout using ACTION_SEND Intent
     */
    private void shareWorkout() {
        CompletedWorkout workout = viewModel.getWorkoutData().getValue();
        List<PersonalRecord> prs = viewModel.getNewPRs().getValue();

        if (workout == null) return;

        StringBuilder shareText = new StringBuilder();
        shareText.append("💪 Workout Complete!\n\n");
        shareText.append("Duration: ").append(TimeUtils.formatDuration(workout.getDurationSeconds())).append("\n");
        shareText.append("Total Volume: ").append(String.format("%.1f kg", workout.getTotalVolume())).append("\n");
        shareText.append("Exercises: ").append(workout.getTotalExercises()).append("\n");
        shareText.append("Sets: ").append(workout.getTotalSets()).append("\n");

        if (prs != null && !prs.isEmpty()) {
            shareText.append("\n🏆 New Personal Records:\n");
            for (PersonalRecord pr : prs) {
                shareText.append("- ").append(pr.getExerciseName())
                        .append(": ").append(formatPRForShare(pr)).append("\n");
            }
        }

        shareText.append("\n#FitTrackPro #WorkoutComplete");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        startActivity(Intent.createChooser(shareIntent, "Share Workout"));
    }

    /**
     * Format PR for sharing
     */
    private String formatPRForShare(PersonalRecord pr) {
        String recordType = pr.getRecordType();
        
        if (recordType.equals(Constants.PR_TYPE_WEIGHT)) {
            return String.format("%.1f kg × %d reps (Weight PR)", pr.getValue(), pr.getReps());
        } else if (recordType.equals(Constants.PR_TYPE_REPS)) {
            return String.format("%d reps at %.1f kg (Rep PR)", pr.getReps(), pr.getValue());
        } else if (recordType.equals(Constants.PR_TYPE_VOLUME)) {
            return String.format("%.1f kg (Volume PR)", pr.getValue());
        }
        
        return "";
    }

    /**
     * Save workout and navigate back to dashboard
     */
    private void saveWorkoutAndNavigate() {
        // Save workout (triggers observer which handles navigation)
        viewModel.saveWorkout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
