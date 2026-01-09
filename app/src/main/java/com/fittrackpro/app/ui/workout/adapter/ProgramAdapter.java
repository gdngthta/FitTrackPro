package com.fittrackpro.app.ui.workout.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.databinding.ItemProgramCardBinding;
import com.fittrackpro.app.data.model.WorkoutProgram;

/**
 * Adapter for displaying workout programs in Browse Programs tab
 */
public class ProgramAdapter extends ListAdapter<WorkoutProgram, ProgramAdapter.ProgramViewHolder> {

    private final OnProgramClickListener listener;

    public interface OnProgramClickListener {
        void onProgramClick(WorkoutProgram program);
    }

    public ProgramAdapter(OnProgramClickListener listener) {
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
                           oldItem.getDescription().equals(newItem.getDescription()) &&
                           oldItem.getDifficulty().equals(newItem.getDifficulty());
                }
            };

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProgramCardBinding binding = ItemProgramCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProgramViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ProgramViewHolder extends RecyclerView.ViewHolder {
        private final ItemProgramCardBinding binding;

        public ProgramViewHolder(@NonNull ItemProgramCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(WorkoutProgram program, OnProgramClickListener listener) {
            binding.textProgramName.setText(program.getProgramName());
            binding.textDescription.setText(program.getDescription());
            binding.textDuration.setText(program.getDurationWeeks() + " weeks");
            binding.textFrequency.setText(program.getDaysPerWeek() + " days/week");
            
            // Set focus areas (if available)
            // For now, we'll use the difficulty as a placeholder
            binding.textFocusAreas.setText("Difficulty: " + program.getDifficulty());

            binding.buttonStart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProgramClick(program);
                }
            });
        }
    }
}
