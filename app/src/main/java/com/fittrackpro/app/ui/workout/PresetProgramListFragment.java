package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation. Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget. LinearLayoutManager;

import com.fittrackpro.app.databinding.FragmentPresetProgramListBinding;
import com.fittrackpro.app.data.model.WorkoutProgram;
import com.fittrackpro. app.ui.workout.adapter.WorkoutProgramAdapter;
import com.google.firebase.auth.FirebaseAuth;

/**
 * PresetProgramListFragment displays all preset programs for duplication.
 */
public class PresetProgramListFragment extends Fragment {

    private FragmentPresetProgramListBinding binding;
    private WorkoutHubViewModel viewModel;
    private WorkoutProgramAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPresetProgramListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WorkoutHubViewModel.class);

        setupRecyclerView();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewModel.setUserId(userId);

        viewModel.getPresetPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                adapter. submitList(programs);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new WorkoutProgramAdapter(new WorkoutProgramAdapter.OnProgramClickListener() {
            @Override
            public void onProgramClick(WorkoutProgram program) {
                // Show program details dialog
                showProgramDetails(program);
            }

            @Override
            public void onStartWorkoutClick(WorkoutProgram program) {
                duplicateAndNavigate(program);
            }
        });

        binding. recyclerPresets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPresets.setAdapter(adapter);
    }

    private void showProgramDetails(WorkoutProgram program) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(program.getProgramName())
                .setMessage(program.getDescription() + "\n\n" +
                        "Difficulty: " + program.getDifficulty() + "\n" +
                        "Duration: " + program. getDurationWeeks() + " weeks\n" +
                        "Days per week: " + program.getDaysPerWeek())
                .setPositiveButton("Use This Program", (dialog, which) -> {
                    duplicateAndNavigate(program);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void duplicateAndNavigate(WorkoutProgram program) {
        binding.progressBar.setVisibility(View. VISIBLE);

        viewModel.duplicatePreset(program. getProgramId()).observe(getViewLifecycleOwner(), newProgramId -> {
            binding. progressBar.setVisibility(View.GONE);

            if (newProgramId != null) {
                Toast.makeText(requireContext(), "Program added to My Programs!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).navigateUp();
            } else {
                Toast.makeText(requireContext(), "Failed to add program", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}