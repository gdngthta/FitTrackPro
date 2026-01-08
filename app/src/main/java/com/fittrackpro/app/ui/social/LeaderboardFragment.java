package com.fittrackpro.app.ui.social;

import android.os.Bundle;
import android.view. LayoutInflater;
import android. view.View;
import android. view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.databinding.FragmentLeaderboardBinding;
import com.fittrackpro. app.ui.social.adapter. LeaderboardAdapter;
import com.google.firebase.auth.FirebaseAuth;

/**
 * LeaderboardFragment displays global or friends-only leaderboard.
 */
public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private LeaderboardViewModel viewModel;
    private LeaderboardAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LeaderboardViewModel. class);

        setupRecyclerView();
        setupObservers();
        setupListeners();

        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not logged in, return to auth
            requireActivity().finish();
            return;
        }
        String userId = currentUser.getUid();
        viewModel.setUserId(userId);
    }

    private void setupRecyclerView() {
        adapter = new LeaderboardAdapter();
        binding.recyclerLeaderboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerLeaderboard.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel. getLeaderboard().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null) {
                adapter.submitList(entries);
                binding. emptyState.setVisibility(entries.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getShowFriendsOnly().observe(getViewLifecycleOwner(), friendsOnly -> {
            if (friendsOnly != null) {
                binding.chipGlobal.setChecked(! friendsOnly);
                binding.chipFriends.setChecked(friendsOnly);
            }
        });
    }

    private void setupListeners() {
        binding.chipGlobal.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.getShowFriendsOnly().getValue())) {
                viewModel.toggleLeaderboardMode();
            }
        });

        binding.chipFriends.setOnClickListener(v -> {
            if (Boolean. FALSE.equals(viewModel.getShowFriendsOnly().getValue())) {
                viewModel.toggleLeaderboardMode();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}