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

import com.fittrackpro.app.R;
import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data.model.ProgramExercise;
import com.fittrackpro.app.data.repository.WorkoutRepository;
import com.fittrackpro.app.databinding.FragmentExerciseConfigurationBinding;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * ExerciseConfigurationFragment allows configuring exercise parameters.
 */
public class ExerciseConfigurationFragment extends Fragment {

    private FragmentExerciseConfigurationBinding binding;
    private String programId;
    private String dayId;
    private String exerciseName;
    private String muscleGroup;
    private String equipment;
    private WorkoutRepository workoutRepository;
    
    private int selectedSets = 3;
    private int selectedRepsMin = 10;
    private int selectedRepsMax = 12;
    private int selectedRestSeconds = 90;

    private List<MaterialButton> setsButtons;
    private List<MaterialButton> repsButtons;
    private List<MaterialButton> restButtons;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExerciseConfigurationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize repository
        AppDatabase database = AppDatabase.getInstance(requireContext());
        workoutRepository = new WorkoutRepository(database);

        if (getArguments() != null) {
            programId = getArguments().getString("programId");
            dayId = getArguments().getString("dayId");
            exerciseName = getArguments().getString("exerciseName", "Exercise");
            muscleGroup = getArguments().getString("muscleGroup", "");
            equipment = getArguments().getString("equipment", "");
        }

        setupUI();
        setupButtonGroups();
        setupListeners();
    }

    private void setupUI() {
        binding.textSelectedExercise.setText(exerciseName);
    }

    private void setupButtonGroups() {
        // Sets buttons
        setsButtons = new ArrayList<>();
        setsButtons.add(binding.buttonSets1);
        setsButtons.add(binding.buttonSets2);
        setsButtons.add(binding.buttonSets3);
        setsButtons.add(binding.buttonSets4);
        setsButtons.add(binding.buttonSets5);
        setsButtons.add(binding.buttonSets6);
        
        for (int i = 0; i < setsButtons.size(); i++) {
            final int sets = i + 1;
            setsButtons.get(i).setOnClickListener(v -> selectSets(sets));
        }
        selectSets(3); // Default

        // Reps buttons
        repsButtons = new ArrayList<>();
        repsButtons.add(binding.buttonReps68);
        repsButtons.add(binding.buttonReps810);
        repsButtons.add(binding.buttonReps1012);
        repsButtons.add(binding.buttonReps1215);
        repsButtons.add(binding.buttonReps1520);
        repsButtons.add(binding.buttonRepsAMRAP);
        
        binding.buttonReps68.setOnClickListener(v -> selectReps(6, 8));
        binding.buttonReps810.setOnClickListener(v -> selectReps(8, 10));
        binding.buttonReps1012.setOnClickListener(v -> selectReps(10, 12));
        binding.buttonReps1215.setOnClickListener(v -> selectReps(12, 15));
        binding.buttonReps1520.setOnClickListener(v -> selectReps(15, 20));
        binding.buttonRepsAMRAP.setOnClickListener(v -> selectReps(0, 0)); // AMRAP
        selectReps(10, 12); // Default

        // Rest buttons
        restButtons = new ArrayList<>();
        restButtons.add(binding.buttonRest60);
        restButtons.add(binding.buttonRest90);
        restButtons.add(binding.buttonRest120);
        restButtons.add(binding.buttonRest180);
        
        binding.buttonRest60.setOnClickListener(v -> selectRest(60));
        binding.buttonRest90.setOnClickListener(v -> selectRest(90));
        binding.buttonRest120.setOnClickListener(v -> selectRest(120));
        binding.buttonRest180.setOnClickListener(v -> selectRest(180));
        selectRest(90); // Default
    }

    private void setupListeners() {
        binding.textBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        binding.buttonBackToSearch.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        binding.buttonAddExercise.setOnClickListener(v -> {
            addExercise();
        });
    }

    private void selectSets(int sets) {
        selectedSets = sets;
        for (int i = 0; i < setsButtons.size(); i++) {
            MaterialButton button = setsButtons.get(i);
            if (i + 1 == sets) {
                button.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                button.setBackgroundColor(getResources().getColor(R.color.colorSurface, null));
            }
        }
    }

    private void selectReps(int min, int max) {
        selectedRepsMin = min;
        selectedRepsMax = max;
        
        // Reset all buttons
        for (MaterialButton button : repsButtons) {
            button.setBackgroundColor(getResources().getColor(R.color.colorSurface, null));
        }
        
        // Highlight selected
        if (min == 6) binding.buttonReps68.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        else if (min == 8) binding.buttonReps810.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        else if (min == 10) binding.buttonReps1012.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        else if (min == 12) binding.buttonReps1215.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        else if (min == 15) binding.buttonReps1520.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        else if (min == 0) binding.buttonRepsAMRAP.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
    }

    private void selectRest(int seconds) {
        selectedRestSeconds = seconds;
        
        // Reset all buttons
        for (MaterialButton button : restButtons) {
            button.setBackgroundColor(getResources().getColor(R.color.colorSurface, null));
        }
        
        // Highlight selected
        if (seconds == 60) binding.buttonRest60.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        else if (seconds == 90) binding.buttonRest90.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        else if (seconds == 120) binding.buttonRest120.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        else if (seconds == 180) binding.buttonRest180.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
    }

    private void addExercise() {
        if (programId == null || dayId == null) {
            Toast.makeText(requireContext(), "Error: Missing program or day information", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create ProgramExercise object
        ProgramExercise exercise = new ProgramExercise();
        exercise.setExerciseName(exerciseName);
        exercise.setMuscleGroup(muscleGroup);
        exercise.setEquipment(equipment);
        exercise.setTargetSets(selectedSets);
        exercise.setTargetRepsMin(selectedRepsMin);
        exercise.setTargetRepsMax(selectedRepsMax);
        exercise.setRestSeconds(selectedRestSeconds);
        exercise.setOrderIndex(0); // Will be set properly by repository
        
        // Save to repository
        workoutRepository.addExercise(programId, dayId, exercise).observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Log.d("ExerciseConfig", "Exercise added successfully: " + exerciseName);
                Toast.makeText(requireContext(), 
                    "Added: " + exerciseName, 
                    Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            } else {
                Log.e("ExerciseConfig", "Failed to add exercise: " + exerciseName);
                Toast.makeText(requireContext(), 
                    "Failed to add exercise. Please try again.", 
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
