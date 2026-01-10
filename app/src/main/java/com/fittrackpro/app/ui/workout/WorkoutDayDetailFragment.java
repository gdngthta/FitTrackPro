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
import com.fittrackpro.app.databinding.FragmentWorkoutDayDetailBinding;
import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data.model.ProgramExercise;
import com.fittrackpro.app.data.repository.WorkoutRepository;

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
    private ExerciseAdapter exerciseAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutDayDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            programId = getArguments().getString("programId");
            dayId = getArguments().getString("dayId");
            dayName = getArguments().getString("dayName", "Workout Day");
        }

        // Initialize repository
        AppDatabase database = AppDatabase.getInstance(requireContext());
        workoutRepository = new WorkoutRepository(database);

        setupUI();
        setupListeners();
        setupFragmentResultListener();
        loadExercises();
    }

    private void setupFragmentResultListener() {
        // Listen for exercise addition result
        getParentFragmentManager().setFragmentResultListener("exercise_added", this, (requestKey, result) -> {
            android.util.Log.d("WorkoutDayDetail", "Received exercise result");
            
            String exerciseName = result.getString("exerciseName");
            String muscleGroup = result.getString("muscleGroup");
            String equipment = result.getString("equipment");
            int targetSets = result.getInt("targetSets");
            int targetRepsMin = result.getInt("targetRepsMin");
            int targetRepsMax = result.getInt("targetRepsMax");
            int restSeconds = result.getInt("restSeconds");
            
            android.util.Log.d("WorkoutDayDetail", "Adding exercise: " + exerciseName);
            
            // Create ProgramExercise object
            ProgramExercise exercise = new ProgramExercise();
            exercise.setExerciseName(exerciseName);
            exercise.setMuscleGroup(muscleGroup);
            exercise.setEquipment(equipment);
            exercise.setTargetSets(targetSets);
            exercise.setTargetRepsMin(targetRepsMin);
            exercise.setTargetRepsMax(targetRepsMax);
            exercise.setRestSeconds(restSeconds);
            exercise.setOrderIndex(exercises.size() + 1);
            
            // Save to repository
            workoutRepository.addExercise(programId, dayId, exercise).observe(getViewLifecycleOwner(), success -> {
                if (success != null && success) {
                    android.util.Log.d("WorkoutDayDetail", "Exercise saved successfully");
                    Toast.makeText(requireContext(), "Exercise added!", Toast.LENGTH_SHORT).show();
                    
                    // Add to local list and update UI
                    exercises.add(exercise);
                    updateExerciseList();
                } else {
                    android.util.Log.e("WorkoutDayDetail", "Failed to save exercise");
                    Toast.makeText(requireContext(), "Failed to add exercise", Toast.LENGTH_SHORT).show();
                }
            });
        });
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
            // Navigate to active workout with program and day info
            Bundle args = new Bundle();
            args.putString("programId", programId);
            args.putString("dayId", dayId);
            args.putString("dayName", dayName);
            Navigation.findNavController(v).navigate(R.id.action_to_activeWorkout, args);
        });

        // Save switch states
        binding.switchWarmup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Save warmup preference
        });

        binding.switchCooldown.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Save cooldown preference
        });
    }

    private void loadExercises() {
        android.util.Log.d("WorkoutDayDetail", "Loading exercises for programId: " + programId + ", dayId: " + dayId);
        
        if (programId != null && dayId != null) {
            workoutRepository.getExercisesForDay(programId, dayId).observe(getViewLifecycleOwner(), exerciseList -> {
                if (exerciseList != null) {
                    android.util.Log.d("WorkoutDayDetail", "Loaded " + exerciseList.size() + " exercises");
                    exercises.clear();
                    exercises.addAll(exerciseList);
                    updateExerciseList();
                } else {
                    android.util.Log.w("WorkoutDayDetail", "Exercise list is null");
                }
            });
        } else {
            android.util.Log.w("WorkoutDayDetail", "programId or dayId is null");
            updateExerciseList();
        }
    }

    private void updateExerciseList() {
        if (exercises.isEmpty()) {
            binding.cardEmptyState.setVisibility(View.VISIBLE);
            binding.recyclerExercises.setVisibility(View.GONE);
            binding.cardAddExercise.setVisibility(View.GONE);
        } else {
            binding.cardEmptyState.setVisibility(View.GONE);
            binding.recyclerExercises.setVisibility(View.VISIBLE);
            binding.cardAddExercise.setVisibility(View.VISIBLE);
            
            // Setup RecyclerView with exercises
            if (exerciseAdapter == null) {
                exerciseAdapter = new ExerciseAdapter(exercises);
                binding.recyclerExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.recyclerExercises.setAdapter(exerciseAdapter);
            } else {
                exerciseAdapter.notifyDataSetChanged();
            }
        }
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
        binding = null;
    }

    // Simple adapter for exercises
    private static class ExerciseAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
        private final List<ProgramExercise> exercises;

        ExerciseAdapter(List<ProgramExercise> exercises) {
            this.exercises = exercises;
        }

        @NonNull
        @Override
        public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_exercise, parent, false);
            return new ExerciseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
            holder.bind(exercises.get(position));
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        static class ExerciseViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            private final android.widget.TextView textNumber;
            private final android.widget.TextView textExerciseName;
            private final android.widget.TextView textDetails;
            private final android.widget.TextView textMuscleInfo;

            ExerciseViewHolder(View view) {
                super(view);
                textNumber = view.findViewById(R.id.textNumber);
                textExerciseName = view.findViewById(R.id.textExerciseName);
                textDetails = view.findViewById(R.id.textDetails);
                textMuscleInfo = view.findViewById(R.id.textMuscleInfo);
            }

            void bind(ProgramExercise exercise) {
                textNumber.setText((getAdapterPosition() + 1) + ".");
                textExerciseName.setText(exercise.getExerciseName());
                
                String details = exercise.getTargetSets() + " sets • " + 
                               exercise.getTargetRepsMin() + "-" + exercise.getTargetRepsMax() + " reps • " +
                               exercise.getRestSeconds() + "s rest";
                textDetails.setText(details);
                
                String muscleInfo = exercise.getMuscleGroup() + " • " + exercise.getEquipment();
                textMuscleInfo.setText(muscleInfo);
            }
        }
    }
}
