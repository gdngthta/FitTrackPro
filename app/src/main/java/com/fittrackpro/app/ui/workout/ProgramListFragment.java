package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.databinding.FragmentProgramListBinding;
import com.fittrackpro.app.data.model.WorkoutProgram;
import com.fittrackpro.app.ui.workout.adapter.ProgramAdapter;

/**
 * Fragment that displays a list of programs filtered by difficulty
 */
public class ProgramListFragment extends Fragment {

    private static final String ARG_DIFFICULTY = "difficulty";

    private FragmentProgramListBinding binding;
    private WorkoutHubViewModel viewModel;
    private ProgramAdapter programAdapter;
    private String difficulty;

    public static ProgramListFragment newInstance(String difficulty) {
        ProgramListFragment fragment = new ProgramListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DIFFICULTY, difficulty);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            difficulty = getArguments().getString(ARG_DIFFICULTY, "Beginner");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProgramListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(WorkoutHubViewModel.class);

        setupRecyclerView();
        observePrograms();
    }

    private void setupRecyclerView() {
        programAdapter = new ProgramAdapter(program -> {
            // Handle program click - start program
            viewModel.startProgram(program).observe(getViewLifecycleOwner(), success -> {
                if (success != null && success) {
                    Toast.makeText(requireContext(), 
                        "Program added successfully!", 
                        Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), 
                        "Failed to add program. Please try again.", 
                        Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.recyclerPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPrograms.setAdapter(programAdapter);
    }

    private void observePrograms() {
        // Observe preset programs filtered by difficulty
        viewModel.getPresetPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                // Filter by difficulty
                java.util.List<WorkoutProgram> filteredPrograms = new java.util.ArrayList<>();
                for (WorkoutProgram program : programs) {
                    if (difficulty.equalsIgnoreCase(program.getDifficulty())) {
                        filteredPrograms.add(program);
                    }
                }
                programAdapter.submitList(filteredPrograms);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
