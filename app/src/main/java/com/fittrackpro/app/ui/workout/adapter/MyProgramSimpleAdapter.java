package com.fittrackpro.app.ui.workout.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.databinding.ItemMyProgramSimpleBinding;
import com.fittrackpro.app.data.model.WorkoutProgram;

/**
 * Adapter for displaying user's programs in My Programs section
 */
public class MyProgramSimpleAdapter extends ListAdapter<WorkoutProgram, MyProgramSimpleAdapter.ViewHolder> {

    private final OnProgramClickListener listener;

    public interface OnProgramClickListener {
        void onProgramClick(WorkoutProgram program);
    }

    public MyProgramSimpleAdapter(OnProgramClickListener listener) {
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
        ItemMyProgramSimpleBinding binding = ItemMyProgramSimpleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMyProgramSimpleBinding binding;

        public ViewHolder(@NonNull ItemMyProgramSimpleBinding binding) {
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
