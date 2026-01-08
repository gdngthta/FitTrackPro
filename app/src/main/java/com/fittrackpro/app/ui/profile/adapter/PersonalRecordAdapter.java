package com.fittrackpro.app.ui.profile.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview. widget.RecyclerView;

import com.fittrackpro.app.databinding.ItemPersonalRecordBinding;
import com.fittrackpro.app.data.model.PersonalRecord;
import com.fittrackpro.app.util.TimeUtils;

/**
 * Adapter for personal records list.
 */
public class PersonalRecordAdapter extends ListAdapter<PersonalRecord, PersonalRecordAdapter.RecordViewHolder> {

    public PersonalRecordAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<PersonalRecord> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PersonalRecord>() {
                @Override
                public boolean areItemsTheSame(@NonNull PersonalRecord oldItem, @NonNull PersonalRecord newItem) {
                    return oldItem.getRecordId().equals(newItem.getRecordId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull PersonalRecord oldItem, @NonNull PersonalRecord newItem) {
                    return oldItem.getRecordId().equals(newItem.getRecordId()) &&
                            oldItem.getValue() == newItem.getValue();
                }
            };

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPersonalRecordBinding binding = ItemPersonalRecordBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new RecordViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        private final ItemPersonalRecordBinding binding;

        public RecordViewHolder(ItemPersonalRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PersonalRecord record) {
            binding.textExerciseName.setText(record. getExerciseName());
            binding.textRecordType.setText(record.getRecordType().toUpperCase());

            String value = "";
            switch (record.getRecordType()) {
                case "weight":
                    value = String. format("%.1f kg", record.getValue());
                    break;
                case "reps":
                    value = String.format("%d reps @ %. 1f kg", record.getReps(), record.getValue());
                    break;
                case "volume":
                    value = String. format("%.1f kg total", record.getValue());
                    break;
            }
            binding.textRecordValue.setText(value);
            binding.textAchievedDate.setText(
                    TimeUtils.formatDate(record.getAchievedAt().toDate())
            );
        }
    }
}