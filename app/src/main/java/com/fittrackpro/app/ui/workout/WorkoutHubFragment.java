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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

/**
 * WorkoutHubFragment displays recommended and user programs.
 */
public class WorkoutHubFragment extends Fragment {

    private FragmentWorkoutHubBinding binding;
    private WorkoutHubViewModel viewModel;
    private WorkoutProgramAdapter programAdapter;
    private boolean showingAllPrograms = false;

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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewModel.setUserId(userId);
    }

    private void setupRecyclerViews() {
        // Single adapter that can show either active or all programs
        programAdapter = new WorkoutProgramAdapter(new WorkoutProgramAdapter.OnProgramClickListener() {
            @Override
            public void onProgramClick(WorkoutProgram program) {
                // Show program preview for presets, navigate to editor for user programs
                if (program.isPreset()) {
                    showProgramPreviewDialog(program);
                } else {
                    navigateToProgramEditor(program.getProgramId());
                }
            }

            @Override
            public void onStartWorkoutClick(WorkoutProgram program) {
                if (program.isPreset()) {
                    // Duplicate preset and start
                    viewModel.duplicatePreset(program.getProgramId()).observe(getViewLifecycleOwner(), newProgramId -> {
                        if (newProgramId != null) {
                            navigateToWorkoutDaySelection(newProgramId, program.getProgramName());
                        }
                    });
                } else {
                    // Start user program directly
                    navigateToWorkoutDaySelection(program.getProgramId(), program.getProgramName());
                }
            }
        });
        binding.recyclerPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPrograms.setAdapter(programAdapter);
    }

    private void setupObservers() {
        // Observe based on current tab
        viewModel.getUserPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null && !showingAllPrograms) {
                programAdapter.submitList(programs);
                binding.emptyState.setVisibility(programs.isEmpty() ? View.VISIBLE : View.GONE);
                binding.recyclerPrograms.setVisibility(programs.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
        
        viewModel.getPresetPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null && showingAllPrograms) {
                programAdapter.submitList(programs);
                binding.emptyState.setVisibility(View.GONE);
                binding.recyclerPrograms.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupListeners() {
        binding.fabAddRoutine.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_to_addRoutine);
        });
        
        binding.buttonAddFirstProgram.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_to_addRoutine);
        });
        
        // Tab selection
        binding.tabLayout.addOnTabSelectedListener(new com.google.android.material.tabs.TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(com.google.android.material.tabs.TabLayout.Tab tab) {
                showingAllPrograms = tab.getPosition() == 1;
                if (showingAllPrograms) {
                    viewModel.getPresetPrograms().observe(getViewLifecycleOwner(), programs -> {
                        if (programs != null) {
                            programAdapter.submitList(programs);
                            binding.emptyState.setVisibility(View.GONE);
                            binding.recyclerPrograms.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    viewModel.getUserPrograms().observe(getViewLifecycleOwner(), programs -> {
                        if (programs != null) {
                            programAdapter.submitList(programs);
                            binding.emptyState.setVisibility(programs.isEmpty() ? View.VISIBLE : View.GONE);
                            binding.recyclerPrograms.setVisibility(programs.isEmpty() ? View.GONE : View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(com.google.android.material.tabs.TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(com.google.android.material.tabs.TabLayout.Tab tab) {}
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