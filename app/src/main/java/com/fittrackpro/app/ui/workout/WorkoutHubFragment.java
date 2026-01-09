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

import java.util.ArrayList;
import java.util.List;

/**
 * WorkoutHubFragment displays recommended and user programs.
 */
public class WorkoutHubFragment extends Fragment {

    private FragmentWorkoutHubBinding binding;
    private WorkoutHubViewModel viewModel;
    private WorkoutProgramAdapter myProgramsAdapter;
    private WorkoutProgramAdapter beginnerAdapter;
    private WorkoutProgramAdapter intermediateAdapter;
    private WorkoutProgramAdapter proAdapter;
    private WorkoutProgramAdapter eliteAdapter;

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
        WorkoutProgramAdapter.OnProgramClickListener listener = new WorkoutProgramAdapter.OnProgramClickListener() {
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
        };

        // My Programs
        myProgramsAdapter = new WorkoutProgramAdapter(listener);
        binding.recyclerMyPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMyPrograms.setAdapter(myProgramsAdapter);

        // Recommended Programs by difficulty
        beginnerAdapter = new WorkoutProgramAdapter(listener);
        binding.recyclerBeginnerPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBeginnerPrograms.setAdapter(beginnerAdapter);

        intermediateAdapter = new WorkoutProgramAdapter(listener);
        binding.recyclerIntermediatePrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerIntermediatePrograms.setAdapter(intermediateAdapter);

        proAdapter = new WorkoutProgramAdapter(listener);
        binding.recyclerProPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerProPrograms.setAdapter(proAdapter);

        eliteAdapter = new WorkoutProgramAdapter(listener);
        binding.recyclerElitePrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerElitePrograms.setAdapter(eliteAdapter);
    }

    private void setupObservers() {
        // Observe user programs
        viewModel.getUserPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                myProgramsAdapter.submitList(programs);
            }
        });
        
        // Observe preset programs and filter by difficulty
        viewModel.getPresetPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                List<WorkoutProgram> beginner = new ArrayList<>();
                List<WorkoutProgram> intermediate = new ArrayList<>();
                List<WorkoutProgram> pro = new ArrayList<>();
                List<WorkoutProgram> elite = new ArrayList<>();

                for (WorkoutProgram program : programs) {
                    String difficulty = program.getDifficulty();
                    if (difficulty == null) continue;
                    
                    switch (difficulty.toLowerCase()) {
                        case "beginner":
                            beginner.add(program);
                            break;
                        case "intermediate":
                            intermediate.add(program);
                            break;
                        case "pro":
                            pro.add(program);
                            break;
                        case "elite":
                            elite.add(program);
                            break;
                    }
                }

                beginnerAdapter.submitList(beginner);
                intermediateAdapter.submitList(intermediate);
                proAdapter.submitList(pro);
                eliteAdapter.submitList(elite);
            }
        });
    }

    private void setupListeners() {
        binding.buttonAddRoutine.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_to_addRoutine);
        });
        
        binding.iconSettings.setOnClickListener(v -> {
            // Navigate to settings if needed
            // Navigation.findNavController(v).navigate(R.id.action_to_settings);
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