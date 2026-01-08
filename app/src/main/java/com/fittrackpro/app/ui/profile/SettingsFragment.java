package com.fittrackpro.app.ui. profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fittrackpro.app.databinding.FragmentSettingsBinding;
import com. fittrackpro.app.util.Constants;

/**
 * SettingsFragment manages app settings and preferences.
 */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding. inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, android.content.Context.MODE_PRIVATE);

        loadSettings();
        setupListeners();
    }

    private void loadSettings() {
        // Weight unit
        String weightUnit = prefs.getString(Constants.PREF_WEIGHT_UNIT, Constants. UNIT_KG);
        binding.switchWeightUnit.setChecked(weightUnit.equals(Constants. UNIT_LB));
        binding.textWeightUnitStatus.setText(weightUnit. equals(Constants.UNIT_KG) ? "Kilograms (kg)" : "Pounds (lb)");

        // Show equivalence
        boolean showEquivalence = prefs.getBoolean(Constants.PREF_SHOW_EQUIVALENCE, true);
        binding.switchShowEquivalence. setChecked(showEquivalence);
    }

    private void setupListeners() {
        binding.switchWeightUnit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String unit = isChecked ? Constants. UNIT_LB : Constants.UNIT_KG;
            prefs.edit().putString(Constants.PREF_WEIGHT_UNIT, unit).apply();
            binding. textWeightUnitStatus.setText(isChecked ? "Pounds (lb)" : "Kilograms (kg)");
        });

        binding.switchShowEquivalence.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(Constants.PREF_SHOW_EQUIVALENCE, isChecked).apply();
        });

        binding.buttonEditNutrition.setOnClickListener(v -> {
            // Navigate to nutrition profile editor
            androidx.navigation.Navigation.findNavController(v)
                    .navigate(com.fittrackpro.app.R.id.action_settings_to_setupNutritionProfile);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}