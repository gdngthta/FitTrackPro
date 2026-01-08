package com.fittrackpro.app.ui.diet;

import android.os.Bundle;
import android.view. LayoutInflater;
import android. view.View;
import android. view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget. Toast;
import androidx.annotation.NonNull;
import androidx.annotation. Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fittrackpro.app.databinding.FragmentSetupNutritionProfileBinding;
import com.fittrackpro.app.util.Constants;
import com.google.firebase.auth.FirebaseAuth;

/**
 * SetupNutritionProfileFragment collects user data to calculate TDEE and macros.
 */
public class SetupNutritionProfileFragment extends Fragment {

    private FragmentSetupNutritionProfileBinding binding;
    private DietViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSetupNutritionProfileBinding. inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DietViewModel.class);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewModel.setUserId(userId);

        setupSpinners();
        setupListeners();
    }

    private void setupSpinners() {
        // Gender
        String[] genders = {"Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout. simple_spinner_dropdown_item,
                genders
        );
        binding.spinnerGender.setAdapter(genderAdapter);

        // Activity level
        String[] activities = {
                "Sedentary (little/no exercise)",
                "Light (1-3 days/week)",
                "Moderate (3-5 days/week)",
                "Active (6-7 days/week)",
                "Very Active (athlete)"
        };
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                activities
        );
        binding.spinnerActivityLevel.setAdapter(activityAdapter);

        // Goal
        String[] goals = {
                "Lose weight",
                "Maintain weight",
                "Gain weight"
        };
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                goals
        );
        binding.spinnerGoal.setAdapter(goalAdapter);
    }

    private void setupListeners() {
        binding.buttonCalculate.setOnClickListener(v -> {
            if (validateInputs()) {
                calculateAndSave();
            }
        });
    }

    private boolean validateInputs() {
        String weightStr = binding.editWeight.getText().toString().trim();
        String heightStr = binding.editHeight.getText().toString().trim();
        String ageStr = binding.editAge. getText().toString().trim();

        if (weightStr.isEmpty()) {
            binding.editWeight. setError("Weight is required");
            return false;
        }

        if (heightStr.isEmpty()) {
            binding.editHeight.setError("Height is required");
            return false;
        }

        if (ageStr.isEmpty()) {
            binding.editAge.setError("Age is required");
            return false;
        }

        return true;
    }

    private void calculateAndSave() {
        double weight = Double.parseDouble(binding.editWeight.getText().toString().trim());
        double height = Double.parseDouble(binding.editHeight.getText().toString().trim());
        int age = Integer.parseInt(binding. editAge.getText().toString().trim());

        String gender = binding.spinnerGender. getSelectedItemPosition() == 0 ?
                Constants.GENDER_MALE :  Constants.GENDER_FEMALE;

        String activityLevel;
        switch (binding.spinnerActivityLevel.getSelectedItemPosition()) {
            case 0: activityLevel = Constants.ACTIVITY_SEDENTARY; break;
            case 1: activityLevel = Constants.ACTIVITY_LIGHT; break;
            case 2: activityLevel = Constants. ACTIVITY_MODERATE; break;
            case 3: activityLevel = Constants.ACTIVITY_ACTIVE; break;
            case 4: activityLevel = Constants. ACTIVITY_VERY_ACTIVE; break;
            default: activityLevel = Constants.ACTIVITY_MODERATE;
        }

        String goal;
        switch (binding.spinnerGoal.getSelectedItemPosition()) {
            case 0: goal = Constants.GOAL_LOSE; break;
            case 1: goal = Constants.GOAL_MAINTAIN; break;
            case 2: goal = Constants.GOAL_GAIN; break;
            default: goal = Constants.GOAL_MAINTAIN;
        }

        binding.progressBar.setVisibility(View. VISIBLE);
        binding.buttonCalculate.setEnabled(false);

        viewModel.createNutritionProfile(weight, height, age, gender, activityLevel, goal)
                .observe(getViewLifecycleOwner(), success -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.buttonCalculate.setEnabled(true);

                    if (success != null && success) {
                        Toast.makeText(requireContext(), "Nutrition profile created!", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    } else {
                        Toast.makeText(requireContext(), "Failed to create profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}