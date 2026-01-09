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
import androidx.viewpager2.widget.ViewPager2;

import com.fittrackpro.app.databinding.FragmentWorkoutBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Main Workout container fragment with Browse Programs and My Programs tabs
 */
public class WorkoutFragment extends Fragment {

    private FragmentWorkoutBinding binding;
    private WorkoutPagerAdapter pagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPager();
    }

    private void setupViewPager() {
        pagerAdapter = new WorkoutPagerAdapter(requireActivity());
        binding.viewPager.setAdapter(pagerAdapter);

        // Setup TabLayout with ViewPager2
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Browse Programs");
                            break;
                        case 1:
                            tab.setText("My Programs");
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
     * ViewPager adapter for Browse Programs and My Programs tabs
     */
    private static class WorkoutPagerAdapter extends FragmentStateAdapter {

        public WorkoutPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new BrowseProgramsFragment();
                case 1:
                    return new MyProgramsFragment();
                default:
                    return new BrowseProgramsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Browse Programs and My Programs
        }
    }
}
