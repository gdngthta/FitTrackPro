package com.fittrackpro.app.ui.dashboard. adapter;

import android.view. LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget. DiffUtil;
import androidx. recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.databinding.ItemRecentWorkoutBinding;
import com.fittrackpro. app.data.model.CompletedWorkout;
import com.fittrackpro.app.util.TimeUtils;

/**
 * Adapter for displaying recent workouts in Dashboard.
 */
public class RecentWorkoutAdapter extends ListAdapter<CompletedWorkout, RecentWorkoutAdapter. WorkoutViewHolder> {

    public RecentWorkoutAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<CompletedWorkout> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CompletedWorkout>() {
                @Override
                public boolean areItemsTheSame(@NonNull CompletedWorkout oldItem, @NonNull CompletedWorkout newItem) {
                    return oldItem.getWorkoutId().equals(newItem.getWorkoutId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull CompletedWorkout oldItem, @NonNull CompletedWorkout newItem) {
                    return oldItem.getWorkoutId().equals(newItem.getWorkoutId()) &&
                            oldItem.getTotalVolume() == newItem.getTotalVolume();
                }
            };

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentWorkoutBinding binding = ItemRecentWorkoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new WorkoutViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecentWorkoutBinding binding;

        public WorkoutViewHolder(ItemRecentWorkoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CompletedWorkout workout) {
            binding.textWorkoutName.setText(workout.getWorkoutName());
            binding.textWorkoutDate.setText(
                    TimeUtils.formatRelativeTime(workout.getStartTime().toDate())
            );
            binding.textWorkoutDuration.setText(
                    TimeUtils.formatDuration(workout.getDurationSeconds())
            );
            binding.textWorkoutVolume.setText(
                    String.format("%.1f kg", workout.getTotalVolume())
            );
            binding.textWorkoutSets.setText(
                    workout.getTotalSets() + " sets"
            );
        }
    }
}