package com.fittrackpro.app.ui.workout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.R;
import com.fittrackpro.app.data.model.WorkoutSet;
import com.fittrackpro.app.util.Constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for displaying exercise breakdown with sets in workout summary
 */
public class SummaryExerciseAdapter extends RecyclerView.Adapter<SummaryExerciseAdapter.ExerciseViewHolder> {

    private List<ExerciseGroup> exerciseGroups = new ArrayList<>();

    /**
     * Group class to hold exercise name and its sets
     */
    public static class ExerciseGroup {
        public String exerciseName;
        public List<WorkoutSet> sets;
        public double totalVolume;

        public ExerciseGroup(String exerciseName, List<WorkoutSet> sets) {
            this.exerciseName = exerciseName;
            this.sets = sets;
            
            // Calculate total volume
            this.totalVolume = 0;
            for (WorkoutSet set : sets) {
                if (set.getStatus().equals(Constants.SET_STATUS_COMPLETED) ||
                    set.getStatus().equals(Constants.SET_STATUS_MODIFIED)) {
                    this.totalVolume += set.getWeight() * set.getReps();
                }
            }
        }
    }

    public void submitList(List<WorkoutSet> sets) {
        // Group sets by exercise name
        Map<String, List<WorkoutSet>> grouped = new LinkedHashMap<>();
        
        for (WorkoutSet set : sets) {
            if (!set.getStatus().equals(Constants.SET_STATUS_COMPLETED) &&
                !set.getStatus().equals(Constants.SET_STATUS_MODIFIED)) {
                continue; // Skip non-completed sets
            }

            String exerciseName = set.getExerciseName();
            if (!grouped.containsKey(exerciseName)) {
                grouped.put(exerciseName, new ArrayList<>());
            }
            grouped.get(exerciseName).add(set);
        }

        // Convert to list of ExerciseGroups
        exerciseGroups.clear();
        for (Map.Entry<String, List<WorkoutSet>> entry : grouped.entrySet()) {
            exerciseGroups.add(new ExerciseGroup(entry.getKey(), entry.getValue()));
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_summary_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ExerciseGroup group = exerciseGroups.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return exerciseGroups.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final TextView textExerciseName;
        private final LinearLayout layoutSets;
        private final TextView textExerciseVolume;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            textExerciseName = itemView.findViewById(R.id.textExerciseName);
            layoutSets = itemView.findViewById(R.id.layoutSets);
            textExerciseVolume = itemView.findViewById(R.id.textExerciseVolume);
        }

        public void bind(ExerciseGroup group) {
            textExerciseName.setText(group.exerciseName);

            // Clear previous sets
            layoutSets.removeAllViews();

            // Add set rows dynamically
            int setNumber = 1;
            for (WorkoutSet set : group.sets) {
                TextView setView = new TextView(itemView.getContext());
                
                String setText = itemView.getContext().getString(
                    R.string.set_format, 
                    setNumber, 
                    set.getWeight(), 
                    set.getReps()
                );
                
                setView.setText(setText);
                setView.setTextSize(14);
                setView.setTextColor(androidx.core.content.ContextCompat.getColor(
                    itemView.getContext(), 
                    R.color.md_theme_onSurface
                ));
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.topMargin = 4;
                setView.setLayoutParams(params);
                
                layoutSets.addView(setView);
                setNumber++;
            }

            // Display total volume
            String volumeText = itemView.getContext().getString(
                R.string.total_format, 
                group.totalVolume
            );
            textExerciseVolume.setText(volumeText);
        }
    }
}
