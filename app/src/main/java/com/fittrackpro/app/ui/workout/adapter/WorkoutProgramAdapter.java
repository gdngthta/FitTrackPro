package com.fittrackpro.app.ui.workout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.ItemWorkoutProgramBinding;
import com.fittrackpro.app.data.model.WorkoutProgram;

/**
 * Adapter for displaying workout programs.
 */
public class WorkoutProgramAdapter extends ListAdapter<WorkoutProgram, WorkoutProgramAdapter.ProgramViewHolder> {

    private final OnProgramClickListener listener;
    private final boolean useOutlinedButton;

    public interface OnProgramClickListener {
        void onProgramClick(WorkoutProgram program);
        void onStartWorkoutClick(WorkoutProgram program);
    }

    public WorkoutProgramAdapter(OnProgramClickListener listener) {
        this(listener, false);
    }

    public WorkoutProgramAdapter(OnProgramClickListener listener, boolean useOutlinedButton) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.useOutlinedButton = useOutlinedButton;
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
        return new ProgramViewHolder(binding, useOutlinedButton);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ProgramViewHolder extends RecyclerView.ViewHolder {
        private final ItemWorkoutProgramBinding binding;
        private final boolean useOutlinedButton;

        public ProgramViewHolder(ItemWorkoutProgramBinding binding, boolean useOutlinedButton) {
            super(binding.getRoot());
            this.binding = binding;
            this.useOutlinedButton = useOutlinedButton;
        }

        public void bind(WorkoutProgram program, OnProgramClickListener listener) {
            binding.textProgramName.setText(program.getProgramName());
            
            // Format info text: "3x per week • 1 workout days"
            String infoText = program.getDaysPerWeek() + "x per week • " + 
                            "1 workout days"; // TODO: Get actual workout day count
            binding.textInfo.setText(infoText);

            // Set play button background based on whether this is a preset/recommended program
            if (useOutlinedButton) {
                binding.buttonPlay.setBackgroundResource(R.drawable.bg_play_button_outline);
            } else {
                binding.buttonPlay.setBackgroundResource(R.drawable.bg_play_button);
            }

            // Click on card to view details or edit
            binding.cardProgram.setOnClickListener(v -> listener.onProgramClick(program));
            
            // Play button click to start workout
            binding.buttonPlay.setOnClickListener(v -> listener.onStartWorkoutClick(program));
        }
    }
}