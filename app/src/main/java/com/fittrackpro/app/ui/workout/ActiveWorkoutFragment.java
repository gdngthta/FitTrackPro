package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.os.SystemClock;
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
import com.fittrackpro.app.databinding.FragmentActiveWorkoutBinding;
import com.fittrackpro.app.ui.workout.adapter.ActiveExerciseAdapter;
import com.fittrackpro.app.util.Constants;
import com.fittrackpro.app.util.NotificationHelper;

import java.util.Locale;

/**
 * ActiveWorkoutFragment displays active workout with chronometer and exercise logging.
 *
 * Features:
 * - Chronometer showing elapsed time
 * - RecyclerView of exercises with expandable sets
 * - Log weight, reps, status for each set
 * - Real-time volume calculation
 * - Rest timer with countdown
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

                    // Start rest timer after completing a set (not for skipped sets)
                    if (status.equals(Constants.SET_STATUS_COMPLETED) || 
                        status.equals(Constants.SET_STATUS_MODIFIED)) {
                        viewModel.startRestTimer(Constants.DEFAULT_REST_TIMER_SECONDS);
                    }
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

        // Observe rest timer state
        viewModel.isTimerVisible().observe(getViewLifecycleOwner(), visible -> {
            binding.cardRestTimer.setVisibility(visible != null && visible ? View.VISIBLE : View.GONE);
        });

        viewModel.getRestTimeRemaining().observe(getViewLifecycleOwner(), seconds -> {
            if (seconds != null) {
                binding.textRestCountdown.setText(formatTime(seconds));
                
                // Update circular progress using actual total duration
                Integer totalSeconds = viewModel.getRestTimerTotal().getValue();
                if (totalSeconds != null && totalSeconds > 0) {
                    int progress = (int) ((seconds / (float) totalSeconds) * 100);
                    binding.progressRestTimer.setProgress(progress);
                }
            }
        });

        viewModel.isTimerPaused().observe(getViewLifecycleOwner(), paused -> {
            if (paused != null) {
                binding.buttonPauseRest.setText(paused ? R.string.resume : R.string.pause);
            }
        });

        viewModel.getRestCompleteNotification().observe(getViewLifecycleOwner(), shouldNotify -> {
            if (shouldNotify != null && shouldNotify) {
                // Trigger vibration and sound
                NotificationHelper.vibratePattern(requireContext(), 
                        Constants.VIBRATION_PATTERN_REST_COMPLETE);
                NotificationHelper.playNotificationSound(requireContext());
                viewModel.resetRestCompleteNotification();
            }
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

        // Rest timer controls
        binding.buttonPauseRest.setOnClickListener(v -> {
            viewModel.toggleRestTimer();
        });

        binding.buttonSkipRest.setOnClickListener(v -> {
            viewModel.skipRestTimer();
        });
    }

    /**
     * Format time in MM:SS format.
     *
     * @param seconds Time in seconds
     * @return Formatted string
     */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, secs);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding.chronometer != null) {
            binding.chronometer.stop();
        }
        // Cancel rest timer when view is destroyed
        viewModel.cancelRestTimer();
        binding = null;
    }
}