package com.fittrackpro.app.ui.workout.adapter;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.ItemWorkoutDayBinding;
import com.fittrackpro.app.data.model.WorkoutDay;

/**
 * Adapter for workout days list.
 */
public class WorkoutDayAdapter extends ListAdapter<WorkoutDay, WorkoutDayAdapter.DayViewHolder> {

    private final OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(WorkoutDay day);
        void onEditClick(WorkoutDay day);
        void onDeleteClick(WorkoutDay day);
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
                    return oldItem.getDayId().equals(newItem.getDayId()) &&
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
            // Create formatted text: "Day 1  workout day name"
            // "Day 1" in gray, "workout day name" in white
            String dayNumText = "Day " + day.getDayNumber();
            String fullText = dayNumText + "  " + day.getDayName();
            
            SpannableString spannable = new SpannableString(fullText);
            
            // Gray color for "Day X"
            int grayColor = binding.getRoot().getContext().getColor(R.color.colorOnSurfaceSecondary);
            spannable.setSpan(new ForegroundColorSpan(grayColor), 
                            0, dayNumText.length(), 
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            
            // White color and bold for workout name
            int whiteColor = binding.getRoot().getContext().getColor(R.color.white);
            spannable.setSpan(new ForegroundColorSpan(whiteColor), 
                            dayNumText.length() + 2, fullText.length(), 
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            
            binding.textDayLabel.setText(spannable);
            
            // TODO: Get actual exercise count from repository
            binding.textExerciseCount.setText("0 exercises");

            binding.cardDay.setOnClickListener(v -> listener.onDayClick(day));
            binding.iconEdit.setOnClickListener(v -> listener.onEditClick(day));
            binding.iconDelete.setOnClickListener(v -> listener.onDeleteClick(day));
        }
    }
}