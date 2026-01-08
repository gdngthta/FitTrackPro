package com.fittrackpro.app.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.databinding.FragmentDashboardBinding;
import com.fittrackpro.app.ui.dashboard.adapter.RecentWorkoutAdapter;
import com.google.firebase.auth.FirebaseAuth;

/**
 * DashboardFragment displays user stats, recent workouts, and leaderboard preview.
 *
 * Layout must:
 * - Use NestedScrollView as root
 * - Display stat cards in flexible layout (2x2 grid or vertical on small screens)
 * - Show RecyclerView for recent workouts
 * - Show leaderboard preview (top 3)
 * - Use ConstraintLayout with proper constraints
 */
public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private RecentWorkoutAdapter workoutAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel. class);

        setupRecyclerView();
        setupObservers();

        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewModel.setUserId(userId);

        // Setup swipe refresh
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.refreshData();
            binding.swipeRefresh.setRefreshing(false);
        });
    }

    private void setupRecyclerView() {
        workoutAdapter = new RecentWorkoutAdapter();
        binding.recyclerRecentWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerRecentWorkouts.setAdapter(workoutAdapter);
    }

    private void setupObservers() {
        // Observe user data
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.textWelcome.setText("Welcome, " + user.getDisplayName());
                binding.textTotalWorkouts.setText(String.valueOf(user.getTotalWorkouts()));
                binding.textCurrentStreak.setText(user.getCurrentStreak() + " days");
                binding.textTotalVolume.setText(String.format("%.1f kg", user.getTotalVolumeLifted()));
                binding.textActivePrograms.setText(String.valueOf(user.getActivePrograms()));
            }
        });

        // Observe recent workouts
        viewModel.getRecentWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null) {
                workoutAdapter.submitList(workouts);
                binding.emptyStateWorkouts.setVisibility(workouts.isEmpty() ? View.VISIBLE : View. GONE);
            }
        });

        // Observe leaderboard preview
        viewModel.getLeaderboardPreview().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null && ! entries.isEmpty()) {
                // Display top 3 in UI (assumption: layout has top1, top2, top3 views)
                if (entries.size() > 0) {
                    binding.textTop1Name.setText(entries.get(0).getUsername());
                    binding.textTop1Volume.setText(String.format("%. 1f kg", entries.get(0).getTotalVolume()));
                }
                if (entries.size() > 1) {
                    binding.textTop2Name.setText(entries. get(1).getUsername());
                    binding.textTop2Volume.setText(String.format("%. 1f kg", entries.get(1).getTotalVolume()));
                }
                if (entries. size() > 2) {
                    binding.textTop3Name.setText(entries.get(2).getUsername());
                    binding.textTop3Volume.setText(String.format("%.1f kg", entries.get(2).getTotalVolume()));
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}