package com.fittrackpro.app.ui.workout;

import android.os.Bundle;
import android.view. LayoutInflater;
import android. view.View;
import android. view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentAddRoutineBinding;

/**
 * AddRoutineFragment lets user choose between preset or custom program.
 */
public class AddRoutineFragment extends Fragment {

    private FragmentAddRoutineBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddRoutineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.cardUsePreset.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_addRoutine_to_presetList);
        });

        binding.cardCreateCustom.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_addRoutine_to_createCustom);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}