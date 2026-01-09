package com.fittrackpro.app.ui.workout;

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
import com.fittrackpro.app.databinding.FragmentMyProgramsBinding;
import com.fittrackpro.app.ui.workout.adapter.MyProgramsAdapter;

/**
 * Fragment displaying user's active workout programs
 */
public class MyProgramsFragment extends Fragment {

    private FragmentMyProgramsBinding binding;
    private WorkoutHubViewModel viewModel;
    private MyProgramsAdapter programsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyProgramsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(WorkoutHubViewModel.class);

        setupRecyclerView();
        setupListeners();
        observePrograms();
    }

    private void setupRecyclerView() {
        programsAdapter = new MyProgramsAdapter(program -> {
            // Handle start workout click
            // TODO: Navigate to ActiveWorkoutFragment with program details
        });

        binding.recyclerMyPrograms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMyPrograms.setAdapter(programsAdapter);
    }

    private void setupListeners() {
        // FAB click - create custom program
        binding.fabCreateProgram.setOnClickListener(v -> {
            // Navigate from activity's nav controller since we're in a nested fragment
            androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.addRoutineFragment);
        });

        // Browse programs button in empty state
        binding.buttonBrowsePrograms.setOnClickListener(v -> {
            // Switch to Browse Programs tab (tab 0)
            if (getParentFragment() instanceof WorkoutFragment) {
                // Access parent fragment's ViewPager - but this is tricky
                // For now, just navigate to add routine
                androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.presetProgramListFragment);
            }
        });

        // Create custom button in empty state
        binding.buttonCreateCustom.setOnClickListener(v -> {
            androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.createCustomProgramFragment);
        });
    }

    private void observePrograms() {
        viewModel.getUserPrograms().observe(getViewLifecycleOwner(), programs -> {
            if (programs != null) {
                if (programs.isEmpty()) {
                    binding.emptyState.setVisibility(View.VISIBLE);
                    binding.recyclerMyPrograms.setVisibility(View.GONE);
                } else {
                    binding.emptyState.setVisibility(View.GONE);
                    binding.recyclerMyPrograms.setVisibility(View.VISIBLE);
                    programsAdapter.submitList(programs);
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
