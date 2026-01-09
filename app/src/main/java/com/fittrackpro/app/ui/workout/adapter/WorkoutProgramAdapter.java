package com.fittrackpro.app.ui.workout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.databinding.ItemWorkoutProgramBinding;
import com.fittrackpro.app.data.model.WorkoutProgram;

/**
 * Adapter for displaying workout programs.
 * Supports simplified card layout with play button.
 */
public class WorkoutProgramAdapter extends ListAdapter<WorkoutProgram, WorkoutProgramAdapter.ProgramViewHolder> {

    private final OnProgramClickListener listener;

    public interface OnProgramClickListener {
        void onProgramClick(WorkoutProgram program);
        void onStartWorkoutClick(WorkoutProgram program);
    }

    public WorkoutProgramAdapter(OnProgramClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<WorkoutProgram> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WorkoutProgram>() {
                @Override
                public boolean areItemsTheSame(@NonNull WorkoutProgram oldItem, @NonNull WorkoutProgram newItem) {
                    return oldItem.getProgramId().equals(newItem.getProgramId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull WorkoutProgram oldItem, @NonNull WorkoutProgram newItem) {
                    return oldItem.getProgramId().equals(newItem.getProgramId()) &&
                            oldItem.getProgramName().equals(newItem.getProgramName()) &&
                            oldItem.isActive() == newItem.isActive();
                }
            };

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkoutProgramBinding binding = ItemWorkoutProgramBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ProgramViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ProgramViewHolder extends RecyclerView.ViewHolder {
        private final ItemWorkoutProgramBinding binding;

        public ProgramViewHolder(ItemWorkoutProgramBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(WorkoutProgram program, OnProgramClickListener listener) {
            // Program name
            binding.textProgramName.setText(program.getProgramName());
            
            // Frequency info (e.g., "3x per week • 3 workout days")
            // For now, use daysPerWeek for both until we have actual workout day count
            int workoutDays = program.getDaysPerWeek(); // This would come from actual workout days count
            String frequencyInfo = program.getDaysPerWeek() + "x per week • " + 
                                 workoutDays + " workout days";
            binding.textFrequencyInfo.setText(frequencyInfo);
            
            // Show difficulty badge for preset programs
            if (program.isPreset() && program.getDifficulty() != null && !program.getDifficulty().isEmpty()) {
                binding.textDifficulty.setVisibility(View.VISIBLE);
                binding.textDifficulty.setText(program.getDifficulty().toUpperCase());
            } else {
                binding.textDifficulty.setVisibility(View.GONE);
            }

            // Click on card to view details
            binding.cardProgram.setOnClickListener(v -> listener.onProgramClick(program));
            
            // Click on play button to start workout
            binding.iconPlay.setOnClickListener(v -> listener.onStartWorkoutClick(program));
        }
    }
}