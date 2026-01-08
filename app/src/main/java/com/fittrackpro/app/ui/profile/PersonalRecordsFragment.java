package com.fittrackpro.app.ui.profile;

import android.os.Bundle;
import android.view. LayoutInflater;
import android. view.View;
import android. view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget. LinearLayoutManager;

import com.fittrackpro.app.databinding.FragmentPersonalRecordsBinding;
import com. fittrackpro.app. ui.profile.adapter.PersonalRecordAdapter;
import com.google. firebase.auth.FirebaseAuth;

/**
 * PersonalRecordsFragment displays all user PRs.
 */
public class PersonalRecordsFragment extends Fragment {

    private FragmentPersonalRecordsBinding binding;
    private ProfileViewModel viewModel;
    private PersonalRecordAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonalRecordsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupRecyclerView();

        String userId = FirebaseAuth. getInstance().getCurrentUser().getUid();
        viewModel.setUserId(userId);

        viewModel.getPersonalRecords().observe(getViewLifecycleOwner(), records -> {
            if (records != null) {
                adapter.submitList(records);
                binding.emptyState.setVisibility(records.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new PersonalRecordAdapter();
        binding.recyclerPrs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPrs.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}