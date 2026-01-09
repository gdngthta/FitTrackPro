package com.fittrackpro.app.ui.workout.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.databinding.ItemRecommendedProgramBinding;
import com.fittrackpro.app.data.model.WorkoutProgram;

/**
 * Adapter for displaying recommended (preset) programs
 */
public class RecommendedProgramAdapter extends ListAdapter<WorkoutProgram, RecommendedProgramAdapter.ViewHolder> {

    private final OnProgramClickListener listener;

    public interface OnProgramClickListener {
        void onProgramClick(WorkoutProgram program);
    }

    public RecommendedProgramAdapter(OnProgramClickListener listener) {
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
                           oldItem.getDaysPerWeek() == newItem.getDaysPerWeek();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecommendedProgramBinding binding = ItemRecommendedProgramBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecommendedProgramBinding binding;

        public ViewHolder(@NonNull ItemRecommendedProgramBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(WorkoutProgram program, OnProgramClickListener listener) {
            binding.textProgramName.setText(program.getProgramName());
            
            // Format info text: "3x per week • 3 workout days"
            String info = program.getDaysPerWeek() + "x per week • " + 
                         program.getDaysPerWeek() + " workout days";
            binding.textInfo.setText(info);

            binding.buttonPlay.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProgramClick(program);
                }
            });
            
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProgramClick(program);
                }
            });
        }
    }
}
