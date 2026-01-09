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
            binding.textProgramName.setText(program.getProgramName());
            
            // Format workout details
            String details = String.format("%dx per week • %d workout days", 
                program.getDaysPerWeek(), program.getDaysPerWeek());
            binding.textWorkoutDetails.setText(details);
            
            // Hide description by default for compact view
            binding.textDescription.setVisibility(View.GONE);
            
            // Show difficulty if present (but hide the layout for new design)
            binding.layoutDifficulty.setVisibility(View.GONE);
            
            // Hide duration for new design
            binding.textDuration.setVisibility(View.GONE);
            
            // Hide info layout for new design
            binding.layoutInfo.setVisibility(View.GONE);

            // Click on card to view details or edit
            binding.cardProgram.setOnClickListener(v -> listener.onProgramClick(program));
            
            // Click on play button to start workout
            binding.buttonPlay.setOnClickListener(v -> listener.onStartWorkoutClick(program));
        }
    }
}