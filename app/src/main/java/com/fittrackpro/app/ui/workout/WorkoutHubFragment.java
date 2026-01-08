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
    private WorkoutProgramAdapter presetAdapter;
    private WorkoutProgramAdapter userAdapter;

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
        // Preset programs
        presetAdapter = new WorkoutProgramAdapter(new WorkoutProgramAdapter.OnProgramClickListener() {
            @Override
            public void onProgramClick(WorkoutProgram program) {
                // IMPLEMENTED: Navigate to program preview
                showProgramPreviewDialog(program);
            }

            @Override
            public void onStartWorkoutClick(WorkoutProgram program) {
                // Duplicate preset and start
                viewModel.duplicatePreset(program.getProgramId()).observe(getViewLifecycleOwner(), newProgramId -> {
                    if (newProgramId != null) {
                        // IMPLEMENTED: Navigate to workout day selection
                        navigateToWorkoutDaySelection(newProgramId, program.getProgramName());
                    }
                });
            }
        });
        binding.recyclerPresetPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPresetPrograms.setAdapter(presetAdapter);

        // User programs
        userAdapter = new WorkoutProgramAdapter(new WorkoutProgramAdapter.OnProgramClickListener() {
            @Override
            public void onProgramClick(WorkoutProgram program) {
                // IMPLEMENTED: Navigate to program editor
                navigateToProgramEditor(program.getProgramId());
            }

            @Override
            public void onStartWorkoutClick(WorkoutProgram program) {
                // IMPLEMENTED: Navigate to workout day selection
                navigateToWorkoutDaySelection(program.getProgramId(), program.getProgramName());
            }
        });
        binding.recyclerUserPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerUserPrograms.setAdapter(userAdapter);
    }

    private void setupObservers() {
        viewModel.getPresetPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                presetAdapter.submitList(programs);
            }
        });

        viewModel.getUserPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                userAdapter.submitList(programs);
                binding.emptyStateUserPrograms.setVisibility(programs.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setupListeners() {
        binding.fabAddRoutine.setOnClickListener(v -> {
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
            .setPositiveButton(R.string.start_workout, (dialog, which) -> {
                viewModel.duplicatePreset(program.getProgramId()).observe(getViewLifecycleOwner(), newProgramId -> {
                    if (newProgramId != null) {
                        navigateToWorkoutDaySelection(newProgramId, program.getProgramName());
                    }
                });
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

    private void navigateToWorkoutDaySelection(String programId, String programName) {
        Bundle args = new Bundle();
        args.putString("programId", programId);
        args.putString("programName", programName);
        Navigation.findNavController(binding.getRoot())
            .navigate(R.id.action_workoutHub_to_daySelection, args);
    }

    private void navigateToProgramEditor(String programId) {
        Bundle args = new Bundle();
        args.putString("programId", programId);
        args.putBoolean("editMode", true);
        Navigation.findNavController(binding.getRoot())
            .navigate(R.id.action_workoutHub_to_editProgram, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}