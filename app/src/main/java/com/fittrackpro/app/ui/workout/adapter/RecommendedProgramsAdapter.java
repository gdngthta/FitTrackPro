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

import java.util.Objects;

/**
 * Adapter for displaying recommended workout programs.
 * This is a simplified adapter for showing preset/recommended programs.
 */
public class RecommendedProgramsAdapter extends ListAdapter<WorkoutProgram, RecommendedProgramsAdapter.ProgramViewHolder> {

    private final OnProgramClickListener listener;

    public interface OnProgramClickListener {
        void onStartWorkoutClick(WorkoutProgram program);
    }

    public RecommendedProgramsAdapter(OnProgramClickListener listener) {
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
                            Objects.equals(oldItem.getDescription(), newItem.getDescription()) &&
                            oldItem.getDaysPerWeek() == newItem.getDaysPerWeek() &&
                            oldItem.isPreset() == newItem.isPreset();
                }
            };

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkoutProgramBinding binding = ItemWorkoutProgramBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
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
            // Set program name
            binding.textProgramName.setText(program.getProgramName());
            
            // Set info text: "3x per week • 1 workout days"
            String infoText = program.getDaysPerWeek() + "x per week • 1 workout days";
            binding.textInfo.setText(infoText);
            
            // Set click listener on play button
            binding.buttonPlay.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStartWorkoutClick(program);
                }
            });
        }
    }
}
