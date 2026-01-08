package com.fittrackpro.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text. Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation. Nullable;
import androidx.fragment.app.Fragment;

import com.fittrackpro.app.databinding.FragmentRegisterBinding;
import com.fittrackpro.app.data.repository. AuthRepository;
import com.fittrackpro.app.ui.main.MainActivity;
import com.fittrackpro.app.util.Constants;

/**
 * RegisterFragment handles new user registration with username validation.
 */
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthRepository authRepository;
    private boolean usernameAvailable = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding. getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository = new AuthRepository();

        setupUsernameValidation();

        binding. buttonRegister.setOnClickListener(v -> handleRegister());

        binding.textGoToLogin.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    private void setupUsernameValidation() {
        binding.editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String username = s.toString().trim();

                if (username. length() < Constants.MIN_USERNAME_LENGTH) {
                    binding.editUsername.setError("Username must be at least " + Constants.MIN_USERNAME_LENGTH + " characters");
                    usernameAvailable = false;
                    return;
                }

                if (username.length() > Constants.MAX_USERNAME_LENGTH) {
                    binding.editUsername.setError("Username must be less than " + Constants.MAX_USERNAME_LENGTH + " characters");
                    usernameAvailable = false;
                    return;
                }

                // Check availability
                authRepository.isUsernameAvailable(username).observe(getViewLifecycleOwner(), available -> {
                    if (available != null && available) {
                        binding.textUsernameStatus.setText("✓ Username available");
                        binding.textUsernameStatus.setTextColor(
                                getResources().getColor(android.R.color.holo_green_dark, null)
                        );
                        usernameAvailable = true;
                    } else {
                        binding.textUsernameStatus.setText("✗ Username taken");
                        binding.textUsernameStatus.setTextColor(
                                getResources().getColor(android.R.color. holo_red_dark, null)
                        );
                        usernameAvailable = false;
                    }
                    binding.textUsernameStatus.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void handleRegister() {
        String email = binding.editEmail.getText().toString().trim();
        String username = binding.editUsername.getText().toString().trim();
        String displayName = binding.editDisplayName. getText().toString().trim();
        String password = binding.editPassword. getText().toString().trim();
        String confirmPassword = binding.editConfirmPassword.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            binding.editEmail.setError("Email is required");
            return;
        }

        if (username.isEmpty()) {
            binding.editUsername.setError("Username is required");
            return;
        }

        if (! usernameAvailable) {
            Toast.makeText(requireContext(), "Username not available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (displayName.isEmpty()) {
            binding.editDisplayName.setError("Display name is required");
            return;
        }

        if (password.isEmpty()) {
            binding.editPassword.setError("Password is required");
            return;
        }

        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            binding.editPassword.setError("Password must be at least " + Constants. MIN_PASSWORD_LENGTH + " characters");
            return;
        }

        if (!password. equals(confirmPassword)) {
            binding.editConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Show progress
        binding.progressBar.setVisibility(View. VISIBLE);
        binding.buttonRegister.setEnabled(false);

        // Register
        authRepository.registerUser(email, password, username, displayName)
                .observe(getViewLifecycleOwner(), result -> {
                    binding.progressBar. setVisibility(View.GONE);
                    binding.buttonRegister.setEnabled(true);

                    if (result.isSuccess()) {
                        Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to MainActivity
                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        Toast.makeText(requireContext(), result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}