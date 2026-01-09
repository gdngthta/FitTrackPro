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
import androidx.navigation.Navigation;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentCreateCustomProgramBinding;
import com.google.firebase.auth.FirebaseAuth;

/**
 * CreateCustomProgramFragment allows creating a new custom workout program.
 */
public class CreateCustomProgramFragment extends Fragment {

    private FragmentCreateCustomProgramBinding binding;
    private WorkoutHubViewModel viewModel;
    private int selectedDaysPerWeek = 4; // Default to 4 days

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

        setupFrequencyButtons();
        setupListeners();
        
        // Select button 4 by default
        selectFrequencyButton(binding.button4, 4);
    }

    private void setupFrequencyButtons() {
        View.OnClickListener frequencyClickListener = v -> {
            int days = Integer.parseInt(((com.google.android.material.button.MaterialButton) v).getText().toString());
            selectFrequencyButton((com.google.android.material.button.MaterialButton) v, days);
        };

        binding.button1.setOnClickListener(frequencyClickListener);
        binding.button2.setOnClickListener(frequencyClickListener);
        binding.button3.setOnClickListener(frequencyClickListener);
        binding.button4.setOnClickListener(frequencyClickListener);
        binding.button5.setOnClickListener(frequencyClickListener);
        binding.button6.setOnClickListener(frequencyClickListener);
        binding.button7.setOnClickListener(frequencyClickListener);
    }

    private void selectFrequencyButton(com.google.android.material.button.MaterialButton selectedButton, int days) {
        // Reset all buttons to default state
        resetButton(binding.button1);
        resetButton(binding.button2);
        resetButton(binding.button3);
        resetButton(binding.button4);
        resetButton(binding.button5);
        resetButton(binding.button6);
        resetButton(binding.button7);

        // Highlight selected button
        selectedButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        selectedDaysPerWeek = days;
    }

    private void resetButton(com.google.android.material.button.MaterialButton button) {
        button.setBackgroundColor(getResources().getColor(R.color.colorSurface, null));
    }

    private void setupListeners() {
        binding.textBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        binding.buttonCreateProgram.setOnClickListener(v -> {
            String programName = binding.editProgramName.getText().toString().trim();

            if (programName.isEmpty()) {
                binding.editProgramName.setError(getString(R.string.program_name_required));
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonCreateProgram.setEnabled(false);

            // Create program with just name and frequency
            viewModel.createCustomProgram(programName, "", "beginner", 12, selectedDaysPerWeek)
                    .observe(getViewLifecycleOwner(), programId -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.buttonCreateProgram.setEnabled(true);

                        if (programId != null) {
                            Toast.makeText(requireContext(), getString(R.string.program_created), Toast.LENGTH_SHORT).show();

                            // Navigate to workout day creation
                            Bundle args = new Bundle();
                            args.putString("programId", programId);
                            args.putInt("daysPerWeek", selectedDaysPerWeek);
                            Navigation.findNavController(v).navigate(R.id.action_createCustom_to_editWorkoutDays, args);
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.failed_to_create_program), Toast.LENGTH_SHORT).show();
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