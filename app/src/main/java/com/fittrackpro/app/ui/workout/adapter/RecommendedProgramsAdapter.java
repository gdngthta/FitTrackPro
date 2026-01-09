package com.fittrackpro.app.ui.workout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.databinding.ItemDifficultyHeaderBinding;
import com.fittrackpro.app.databinding.ItemWorkoutProgramBinding;
import com.fittrackpro.app.data.model.WorkoutProgram;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying grouped recommended programs by difficulty.
 */
public class RecommendedProgramsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_PROGRAM = 1;

    private final List<Object> items = new ArrayList<>();
    private final OnProgramClickListener listener;

    public interface OnProgramClickListener {
        void onProgramClick(WorkoutProgram program);
        void onStartWorkoutClick(WorkoutProgram program);
    }

    public RecommendedProgramsAdapter(OnProgramClickListener listener) {
        this.listener = listener;
    }

    public void setPrograms(List<WorkoutProgram> programs) {
        items.clear();
        
        // Group programs by difficulty
        List<WorkoutProgram> beginnerPrograms = new ArrayList<>();
        List<WorkoutProgram> intermediatePrograms = new ArrayList<>();
        List<WorkoutProgram> proPrograms = new ArrayList<>();
        List<WorkoutProgram> elitePrograms = new ArrayList<>();
        
        for (WorkoutProgram program : programs) {
            String difficulty = program.getDifficulty();
            if (difficulty == null) continue;
            
            switch (difficulty.toLowerCase()) {
                case "beginner":
                    beginnerPrograms.add(program);
                    break;
                case "intermediate":
                    intermediatePrograms.add(program);
                    break;
                case "pro":
                    proPrograms.add(program);
                    break;
                case "elite":
                    elitePrograms.add(program);
                    break;
            }
        }
        
        // Add beginner section
        if (!beginnerPrograms.isEmpty()) {
            items.add(new DifficultyHeader("Beginner", "New to weightlifting or returning after a long break"));
            items.addAll(beginnerPrograms);
        }
        
        // Add intermediate section
        if (!intermediatePrograms.isEmpty()) {
            items.add(new DifficultyHeader("Intermediate", "6+ months of consistent training experience"));
            items.addAll(intermediatePrograms);
        }
        
        // Add pro section
        if (!proPrograms.isEmpty()) {
            items.add(new DifficultyHeader("Pro", "2+ years of training with solid strength foundation"));
            items.addAll(proPrograms);
        }
        
        // Add elite section
        if (!elitePrograms.isEmpty()) {
            items.add(new DifficultyHeader("Elite", "Advanced lifters focused on powerlifting or competition"));
            items.addAll(elitePrograms);
        }
        
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof DifficultyHeader ? TYPE_HEADER : TYPE_PROGRAM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            ItemDifficultyHeaderBinding binding = ItemDifficultyHeaderBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new HeaderViewHolder(binding);
        } else {
            ItemWorkoutProgramBinding binding = ItemWorkoutProgramBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new ProgramViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((DifficultyHeader) items.get(position));
        } else if (holder instanceof ProgramViewHolder) {
            ((ProgramViewHolder) holder).bind((WorkoutProgram) items.get(position), listener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final ItemDifficultyHeaderBinding binding;

        public HeaderViewHolder(ItemDifficultyHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DifficultyHeader header) {
            binding.textDifficultyLevel.setText(header.level);
            binding.textDifficultyDescription.setText(header.description);
        }
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
            
            // Hide other elements for new design
            binding.layoutDifficulty.setVisibility(View.GONE);
            binding.textDuration.setVisibility(View.GONE);
            binding.layoutInfo.setVisibility(View.GONE);

            // Click on card to view details
            binding.cardProgram.setOnClickListener(v -> listener.onProgramClick(program));
            
            // Click on play button to start workout
            binding.buttonPlay.setOnClickListener(v -> listener.onStartWorkoutClick(program));
        }
    }

    static class DifficultyHeader {
        final String level;
        final String description;

        DifficultyHeader(String level, String description) {
            this.level = level;
            this.description = description;
        }
    }
}
