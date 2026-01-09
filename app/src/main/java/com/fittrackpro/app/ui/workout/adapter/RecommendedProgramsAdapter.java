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
            
            // Show/hide preset chip
            binding.chipPreset.setVisibility(program.isPreset() ? View.VISIBLE : View.GONE);
            
            // Set description
            String description = program.getDescription();
            if (description != null && !description.isEmpty()) {
                binding.textDescription.setText(description);
                binding.textDescription.setVisibility(View.VISIBLE);
            } else {
                binding.textDescription.setVisibility(View.GONE);
            }
            
            // layoutInfo should remain visible (it contains days/week info)
            binding.layoutInfo.setVisibility(View.VISIBLE);
            
            // Set days per week
            binding.textDaysPerWeek.setText(program.getDaysPerWeek() + " days/week");
            
            // Set click listener on the card itself (not a button)
            binding.cardProgram.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStartWorkoutClick(program);
                }
            });
        }
    }
}
