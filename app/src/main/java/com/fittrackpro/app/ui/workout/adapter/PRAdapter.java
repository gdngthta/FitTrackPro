package com.fittrackpro.app.ui.workout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrackpro.app.R;
import com.fittrackpro.app.data.model.PersonalRecord;
import com.fittrackpro.app.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying new personal records in celebratory card
 */
public class PRAdapter extends RecyclerView.Adapter<PRAdapter.PRViewHolder> {

    private List<PersonalRecord> personalRecords = new ArrayList<>();

    public void submitList(List<PersonalRecord> records) {
        this.personalRecords = records != null ? records : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pr_achievement, parent, false);
        return new PRViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PRViewHolder holder, int position) {
        PersonalRecord pr = personalRecords.get(position);
        holder.bind(pr);
    }

    @Override
    public int getItemCount() {
        return personalRecords.size();
    }

    static class PRViewHolder extends RecyclerView.ViewHolder {
        private final TextView textExerciseName;
        private final TextView textPRType;
        private final TextView textOldValue;
        private final TextView textNewValue;

        public PRViewHolder(@NonNull View itemView) {
            super(itemView);
            textExerciseName = itemView.findViewById(R.id.textExerciseName);
            textPRType = itemView.findViewById(R.id.textPRType);
            textOldValue = itemView.findViewById(R.id.textOldValue);
            textNewValue = itemView.findViewById(R.id.textNewValue);
        }

        public void bind(PersonalRecord pr) {
            textExerciseName.setText(pr.getExerciseName());

            // Format PR type
            String prType = "";
            switch (pr.getRecordType()) {
                case Constants.PR_TYPE_WEIGHT:
                    prType = itemView.getContext().getString(R.string.weight_pr);
                    break;
                case Constants.PR_TYPE_REPS:
                    prType = itemView.getContext().getString(R.string.rep_pr);
                    break;
                case Constants.PR_TYPE_VOLUME:
                    prType = itemView.getContext().getString(R.string.volume_pr);
                    break;
            }
            textPRType.setText(prType);

            // Format values based on PR type
            String newValue = formatPRValue(pr);
            textNewValue.setText(newValue);

            // For now, show "—" for old value since we don't fetch historical PRs
            // In a real implementation, you'd fetch the previous PR value
            textOldValue.setText("—");
        }

        private String formatPRValue(PersonalRecord pr) {
            String recordType = pr.getRecordType();
            
            if (recordType.equals(Constants.PR_TYPE_WEIGHT)) {
                return String.format("%.1f kg × %d", pr.getValue(), pr.getReps());
            } else if (recordType.equals(Constants.PR_TYPE_REPS)) {
                return String.format("%.1f kg × %d reps", pr.getValue(), pr.getReps());
            } else if (recordType.equals(Constants.PR_TYPE_VOLUME)) {
                return String.format("%.1f kg", pr.getValue());
            }
            
            return "";
        }
    }
}
