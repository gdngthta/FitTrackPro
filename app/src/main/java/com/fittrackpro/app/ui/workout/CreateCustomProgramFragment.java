package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentCreateCustomProgramBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

/**
 * CreateCustomProgramFragment allows creating a new custom workout program.
 * Uses frequency selector buttons (1-7) instead of spinners.
 */
public class CreateCustomProgramFragment extends Fragment {

    private FragmentCreateCustomProgramBinding binding;
    private WorkoutHubViewModel viewModel;
    private int selectedFrequency = 3; // Default to 3 days per week
    private MaterialButton[] frequencyButtons;

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

        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not logged in, return to auth
            requireActivity().finish();
            return;
        }
        String userId = currentUser.getUid();
        viewModel.setUserId(userId);

        setupFrequencySelector();
        setupListeners();
    }

    private void setupFrequencySelector() {
        // Initialize frequency buttons array
        frequencyButtons = new MaterialButton[]{
                binding.buttonFreq1,
                binding.buttonFreq2,
                binding.buttonFreq3,
                binding.buttonFreq4,
                binding.buttonFreq5,
                binding.buttonFreq6,
                binding.buttonFreq7
        };

        // Set click listeners for all frequency buttons
        for (int i = 0; i < frequencyButtons.length; i++) {
            final int frequency = i + 1;
            frequencyButtons[i].setOnClickListener(v -> selectFrequency(frequency));
        }

        // Set default selection (3 days per week)
        selectFrequency(3);
    }

    private void selectFrequency(int frequency) {
        selectedFrequency = frequency;

        // Update button states
        for (int i = 0; i < frequencyButtons.length; i++) {
            MaterialButton button = frequencyButtons[i];
            if (i + 1 == frequency) {
                // Selected button - filled style
                button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_primary));
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_onPrimary));
            } else {
                // Unselected button - outlined style
                button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_surface));
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_onSurface));
            }
        }
    }

    private void setupListeners() {
        binding.buttonContinue.setOnClickListener(v -> {
            String programName = binding.editProgramName.getText().toString().trim();

            if (programName.isEmpty()) {
                binding.editProgramName.setError("Program name is required");
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonContinue.setEnabled(false);

            // Create program with simplified parameters
            String description = "";  // Description optional for now
            String difficulty = "beginner";  // Default difficulty
            int durationWeeks = 12;  // Default 12 weeks

            viewModel.createCustomProgram(programName, description, difficulty, durationWeeks, selectedFrequency)
                    .observe(getViewLifecycleOwner(), programId -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.buttonContinue.setEnabled(true);

                        if (programId != null) {
                            Toast.makeText(requireContext(), "Program created!", Toast.LENGTH_SHORT).show();

                            // Navigate to workout day creation
                            Bundle args = new Bundle();
                            args.putString("programId", programId);
                            args.putInt("daysPerWeek", selectedFrequency);
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