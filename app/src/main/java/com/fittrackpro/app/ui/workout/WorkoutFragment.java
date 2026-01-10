package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.util.Log;
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
import com.fittrackpro.app.databinding.FragmentWorkoutBinding;
import com.fittrackpro.app.data.model.WorkoutProgram;
import com.fittrackpro.app.ui.workout.adapter.MyProgramSimpleAdapter;
import com.fittrackpro.app.ui.workout.adapter.RecommendedProgramAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Workout fragment with single-page design showing My Programs and Recommended Programs
 */
public class WorkoutFragment extends Fragment {

    private FragmentWorkoutBinding binding;
    private WorkoutHubViewModel viewModel;
    
    private MyProgramSimpleAdapter myProgramsAdapter;
    private RecommendedProgramAdapter beginnerAdapter;
    private RecommendedProgramAdapter intermediateAdapter;
    private RecommendedProgramAdapter proAdapter;
    private RecommendedProgramAdapter eliteAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(WorkoutHubViewModel.class);

        // Set user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                        FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            viewModel.setUserId(userId);
        }

        // Initialize preset programs
        viewModel.initializePresetPrograms();

        setupRecyclerViews();
        setupListeners();
        observePrograms();
    }

    private void setupRecyclerViews() {
        // My Programs
        myProgramsAdapter = new MyProgramSimpleAdapter(program -> {
            // Handle My Program click - start workout
            Toast.makeText(requireContext(), "Starting: " + program.getProgramName(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to active workout or program details
        });
        binding.recyclerMyPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMyPrograms.setAdapter(myProgramsAdapter);

        // Beginner Programs
        beginnerAdapter = new RecommendedProgramAdapter(this::handlePresetProgramClick);
        binding.recyclerBeginner.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBeginner.setAdapter(beginnerAdapter);

        // Intermediate Programs
        intermediateAdapter = new RecommendedProgramAdapter(this::handlePresetProgramClick);
        binding.recyclerIntermediate.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerIntermediate.setAdapter(intermediateAdapter);

        // Pro Programs
        proAdapter = new RecommendedProgramAdapter(this::handlePresetProgramClick);
        binding.recyclerPro.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPro.setAdapter(proAdapter);

        // Elite Programs
        eliteAdapter = new RecommendedProgramAdapter(this::handlePresetProgramClick);
        binding.recyclerElite.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerElite.setAdapter(eliteAdapter);
    }

    private void setupListeners() {
        // Add Routine button
        binding.buttonAddRoutine.setOnClickListener(v -> {
            // Navigate to Add Routine screen
            Navigation.findNavController(v).navigate(R.id.addRoutineFragment);
        });

        // Settings icon
        binding.iconSettings.setOnClickListener(v -> {
            // Settings functionality can be added here if needed
            // For now, settings are accessed from Profile tab
        });
    }

    private void observePrograms() {
        // Observe user's programs
        viewModel.getUserPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                myProgramsAdapter.submitList(programs);
                Log.d("WorkoutFragment", "Loaded " + programs.size() + " user programs");
            }
        });

        // Observe preset programs and filter by difficulty
        viewModel.getPresetPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                Log.d("WorkoutFragment", "Loaded " + programs.size() + " preset programs");
                
                // Filter by difficulty
                List<WorkoutProgram> beginnerPrograms = new ArrayList<>();
                List<WorkoutProgram> intermediatePrograms = new ArrayList<>();
                List<WorkoutProgram> proPrograms = new ArrayList<>();
                List<WorkoutProgram> elitePrograms = new ArrayList<>();

                for (WorkoutProgram program : programs) {
                    String difficulty = program.getDifficulty();
                    if (difficulty != null) {
                        switch (difficulty) {
                            case "Beginner":
                                beginnerPrograms.add(program);
                                break;
                            case "Intermediate":
                                intermediatePrograms.add(program);
                                break;
                            case "Pro":
                                proPrograms.add(program);
                                break;
                            case "Elite":
                                elitePrograms.add(program);
                                break;
                        }
                    }
                }

                beginnerAdapter.submitList(beginnerPrograms);
                intermediateAdapter.submitList(intermediatePrograms);
                proAdapter.submitList(proPrograms);
                eliteAdapter.submitList(elitePrograms);

                Log.d("WorkoutFragment", "Beginner: " + beginnerPrograms.size() + 
                      ", Intermediate: " + intermediatePrograms.size() +
                      ", Pro: " + proPrograms.size() +
                      ", Elite: " + elitePrograms.size());
            }
        });
    }

    private void handlePresetProgramClick(WorkoutProgram program) {
        // Add preset program to user
        viewModel.startProgram(program).observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), 
                    "Program added to My Programs!", 
                    Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), 
                    "Failed to add program. Please try again.", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
