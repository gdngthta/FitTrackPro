package com.fittrackpro. app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx. fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentLoginBinding;
import com.fittrackpro.app.data.repository.AuthRepository;
import com. fittrackpro.app. ui.main.MainActivity;

/**
 * LoginFragment handles user login with email and password.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthRepository authRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository = new AuthRepository();

        binding.buttonBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        binding.buttonLogin.setOnClickListener(v -> handleLogin());

        binding.textGoToRegister.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_login_to_register);
        });
    }

    private void handleLogin() {
        String email = binding.editEmail.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            binding.editEmail.setError("Email is required");
            return;
        }

        if (password.isEmpty()) {
            binding.editPassword.setError("Password is required");
            return;
        }

        // Show progress
        binding.progressBar. setVisibility(View.VISIBLE);
        binding.buttonLogin. setEnabled(false);

        // Login
        authRepository.loginUser(email, password).observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonLogin.setEnabled(true);

            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();

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