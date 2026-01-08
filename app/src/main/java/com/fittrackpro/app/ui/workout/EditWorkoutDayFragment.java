package com.fittrackpro.app. ui.workout;

import android. os.Bundle;
import android. view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation. Nullable;
import androidx.fragment. app.Fragment;
import androidx. lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.databinding.FragmentEditWorkoutDayBinding;
import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data. repository.WorkoutRepository;
import com.fittrackpro.app.ui.workout.adapter.WorkoutDayAdapter;

/**
 * EditWorkoutDayFragment allows adding/editing workout days for a program.
 */
public class EditWorkoutDayFragment extends Fragment {

    private FragmentEditWorkoutDayBinding binding;
    private WorkoutRepository workoutRepository;
    private WorkoutDayAdapter adapter;
    private String programId;
    private int daysPerWeek;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditWorkoutDayBinding. inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        workoutRepository = new WorkoutRepository(AppDatabase. getInstance(requireContext()));

        if (getArguments() != null) {
            programId = getArguments().getString("programId");
            daysPerWeek = getArguments().getInt("daysPerWeek", 4);
        }

        setupRecyclerView();
        loadWorkoutDays();
        setupListeners();
    }

    private void setupRecyclerView() {
        adapter = new WorkoutDayAdapter((day) -> {
            // Navigate to exercise editor for this day
            Bundle args = new Bundle();
            args.putString("programId", programId);
            args.putString("dayId", day.getDayId());
            // Navigation code here
        });

        binding.recyclerWorkoutDays. setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerWorkoutDays.setAdapter(adapter);
    }

    private void loadWorkoutDays() {
        workoutRepository.getWorkoutDays(programId).observe(getViewLifecycleOwner(), days -> {
            if (days != null) {
                adapter.submitList(days);
                binding.emptyState.setVisibility(days.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setupListeners() {
        binding.fabAddDay.setOnClickListener(v -> {
            showAddDayDialog();
        });

        binding.buttonFinishSetup.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Program setup complete!", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });
    }

    private void showAddDayDialog() {
        android.widget.EditText input = new android.widget. EditText(requireContext());
        input.setHint("Day name (e.g., Push Day)");

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Workout Day")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String dayName = input.getText().toString().trim();
                    if (! dayName.isEmpty()) {
                        addWorkoutDay(dayName);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addWorkoutDay(String dayName) {
        int dayNumber = adapter.getItemCount() + 1;

        workoutRepository.addWorkoutDay(programId, dayName, dayNumber)
                .observe(getViewLifecycleOwner(), success -> {
                    if (success != null && success) {
                        Toast. makeText(requireContext(), "Day added!", Toast.LENGTH_SHORT).show();
                        loadWorkoutDays();
                    } else {
                        Toast.makeText(requireContext(), "Failed to add day", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}