package com.fittrackpro.app.ui.workout. adapter;

import android.text. Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com. fittrackpro.app. databinding.ItemActiveExerciseBinding;
import com.fittrackpro.app.data. model.ProgramExercise;

/**
 * Adapter for active workout exercise logging.
 *
 * Each item shows:
 * - Exercise name
 * - Target sets/reps
 * - Input fields for weight and reps
 * - Set status buttons
 */
public class ActiveExerciseAdapter extends ListAdapter<ProgramExercise, ActiveExerciseAdapter.ExerciseViewHolder> {

    private final OnSetLoggedListener listener;

    public interface OnSetLoggedListener {
        void onSetLogged(String exerciseName, double weight, int reps, String status);
    }

    public ActiveExerciseAdapter(OnSetLoggedListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ProgramExercise> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ProgramExercise>() {
                @Override
                public boolean areItemsTheSame(@NonNull ProgramExercise oldItem, @NonNull ProgramExercise newItem) {
                    return oldItem.getExerciseId().equals(newItem.getExerciseId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull ProgramExercise oldItem, @NonNull ProgramExercise newItem) {
                    return oldItem.getExerciseId().equals(newItem.getExerciseId());
                }
            };

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActiveExerciseBinding binding = ItemActiveExerciseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ExerciseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final ItemActiveExerciseBinding binding;

        public ExerciseViewHolder(ItemActiveExerciseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ProgramExercise exercise, OnSetLoggedListener listener) {
            binding.textExerciseName.setText(exercise.getExerciseName());
            binding.textTargetSets.setText(exercise.getTargetSets() + " sets");
            binding. textTargetReps.setText(
                    exercise.getTargetRepsMin() + "-" + exercise.getTargetRepsMax() + " reps"
            );

            if (exercise.getNotes() != null && !exercise.getNotes().isEmpty()) {
                binding.textExerciseNotes.setText(exercise.getNotes());
                binding.textExerciseNotes.setVisibility(View. VISIBLE);
            } else {
                binding.textExerciseNotes.setVisibility(View. GONE);
            }

            // Log set button
            binding.buttonLogSet. setOnClickListener(v -> {
                String weightStr = binding.editWeight.getText().toString();
                String repsStr = binding.editReps.getText().toString();

                if (weightStr.isEmpty() || repsStr.isEmpty()) {
                    binding.editWeight.setError("Enter weight");
                    binding.editReps.setError("Enter reps");
                    return;
                }

                double weight = Double.parseDouble(weightStr);
                int reps = Integer.parseInt(repsStr);

                listener.onSetLogged(exercise.getExerciseName(), weight, reps, "completed");

                // Clear inputs for next set
                binding.editWeight.setText("");
                binding.editReps.setText("");
            });

            // Skip button
            binding.buttonSkip.setOnClickListener(v -> {
                listener.onSetLogged(exercise.getExerciseName(), 0, 0, "skipped");
            });
        }
    }
}