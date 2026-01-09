package com.fittrackpro.app.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentRegisterBinding;
import com.fittrackpro.app.data.repository.AuthRepository;
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository = new AuthRepository();

        setupUsernameValidation();

        binding.buttonBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        binding.buttonRegister.setOnClickListener(v -> handleRegister());

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

                if (username.length() < Constants.MIN_USERNAME_LENGTH) {
                    binding.editUsername.setError(requireContext().getString(R.string.username_min_length, Constants.MIN_USERNAME_LENGTH));
                    usernameAvailable = false;
                    return;
                }

                if (username.length() > Constants.MAX_USERNAME_LENGTH) {
                    binding.editUsername.setError(requireContext().getString(R.string.username_max_length, Constants.MAX_USERNAME_LENGTH));
                    usernameAvailable = false;
                    return;
                }

                // Check availability
                authRepository.isUsernameAvailable(username).observe(getViewLifecycleOwner(), available -> {
                    if (available == null) {
                        // Error checking username - could be network issue
                        binding.textUsernameStatus.setText("Error checking username");
                        binding.textUsernameStatus.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.md_theme_error)
                        );
                        usernameAvailable = false;
                        binding.textUsernameStatus.setVisibility(View.VISIBLE);
                    } else if (available) {
                        // Username is available
                        binding.textUsernameStatus.setText(R.string.username_available);
                        binding.textUsernameStatus.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.emerald_green)
                        );
                        usernameAvailable = true;
                        binding.textUsernameStatus.setVisibility(View.VISIBLE);
                    } else {
                        // Username is taken
                        binding.textUsernameStatus.setText(R.string.username_taken);
                        binding.textUsernameStatus.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.md_theme_error)
                        );
                        usernameAvailable = false;
                        binding.textUsernameStatus.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void handleRegister() {
        String email = binding.editEmail.getText().toString().trim();
        String username = binding.editUsername.getText().toString().trim();
        String displayName = binding.editDisplayName.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();
        String confirmPassword = binding.editConfirmPassword.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            binding.editEmail.setError(getString(R.string.email_required));
            return;
        }

        if (username.isEmpty()) {
            binding.editUsername.setError(getString(R.string.username_required));
            return;
        }

        if (!usernameAvailable) {
            Toast.makeText(requireContext(), R.string.username_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        if (displayName.isEmpty()) {
            binding.editDisplayName.setError(getString(R.string.display_name_required));
            return;
        }

        if (password.isEmpty()) {
            binding.editPassword.setError(getString(R.string.password_required));
            return;
        }

        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            binding.editPassword.setError(getString(R.string.password_min_length, Constants.MIN_PASSWORD_LENGTH));
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.editConfirmPassword.setError(getString(R.string.passwords_dont_match));
            return;
        }

        // Show progress
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonRegister.setEnabled(false);

        // Register
        authRepository.registerUser(email, password, username, displayName)
                .observe(getViewLifecycleOwner(), result -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.buttonRegister.setEnabled(true);

                    if (result.isSuccess()) {
                        Toast.makeText(requireContext(), R.string.registration_successful, Toast.LENGTH_SHORT).show();

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