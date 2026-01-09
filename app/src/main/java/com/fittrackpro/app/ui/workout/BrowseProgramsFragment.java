package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.fittrackpro.app.databinding.FragmentBrowseProgramsBinding;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Browse Programs fragment with difficulty-based tabs (Beginner/Intermediate/Advanced)
 */
public class BrowseProgramsFragment extends Fragment {

    private FragmentBrowseProgramsBinding binding;
    private BrowseProgramsPagerAdapter pagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBrowseProgramsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPager();
    }

    private void setupViewPager() {
        pagerAdapter = new BrowseProgramsPagerAdapter(requireActivity());
        binding.viewPagerDifficulty.setAdapter(pagerAdapter);

        // Setup difficulty TabLayout with ViewPager2
        new TabLayoutMediator(binding.tabLayoutDifficulty, binding.viewPagerDifficulty,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Beginner");
                            break;
                        case 1:
                            tab.setText("Intermediate");
                            break;
                        case 2:
                            tab.setText("Advanced");
                            break;
                    }
                }
        ).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * ViewPager adapter for difficulty levels
     */
    private static class BrowseProgramsPagerAdapter extends FragmentStateAdapter {

        public BrowseProgramsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Create ProgramListFragment for each difficulty level
            String difficulty;
            switch (position) {
                case 0:
                    difficulty = "Beginner";
                    break;
                case 1:
                    difficulty = "Intermediate";
                    break;
                case 2:
                    difficulty = "Advanced";
                    break;
                default:
                    difficulty = "Beginner";
            }
            return ProgramListFragment.newInstance(difficulty);
        }

        @Override
        public int getItemCount() {
            return 3; // Beginner, Intermediate, Advanced
        }
    }
}
