package com.fittrackpro.app.ui.social.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview. widget.RecyclerView;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding. ItemLeaderboardBinding;
import com.fittrackpro. app.data.model.LeaderboardEntry;

/**
 * Adapter for leaderboard display.
 */
public class LeaderboardAdapter extends ListAdapter<LeaderboardEntry, LeaderboardAdapter.LeaderboardViewHolder> {

    public LeaderboardAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<LeaderboardEntry> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<LeaderboardEntry>() {
                @Override
                public boolean areItemsTheSame(@NonNull LeaderboardEntry oldItem, @NonNull LeaderboardEntry newItem) {
                    return oldItem.getUserId().equals(newItem.getUserId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull LeaderboardEntry oldItem, @NonNull LeaderboardEntry newItem) {
                    return oldItem. getUserId().equals(newItem.getUserId()) &&
                            oldItem.getTotalVolume() == newItem.getTotalVolume() &&
                            oldItem. getRank() == newItem.getRank();
                }
            };

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLeaderboardBinding binding = ItemLeaderboardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new LeaderboardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        private final ItemLeaderboardBinding binding;

        public LeaderboardViewHolder(ItemLeaderboardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(LeaderboardEntry entry) {
            binding.textRank.setText("#" + entry.getRank());
            binding.textUsername.setText(entry.getUsername());
            binding.textDisplayName.setText(entry.getDisplayName());
            binding.textVolume.setText(String.format("%.1f kg", entry.getTotalVolume()));

            // Highlight current user
            if (entry.isCurrentUser()) {
                binding. cardLeaderboard.setCardBackgroundColor(
                        ContextCompat.getColor(binding.getRoot().getContext(), R.color.highlight_color)
                );
            } else {
                binding.cardLeaderboard.setCardBackgroundColor(
                        ContextCompat.getColor(binding.getRoot().getContext(), R.color.card_background)
                );
            }

            // Special styling for top 3
            if (entry.getRank() == 1) {
                binding. textRank.setText("🥇");
            } else if (entry.getRank() == 2) {
                binding.textRank.setText("🥈");
            } else if (entry.getRank() == 3) {
                binding. textRank.setText("🥉");
            }
        }
    }
}