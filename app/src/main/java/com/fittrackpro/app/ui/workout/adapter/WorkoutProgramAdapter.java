package com.fittrackpro.app.ui.workout. adapter;

import android.view. LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget. DiffUtil;
import androidx. recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.databinding.ItemWorkoutProgramBinding;
import com. fittrackpro.app. data.model.WorkoutProgram;

/**
 * Adapter for displaying workout programs.
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
                    return oldItem. getProgramId().equals(newItem.getProgramId()) &&
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
            binding.textProgramName.setText(program. getProgramName());
            binding. textProgramDescription.setText(program.getDescription());
            binding.textProgramDifficulty.setText(program.getDifficulty());
            binding.textProgramDuration.setText(program.getDurationWeeks() + " weeks");
            binding.textProgramDays.setText(program.getDaysPerWeek() + " days/week");

            binding.cardProgram.setOnClickListener(v -> listener.onProgramClick(program));
            binding.buttonStartWorkout.setOnClickListener(v -> listener.onStartWorkoutClick(program));
        }
    }
}