package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.view. LayoutInflater;
import android. view.View;
import android. view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fittrackpro.app.databinding.FragmentWorkoutSummaryBinding;
import com.fittrackpro.app.util. EquivalenceCalculator;
import com.fittrackpro.app.util.TimeUtils;

/**
 * WorkoutSummaryFragment displays post-workout summary with PRs and stats.
 */
public class WorkoutSummaryFragment extends Fragment {

    private FragmentWorkoutSummaryBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutSummaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            long durationSeconds = getArguments().getLong("durationSeconds", 0);
            double totalVolume = getArguments().getDouble("totalVolume", 0.0);
            int totalSets = getArguments().getInt("totalSets", 0);
            int totalExercises = getArguments().getInt("totalExercises", 0);
            String[] prs = getArguments().getStringArray("prs");

            displaySummary(durationSeconds, totalVolume, totalSets, totalExercises, prs);
        }

        binding.buttonFinish.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    private void displaySummary(long durationSeconds, double totalVolume, int totalSets,
                                int totalExercises, String[] prs) {
        binding.textDuration.setText(TimeUtils.formatDuration(durationSeconds));
        binding.textVolume.setText(String.format("%.1f kg", totalVolume));
        binding.textSets.setText(String.valueOf(totalSets));
        binding.textExercises.setText(String.valueOf(totalExercises));

        // Show equivalence
        String equivalence = EquivalenceCalculator. getEquivalence(totalVolume);
        binding.textEquivalence.setText("That's like lifting " + equivalence + "!");

        // Show PRs
        if (prs != null && prs.length > 0) {
            binding.layoutPrs.setVisibility(View.VISIBLE);
            StringBuilder prText = new StringBuilder();
            for (String pr : prs) {
                prText.append("🎉 ").append(pr).append("\n");
            }
            binding. textPrs.setText(prText.toString());
        } else {
            binding.layoutPrs. setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}