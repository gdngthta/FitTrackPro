package com.fittrackpro. app.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentProfileBinding;
import com.fittrackpro.app.data.local.AppDatabase;
import com.fittrackpro.app.data. repository.AuthRepository;
import com.fittrackpro.app.data. repository.UserRepository;
import com.fittrackpro.app.ui.auth.AuthActivity;
import com.fittrackpro. app.util.Constants;
import com.google.firebase.auth.FirebaseAuth;

/**
 * ProfileFragment displays user profile and settings.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private AuthRepository authRepository;
    private UserRepository userRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding. getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        authRepository = new AuthRepository();
        userRepository = new UserRepository(AppDatabase.getInstance(requireContext()), requireContext());

        setupObservers();
        setupListeners();

        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewModel.setUserId(userId);
    }

    private void setupObservers() {
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.textDisplayName.setText(user.getDisplayName());
                binding.textUsername.setText("@" + user.getUsername());

                // Stats
                binding.textTotalWorkouts.setText(String.valueOf(user.getTotalWorkouts()));
                binding.textStreak.setText(String.valueOf(user.getCurrentStreak()));
                binding.textTotalVolume.setText(String.format("%.1fK", user.getTotalVolumeLifted() / 1000));
            }
        });
    }

    private void setupListeners() {
        binding.buttonViewPrs.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_to_personalRecords);
        });

        binding.buttonSettings.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_to_settings);
        });

        binding.buttonLogout. setOnClickListener(v -> handleLogout());
    }

    private void handleLogout() {
        // Clear local cache
        userRepository.clearCache();

        // Clear shared preferences
        SharedPreferences prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, android.content.Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Firebase logout
        authRepository.logout();

        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();

        // Navigate to AuthActivity
        Intent intent = new Intent(requireActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}