package com.fittrackpro.app.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentDashboardBinding;
import com.fittrackpro.app.ui.dashboard.adapter.RecentWorkoutAdapter;
import com.google.firebase.auth.FirebaseAuth;

/**
 * DashboardFragment displays user stats, recent workouts, and quick actions.
 *
 * Layout features:
 * - Time-based greeting
 * - Statistics cards (This Week, This Month)
 * - Overall Progress section
 * - Active Program card
 * - Recent Activity section
 * - Quick Action cards for navigation
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

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupClickListeners();

        // Get current user ID
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not logged in, return to auth
            requireActivity().finish();
            return;
        }
        String userId = currentUser.getUid();
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
        // Observe greeting
        viewModel.getGreeting().observe(getViewLifecycleOwner(), greeting -> {
            if (greeting != null) {
                binding.textGreeting.setText(greeting);
            }
        });

        // Observe user data
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.textUsername.setText(user.getDisplayName());
                binding.textTotalWorkouts.setText(String.valueOf(user.getTotalWorkouts()));
                
                // Format volume as X.Xk
                double volumeInK = user.getTotalVolumeLifted() / 1000.0;
                binding.textTotalVolume.setText(String.format("%.1fk", volumeInK));
            }
        });

        // Observe this week workouts
        viewModel.getThisWeekWorkouts().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                binding.textThisWeek.setText(String.valueOf(count));
            }
        });

        // Observe this month workouts
        viewModel.getThisMonthWorkouts().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                binding.textThisMonth.setText(String.valueOf(count));
            }
        });

        // Observe average workout duration
        viewModel.getAvgWorkoutDuration().observe(getViewLifecycleOwner(), avgMinutes -> {
            if (avgMinutes != null) {
                binding.textAvgDuration.setText(String.valueOf(avgMinutes));
            }
        });

        // Observe active program
        viewModel.getActiveProgram().observe(getViewLifecycleOwner(), program -> {
            if (program != null) {
                binding.textNoActiveProgram.setVisibility(View.GONE);
                binding.buttonCreateProgram.setVisibility(View.GONE);
                // TODO: Show program details when UI is expanded
            } else {
                binding.textNoActiveProgram.setVisibility(View.VISIBLE);
                binding.buttonCreateProgram.setVisibility(View.VISIBLE);
            }
        });

        // Observe recent workouts
        viewModel.getRecentWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null) {
                workoutAdapter.submitList(workouts);
                if (workouts.isEmpty()) {
                    binding.recyclerRecentWorkouts.setVisibility(View.GONE);
                    binding.emptyStateWorkouts.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerRecentWorkouts.setVisibility(View.VISIBLE);
                    binding.emptyStateWorkouts.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupClickListeners() {
        // Settings navigation
        binding.buttonSettings.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.settingsFragment)
        );

        // Create Program button
        binding.buttonCreateProgram.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.addRoutineFragment)
        );

        // Start First Workout button
        binding.buttonStartFirstWorkout.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.workoutHubFragment)
        );

        // Browse Programs action card
        binding.cardBrowsePrograms.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.addRoutineFragment)
        );

        // Track Nutrition action card
        binding.cardTrackNutrition.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.dietFragment)
        );

        // Leaderboard action card
        binding.cardLeaderboard.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.leaderboardFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}