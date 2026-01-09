package com.fittrackpro.app.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentWelcomeBinding;

/**
 * WelcomeFragment serves as the landing page for the authentication flow.
 * It presents the app branding and provides options to navigate to login or registration.
 */
public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonLogin.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_welcome_to_login);
        });

        binding.buttonRegister.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_welcome_to_register);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
