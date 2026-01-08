package com.fittrackpro. app.ui.workout;

import android.os.Bundle;
import android.view. LayoutInflater;
import android. view.View;
import android. view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx. fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentWorkoutHubBinding;
import com. fittrackpro.app. data.model.WorkoutProgram;
import com.fittrackpro.app.ui.workout.adapter.WorkoutProgramAdapter;
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
                // Preview program
                // TODO: Navigate to program preview
            }

            @Override
            public void onStartWorkoutClick(WorkoutProgram program) {
                // Duplicate preset and start
                viewModel.duplicatePreset(program. getProgramId()).observe(getViewLifecycleOwner(), newProgramId -> {
                    if (newProgramId != null) {
                        // Navigate to workout day selection
                        // TODO: Navigate to day selection
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
                // Edit program
                // TODO: Navigate to program editor
            }

            @Override
            public void onStartWorkoutClick(WorkoutProgram program) {
                // Navigate to workout day selection
                // TODO: Navigate to day selection
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}