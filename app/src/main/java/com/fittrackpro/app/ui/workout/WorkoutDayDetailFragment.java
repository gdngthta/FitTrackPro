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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.R;
import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data.repository.WorkoutRepository;
import com.fittrackpro.app.databinding.FragmentWorkoutDayDetailBinding;
import com.fittrackpro.app.data.model.ProgramExercise;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * WorkoutDayDetailFragment shows workout day details with session settings and exercises.
 */
public class WorkoutDayDetailFragment extends Fragment {

    private FragmentWorkoutDayDetailBinding binding;
    private String programId;
    private String dayId;
    private String dayName;
    private List<ProgramExercise> exercises = new ArrayList<>();
    private WorkoutRepository workoutRepository;
    private FirebaseFirestore firestore;
    private ListenerRegistration exerciseListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutDayDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize repository and firestore
        AppDatabase database = AppDatabase.getInstance(requireContext());
        workoutRepository = new WorkoutRepository(database);
        firestore = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            programId = getArguments().getString("programId");
            dayId = getArguments().getString("dayId");
            dayName = getArguments().getString("dayName", "Workout Day");
        }

        setupUI();
        setupListeners();
        observeExercises();
    }

    private void setupUI() {
        binding.textDayName.setText(dayName);
        // TODO: Get actual program name from repository
        binding.textProgramName.setText("routine name");
        
        // Set default switch states
        binding.switchWarmup.setChecked(true);
        binding.switchCooldown.setChecked(true);
    }

    private void setupListeners() {
        binding.textBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        binding.buttonAddFirstExercise.setOnClickListener(v -> {
            navigateToExerciseLibrary();
        });

        binding.cardAddExercise.setOnClickListener(v -> {
            navigateToExerciseLibrary();
        });

        binding.buttonStartWorkout.setOnClickListener(v -> {
            // TODO: Navigate to active workout
            Toast.makeText(requireContext(), "Start workout functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Save switch states
        binding.switchWarmup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Save warmup preference
        });

        binding.switchCooldown.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Save cooldown preference
        });
    }

    private void observeExercises() {
        if (programId == null || dayId == null) {
            Log.e("WorkoutDayDetail", "Missing programId or dayId");
            return;
        }

        // Set up real-time listener for exercises
        exerciseListener = firestore.collection("workoutPrograms")
                .document(programId)
                .collection("workoutDays")
                .document(dayId)
                .collection("programExercises")
                .orderBy("orderIndex")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("WorkoutDayDetail", "Error listening to exercises", error);
                        return;
                    }

                    if (snapshots != null) {
                        exercises.clear();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots) {
                            ProgramExercise exercise = doc.toObject(ProgramExercise.class);
                            if (exercise != null) {
                                exercise.setExerciseId(doc.getId());
                                exercises.add(exercise);
                            }
                        }

                        Log.d("WorkoutDayDetail", "Loaded " + exercises.size() + " exercises");
                        updateExerciseUI();
                    }
                });
    }

    private void updateExerciseUI() {
        if (exercises.isEmpty()) {
            binding.cardEmptyState.setVisibility(View.VISIBLE);
            binding.recyclerExercises.setVisibility(View.GONE);
            binding.cardAddExercise.setVisibility(View.GONE);
        } else {
            binding.cardEmptyState.setVisibility(View.GONE);
            binding.recyclerExercises.setVisibility(View.VISIBLE);
            binding.cardAddExercise.setVisibility(View.VISIBLE);
            
            // TODO: Setup RecyclerView with exercises adapter
            // For now, this will show the exercises area
            Log.d("WorkoutDayDetail", "Displaying " + exercises.size() + " exercises");
        }
    }

    private void loadExercises() {
        // This method is now replaced by observeExercises()
        updateExerciseUI();
    }

    private void navigateToExerciseLibrary() {
        Bundle args = new Bundle();
        args.putString("programId", programId);
        args.putString("dayId", dayId);
        Navigation.findNavController(binding.getRoot())
            .navigate(R.id.action_dayDetail_to_exerciseLibrary, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Remove Firestore listener
        if (exerciseListener != null) {
            exerciseListener.remove();
            exerciseListener = null;
        }
        
        binding = null;
    }
}
