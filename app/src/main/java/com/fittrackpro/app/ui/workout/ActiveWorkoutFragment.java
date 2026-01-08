package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation. Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.databinding.FragmentActiveWorkoutBinding;
import com.fittrackpro. app.ui.workout.adapter. ActiveExerciseAdapter;

/**
 * ActiveWorkoutFragment displays active workout with chronometer and exercise logging.
 *
 * Features:
 * - Chronometer showing elapsed time
 * - RecyclerView of exercises with expandable sets
 * - Log weight, reps, status for each set
 * - Real-time volume calculation
 * - Finish button to save workout
 */
public class ActiveWorkoutFragment extends Fragment {

    private FragmentActiveWorkoutBinding binding;
    private ActiveWorkoutViewModel viewModel;
    private ActiveExerciseAdapter exerciseAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActiveWorkoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ActiveWorkoutViewModel.class);

        // Get arguments from navigation
        Bundle args = getArguments();
        if (args != null) {
            String userId = args.getString("userId");
            String programId = args.getString("programId");
            String dayId = args.getString("dayId");
            String workoutName = args.getString("workoutName");

            viewModel.initWorkout(userId, programId, dayId, workoutName);
            binding.textWorkoutTitle.setText(workoutName);
        }

        setupRecyclerView();
        setupObservers();
        setupListeners();

        // Start workout automatically
        viewModel.startWorkout();
    }

    private void setupRecyclerView() {
        exerciseAdapter = new ActiveExerciseAdapter(
                (exerciseName, weight, reps, status) -> {
                    // Callback when user logs a set
                    viewModel.addSet(exerciseName, weight, reps, status);
                }
        );
        binding.recyclerExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerExercises.setAdapter(exerciseAdapter);
    }

    private void setupObservers() {
        // Observe exercises
        viewModel.getExercises().observe(getViewLifecycleOwner(), exercises -> {
            if (exercises != null) {
                exerciseAdapter.submitList(exercises);
            }
        });

        // Observe start time to start chronometer
        viewModel.getStartTime().observe(getViewLifecycleOwner(), startTime -> {
            if (startTime != null) {
                binding.chronometer.setBase(SystemClock.elapsedRealtime() -
                        (System.currentTimeMillis() - startTime));
                binding.chronometer.start();
            }
        });

        // Observe totals
        viewModel.getTotalVolume().observe(getViewLifecycleOwner(), volume -> {
            binding.textTotalVolume.setText(String.format("%.1f kg", volume != null ? volume : 0.0));
        });

        viewModel.getCompletedSets().observe(getViewLifecycleOwner(), sets -> {
            binding.textCompletedSets.setText(String.valueOf(sets != null ? sets : 0));
        });
    }

    private void setupListeners() {
        binding.buttonFinishWorkout.setOnClickListener(v -> {
            binding.chronometer.stop();

            viewModel.finishWorkout().observe(getViewLifecycleOwner(), success -> {
                if (success != null && success) {
                    Toast.makeText(requireContext(), "Workout saved!", Toast.LENGTH_SHORT).show();
                    // Navigate to workout summary
                    Navigation.findNavController(v).navigateUp();
                } else {
                    Toast.makeText(requireContext(), "Failed to save workout", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding. chronometer != null) {
            binding.chronometer.stop();
        }
        binding = null;
    }
}