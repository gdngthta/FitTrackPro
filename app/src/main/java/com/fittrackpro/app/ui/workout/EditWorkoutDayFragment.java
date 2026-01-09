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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentEditWorkoutDayBinding;
import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data.repository.WorkoutRepository;
import com.fittrackpro.app.data.model.WorkoutDay;
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
        binding = FragmentEditWorkoutDayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        workoutRepository = new WorkoutRepository(AppDatabase.getInstance(requireContext()));

        if (getArguments() != null) {
            programId = getArguments().getString("programId");
            daysPerWeek = getArguments().getInt("daysPerWeek", 4);
        }

        setupRecyclerView();
        loadWorkoutDays();
        setupListeners();
        updateProgramInfo();
    }

    private void setupRecyclerView() {
        adapter = new WorkoutDayAdapter(new WorkoutDayAdapter.OnDayClickListener() {
            @Override
            public void onDayClick(WorkoutDay day) {
                // Navigate to workout day detail screen
                Bundle args = new Bundle();
                args.putString("programId", programId);
                args.putString("dayId", day.getDayId());
                args.putString("dayName", day.getDayName());
                Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_editWorkoutDays_to_dayDetail, args);
            }

            @Override
            public void onEditClick(WorkoutDay day) {
                // Show edit dialog
                showEditDayDialog(day);
            }

            @Override
            public void onDeleteClick(WorkoutDay day) {
                // Delete workout day
                deleteWorkoutDay(day);
            }
        });

        binding.recyclerWorkoutDays.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerWorkoutDays.setAdapter(adapter);
    }

    private void loadWorkoutDays() {
        workoutRepository.getWorkoutDays(programId).observe(getViewLifecycleOwner(), days -> {
            if (days != null) {
                adapter.submitList(days);
                updateProgramInfo();
            }
        });
    }

    private void updateProgramInfo() {
        // TODO: Get actual program name from repository
        binding.textProgramName.setText("routine name");
        
        int workoutDaysCount = adapter.getItemCount();
        binding.textProgramInfo.setText(daysPerWeek + "x per week • " + workoutDaysCount + " workout days");
    }

    private void setupListeners() {
        binding.textBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        binding.cardAddDay.setOnClickListener(v -> {
            showAddDayDialog();
        });

        binding.buttonAddDayDialog.setOnClickListener(v -> {
            String dayName = binding.editDayName.getText().toString().trim();
            if (!dayName.isEmpty()) {
                addWorkoutDay(dayName);
                binding.cardAddDayDialog.setVisibility(View.GONE);
                binding.editDayName.setText("");
            } else {
                binding.editDayName.setError(getString(R.string.workout_day_name));
            }
        });

        binding.buttonCancelDialog.setOnClickListener(v -> {
            binding.cardAddDayDialog.setVisibility(View.GONE);
            binding.editDayName.setText("");
        });
    }

    private void showAddDayDialog() {
        binding.cardAddDayDialog.setVisibility(View.VISIBLE);
        binding.editDayName.requestFocus();
    }

    private void showEditDayDialog(WorkoutDay day) {
        binding.editDayName.setText(day.getDayName());
        binding.cardAddDayDialog.setVisibility(View.VISIBLE);
        binding.editDayName.requestFocus();
        
        // Change button text temporarily
        binding.buttonAddDayDialog.setText(R.string.save);
        binding.buttonAddDayDialog.setOnClickListener(v -> {
            String newName = binding.editDayName.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateWorkoutDay(day, newName);
                binding.cardAddDayDialog.setVisibility(View.GONE);
                binding.editDayName.setText("");
                binding.buttonAddDayDialog.setText(R.string.add_day);
            }
        });
    }

    private void addWorkoutDay(String dayName) {
        int dayNumber = adapter.getItemCount() + 1;

        workoutRepository.addWorkoutDay(programId, dayName, dayNumber)
                .observe(getViewLifecycleOwner(), success -> {
                    if (success != null && success) {
                        Toast.makeText(requireContext(), "Day added!", Toast.LENGTH_SHORT).show();
                        loadWorkoutDays();
                    } else {
                        Toast.makeText(requireContext(), "Failed to add day", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateWorkoutDay(WorkoutDay day, String newName) {
        // TODO: Implement update in repository
        Toast.makeText(requireContext(), "Update functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    private void deleteWorkoutDay(WorkoutDay day) {
        // TODO: Implement delete in repository
        Toast.makeText(requireContext(), "Delete functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}