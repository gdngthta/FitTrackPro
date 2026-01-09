package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentWorkoutHubBinding;
import com.fittrackpro.app.data.model.WorkoutProgram;
import com.fittrackpro.app.ui.workout.adapter.WorkoutProgramAdapter;
import com.fittrackpro.app.ui.workout.adapter.RecommendedProgramsAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

/**
 * WorkoutHubFragment displays user programs and recommended programs.
 */
public class WorkoutHubFragment extends Fragment {

    private FragmentWorkoutHubBinding binding;
    private WorkoutHubViewModel viewModel;
    private WorkoutProgramAdapter myProgramsAdapter;
    private RecommendedProgramsAdapter recommendedProgramsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutHubBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WorkoutHubViewModel.class);

        setupRecyclerViews();
        setupObservers();
        setupListeners();

        // Get current user ID
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not logged in, return to auth
            requireActivity().finish();
            return;
        }
        String userId = currentUser.getUid();
        viewModel.setUserId(userId);
    }

    private void setupRecyclerViews() {
        // My Programs adapter
        myProgramsAdapter = new WorkoutProgramAdapter(new WorkoutProgramAdapter.OnProgramClickListener() {
            @Override
            public void onProgramClick(WorkoutProgram program) {
                // Navigate to program editor for user programs
                navigateToProgramEditor(program.getProgramId());
            }

            @Override
            public void onStartWorkoutClick(WorkoutProgram program) {
                // Start user program directly
                navigateToWorkoutDaySelection(program.getProgramId(), program.getProgramName());
            }
        });
        binding.recyclerMyPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMyPrograms.setAdapter(myProgramsAdapter);
        
        // Recommended Programs adapter
        recommendedProgramsAdapter = new RecommendedProgramsAdapter(new RecommendedProgramsAdapter.OnProgramClickListener() {
            @Override
            public void onProgramClick(WorkoutProgram program) {
                // Show program preview for presets
                showProgramPreviewDialog(program);
            }

            @Override
            public void onStartWorkoutClick(WorkoutProgram program) {
                // Duplicate preset and start
                viewModel.duplicatePreset(program.getProgramId()).observe(getViewLifecycleOwner(), newProgramId -> {
                    if (newProgramId != null) {
                        navigateToWorkoutDaySelection(newProgramId, program.getProgramName());
                    }
                });
            }
        });
        binding.recyclerRecommendedPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerRecommendedPrograms.setAdapter(recommendedProgramsAdapter);
    }

    private void setupObservers() {
        // Observe user programs
        viewModel.getUserPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                myProgramsAdapter.submitList(programs);
                
                // Show/hide empty state
                if (programs.isEmpty()) {
                    binding.recyclerMyPrograms.setVisibility(View.GONE);
                    binding.cardEmptyState.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerMyPrograms.setVisibility(View.VISIBLE);
                    binding.cardEmptyState.setVisibility(View.GONE);
                }
            }
        });
        
        // Observe preset programs
        viewModel.getPresetPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                recommendedProgramsAdapter.setPrograms(programs);
            }
        });
    }

    private void setupListeners() {
        // Settings button
        binding.buttonSettings.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_to_settings);
        });
        
        // Add Routine button
        binding.buttonAddRoutine.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_to_addRoutine);
        });
        
        // Create First Program button
        binding.buttonCreateFirstProgram.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_to_addRoutine);
        });
    }

    private void showProgramPreviewDialog(WorkoutProgram program) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle(program.getProgramName())
            .setMessage(program.getDescription() + "\n\n" +
                    "Difficulty: " + program.getDifficulty() + "\n" +
                    "Duration: " + program.getDurationWeeks() + " weeks\n" +
                    "Days per week: " + program.getDaysPerWeek())
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }

    private void navigateToWorkoutDaySelection(String programId, String programName) {
        // TODO: Navigate to day selection when the destination exists
        // For now, navigate to active workout directly
        Navigation.findNavController(binding.getRoot())
            .navigate(R.id.action_to_activeWorkout);
    }

    private void navigateToProgramEditor(String programId) {
        // TODO: Navigate to program editor when the destination exists
        // For now, navigate to edit workout day
        Bundle args = new Bundle();
        args.putString("programId", programId);
        args.putInt("daysPerWeek", 4);
        Navigation.findNavController(binding.getRoot())
            .navigate(R.id.editWorkoutDayFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}