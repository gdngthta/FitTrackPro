package com.fittrackpro.app.ui.workout. adapter;

import android.view. LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx. recyclerview.widget.DiffUtil;
import androidx.recyclerview. widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.databinding.ItemWorkoutDayBinding;
import com.fittrackpro. app.data.model.WorkoutDay;

/**
 * Adapter for workout days list.
 */
public class WorkoutDayAdapter extends ListAdapter<WorkoutDay, WorkoutDayAdapter.DayViewHolder> {

    private final OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(WorkoutDay day);
    }

    public WorkoutDayAdapter(OnDayClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<WorkoutDay> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WorkoutDay>() {
                @Override
                public boolean areItemsTheSame(@NonNull WorkoutDay oldItem, @NonNull WorkoutDay newItem) {
                    return oldItem.getDayId().equals(newItem.getDayId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull WorkoutDay oldItem, @NonNull WorkoutDay newItem) {
                    return oldItem. getDayId().equals(newItem.getDayId()) &&
                            oldItem.getDayName().equals(newItem.getDayName());
                }
            };

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkoutDayBinding binding = ItemWorkoutDayBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new DayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        private final ItemWorkoutDayBinding binding;

        public DayViewHolder(ItemWorkoutDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(WorkoutDay day, OnDayClickListener listener) {
            binding.textDayName.setText(day. getDayName());
            binding. textDayNumber.setText("Day " + day.getDayNumber());

            binding.cardDay.setOnClickListener(v -> listener.onDayClick(day));
        }
    }
}