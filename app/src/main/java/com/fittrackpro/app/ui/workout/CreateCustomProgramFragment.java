package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget. Toast;
import androidx.annotation.NonNull;
import androidx.annotation. Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding. FragmentCreateCustomProgramBinding;
import com.fittrackpro.app.util.Constants;
import com.google.firebase.auth.FirebaseAuth;

/**
 * CreateCustomProgramFragment allows creating a new custom workout program.
 */
public class CreateCustomProgramFragment extends Fragment {

    private FragmentCreateCustomProgramBinding binding;
    private WorkoutHubViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateCustomProgramBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WorkoutHubViewModel.class);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewModel.setUserId(userId);

        setupSpinners();
        setupListeners();
    }

    private void setupSpinners() {
        // Difficulty spinner
        String[] difficulties = {
                Constants.DIFFICULTY_BEGINNER,
                Constants.DIFFICULTY_INTERMEDIATE,
                Constants. DIFFICULTY_ADVANCED
        };
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(
                requireContext(),
                android. R.layout.simple_spinner_dropdown_item,
                difficulties
        );
        binding.spinnerDifficulty.setAdapter(difficultyAdapter);

        // Duration spinner (4-16 weeks)
        String[] durations = new String[13];
        for (int i = 0; i < 13; i++) {
            durations[i] = (i + 4) + " weeks";
        }
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                durations
        );
        binding.spinnerDuration.setAdapter(durationAdapter);

        // Days per week spinner (3-7 days)
        String[] days = new String[5];
        for (int i = 0; i < 5; i++) {
            days[i] = (i + 3) + " days/week";
        }
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R. layout.simple_spinner_dropdown_item,
                days
        );
        binding.spinnerDaysPerWeek.setAdapter(daysAdapter);
    }

    private void setupListeners() {
        binding.buttonCreateProgram.setOnClickListener(v -> {
            String programName = binding.editProgramName.getText().toString().trim();
            String description = binding.editDescription.getText().toString().trim();

            if (programName.isEmpty()) {
                binding.editProgramName.setError("Program name is required");
                return;
            }

            String difficulty = binding.spinnerDifficulty.getSelectedItem().toString();
            int durationWeeks = binding.spinnerDuration.getSelectedItemPosition() + 4;
            int daysPerWeek = binding. spinnerDaysPerWeek. getSelectedItemPosition() + 3;

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonCreateProgram.setEnabled(false);

            viewModel.createCustomProgram(programName, description, difficulty, durationWeeks, daysPerWeek)
                    .observe(getViewLifecycleOwner(), programId -> {
                        binding. progressBar.setVisibility(View.GONE);
                        binding.buttonCreateProgram.setEnabled(true);

                        if (programId != null) {
                            Toast.makeText(requireContext(), "Program created!", Toast.LENGTH_SHORT).show();

                            // Navigate to workout day creation
                            Bundle args = new Bundle();
                            args.putString("programId", programId);
                            args.putInt("daysPerWeek", daysPerWeek);
                            Navigation.findNavController(v).navigate(R.id.action_createCustom_to_editWorkoutDays, args);
                        } else {
                            Toast.makeText(requireContext(), "Failed to create program", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}