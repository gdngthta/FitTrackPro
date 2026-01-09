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
import java.util.Comparator;
import java.util.List;

/**
 * WorkoutHubFragment displays user's programs and recommended programs in a single scrollable view.
 */
public class WorkoutHubFragment extends Fragment {

    private FragmentWorkoutHubBinding binding;
    private WorkoutHubViewModel viewModel;
    private WorkoutProgramAdapter myProgramsAdapter;
    private WorkoutProgramAdapter recommendedProgramsAdapter;

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
        // My Programs Adapter
        myProgramsAdapter = new WorkoutProgramAdapter(new WorkoutProgramAdapter.OnProgramClickListener() {
            @Override
            public void onProgramClick(WorkoutProgram program) {
                navigateToProgramEditor(program.getProgramId());
            }

            @Override
            public void onStartWorkoutClick(WorkoutProgram program) {
                navigateToWorkoutDaySelection(program.getProgramId(), program.getProgramName());
            }
        });
        binding.recyclerMyPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMyPrograms.setAdapter(myProgramsAdapter);

        // Recommended Programs Adapter (sorted by difficulty)
        recommendedProgramsAdapter = new WorkoutProgramAdapter(new WorkoutProgramAdapter.OnProgramClickListener() {
            @Override
            public void onProgramClick(WorkoutProgram program) {
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
                binding.emptyStateMyPrograms.setVisibility(programs.isEmpty() ? View.VISIBLE : View.GONE);
                binding.recyclerMyPrograms.setVisibility(programs.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
        
        // Observe preset programs (for recommended section) and sort by difficulty
        viewModel.getPresetPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                List<WorkoutProgram> sortedPrograms = sortProgramsByDifficulty(programs);
                recommendedProgramsAdapter.submitList(sortedPrograms);
            }
        });
    }

    /**
     * Sort programs by difficulty level (Beginner, Intermediate, Advanced/Pro, Elite).
     */
    private List<WorkoutProgram> sortProgramsByDifficulty(List<WorkoutProgram> programs) {
        List<WorkoutProgram> sorted = new ArrayList<>(programs);
        sorted.sort(new Comparator<WorkoutProgram>() {
            @Override
            public int compare(WorkoutProgram p1, WorkoutProgram p2) {
                return getDifficultyOrder(p1.getDifficulty()) - getDifficultyOrder(p2.getDifficulty());
            }
        });
        return sorted;
    }

    /**
     * Get numeric order for difficulty levels for sorting.
     */
    private int getDifficultyOrder(String difficulty) {
        if (difficulty == null) return 0;
        switch (difficulty.toLowerCase()) {
            case "beginner": return 0;
            case "intermediate": return 1;
            case "advanced":
            case "pro": return 2;
            case "elite": return 3;
            default: return 0;
        }
    }

    private void setupListeners() {
        binding.buttonAddRoutine.setOnClickListener(v -> {
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
            .setPositiveButton("Add to My Programs", (dialog, which) -> {
                // Duplicate preset
                viewModel.duplicatePreset(program.getProgramId()).observe(getViewLifecycleOwner(), newProgramId -> {
                    if (newProgramId != null) {
                        // Show success message or navigate
                    }
                });
            })
            .setNegativeButton(android.R.string.cancel, null)
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