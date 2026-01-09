package com.fittrackpro.app.ui.workout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.databinding.ItemMyProgramCardBinding;
import com.fittrackpro.app.data.model.WorkoutProgram;

/**
 * Adapter for displaying user's active workout programs
 */
public class MyProgramsAdapter extends ListAdapter<WorkoutProgram, MyProgramsAdapter.ProgramViewHolder> {

    private final OnProgramClickListener listener;

    public interface OnProgramClickListener {
        void onStartWorkoutClick(WorkoutProgram program);
    }

    public MyProgramsAdapter(OnProgramClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<WorkoutProgram> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WorkoutProgram>() {
                @Override
                public boolean areItemsTheSame(@NonNull WorkoutProgram oldItem,
                                                @NonNull WorkoutProgram newItem) {
                    return oldItem.getProgramId().equals(newItem.getProgramId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull WorkoutProgram oldItem,
                                                   @NonNull WorkoutProgram newItem) {
                    return oldItem.getProgramName().equals(newItem.getProgramName()) &&
                           oldItem.isActive() == newItem.isActive();
                }
            };

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMyProgramCardBinding binding = ItemMyProgramCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProgramViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ProgramViewHolder extends RecyclerView.ViewHolder {
        private final ItemMyProgramCardBinding binding;

        public ProgramViewHolder(@NonNull ItemMyProgramCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(WorkoutProgram program, OnProgramClickListener listener) {
            binding.textProgramName.setText(program.getProgramName());
            
            // For now, show a simple progress (would be calculated based on user's completion)
            binding.textProgress.setText("Week 1 of " + program.getDurationWeeks());
            binding.progressBar.setProgress(10); // Placeholder

            // Show today's workout section (for now, show as a workout day)
            binding.layoutTodayWorkout.setVisibility(View.VISIBLE);
            binding.layoutRestDay.setVisibility(View.GONE);
            binding.textTodayLabel.setText("Today's Workout: Day 1");
            binding.textExercisePreview.setText("Tap to start workout");

            binding.buttonStartWorkout.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStartWorkoutClick(program);
                }
            });

            // Hide view details button for now
            binding.buttonViewDetails.setVisibility(View.GONE);
        }
    }
}
