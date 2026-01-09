package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentExerciseLibraryBinding;
import com.fittrackpro.app.data.model.ExerciseTemplate;
import com.fittrackpro.app.util.ExerciseLibrary;
import com.google.android.material.chip.Chip;

import java.util.List;

/**
 * ExerciseLibraryFragment allows browsing and selecting exercises.
 */
public class ExerciseLibraryFragment extends Fragment {

    private FragmentExerciseLibraryBinding binding;
    private ExerciseLibraryAdapter adapter;
    private List<ExerciseTemplate> allExercises;
    private String selectedMuscle = "All";
    private String selectedEquipment = "All";
    private String searchQuery = "";
    private ExerciseTemplate selectedExercise = null;
    
    private String programId;
    private String dayId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExerciseLibraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            programId = getArguments().getString("programId");
            dayId = getArguments().getString("dayId");
        }

        allExercises = ExerciseLibrary.getAllExercises();
        
        setupFilterChips();
        setupRecyclerView();
        setupListeners();
        filterExercises();
    }

    private void setupFilterChips() {
        // Add muscle group chips
        for (String muscle : ExerciseLibrary.getMuscleGroups()) {
            Chip chip = createFilterChip(muscle);
            chip.setOnClickListener(v -> {
                selectedMuscle = muscle;
                filterExercises();
            });
            binding.chipGroupMuscle.addView(chip);
            if (muscle.equals("All")) {
                chip.setChecked(true);
            }
        }

        // Add equipment chips
        for (String equipment : ExerciseLibrary.getEquipmentTypes()) {
            Chip chip = createFilterChip(equipment);
            chip.setOnClickListener(v -> {
                selectedEquipment = equipment;
                filterExercises();
            });
            binding.chipGroupEquipment.addView(chip);
            if (equipment.equals("All")) {
                chip.setChecked(true);
            }
        }
    }

    private Chip createFilterChip(String text) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setChipBackgroundColorResource(R.color.colorSurface);
        chip.setTextColor(getResources().getColor(R.color.white, null));
        chip.setCheckedIconVisible(false);
        chip.setChipCornerRadius(20);
        
        // Change color when selected
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chip.setChipBackgroundColorResource(R.color.colorPrimary);
            } else {
                chip.setChipBackgroundColorResource(R.color.colorSurface);
            }
        });
        
        return chip;
    }

    private void setupRecyclerView() {
        adapter = new ExerciseLibraryAdapter(exercise -> {
            selectedExercise = exercise;
            // Navigate to configuration screen
            navigateToConfiguration(exercise);
        });
        
        binding.recyclerExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerExercises.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.textBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                filterExercises();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterExercises() {
        List<ExerciseTemplate> filtered = ExerciseLibrary.filterExercises(
            allExercises, 
            selectedMuscle, 
            selectedEquipment, 
            searchQuery
        );
        adapter.submitList(filtered);
    }

    private void navigateToConfiguration(ExerciseTemplate exercise) {
        Bundle args = new Bundle();
        args.putString("programId", programId);
        args.putString("dayId", dayId);
        args.putString("exerciseName", exercise.getName());
        args.putString("muscleGroup", exercise.getMuscleGroup());
        args.putString("equipment", exercise.getEquipment());
        Navigation.findNavController(binding.getRoot())
            .navigate(R.id.action_exerciseLibrary_to_configuration, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Inner adapter class
    private static class ExerciseLibraryAdapter extends RecyclerView.Adapter<ExerciseLibraryAdapter.ViewHolder> {
        private List<ExerciseTemplate> exercises;
        private final OnExerciseClickListener listener;

        interface OnExerciseClickListener {
            void onExerciseClick(ExerciseTemplate exercise);
        }

        ExerciseLibraryAdapter(OnExerciseClickListener listener) {
            this.listener = listener;
        }

        void submitList(List<ExerciseTemplate> exercises) {
            this.exercises = exercises;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_library, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ExerciseTemplate exercise = exercises.get(position);
            holder.bind(exercise, listener);
        }

        @Override
        public int getItemCount() {
            return exercises != null ? exercises.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final android.widget.TextView textExerciseName;
            private final android.widget.TextView textInfo;
            private final com.google.android.material.card.MaterialCardView cardExercise;

            ViewHolder(View view) {
                super(view);
                textExerciseName = view.findViewById(R.id.textExerciseName);
                textInfo = view.findViewById(R.id.textInfo);
                cardExercise = view.findViewById(R.id.cardExercise);
            }

            void bind(ExerciseTemplate exercise, OnExerciseClickListener listener) {
                textExerciseName.setText(exercise.getName());
                textInfo.setText(exercise.getMuscleGroup() + " • " + exercise.getEquipment());
                cardExercise.setOnClickListener(v -> listener.onExerciseClick(exercise));
            }
        }
    }
}
