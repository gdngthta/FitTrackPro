package com.fittrackpro. app.ui.diet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view. View;
import android.view. ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.databinding.FragmentFoodSearchBinding;
import com.fittrackpro.app.data.local.AppDatabase;
import com. fittrackpro.app. data.model.Food;
import com.fittrackpro. app.data.repository.NutritionRepository;
import com.fittrackpro.app.ui.diet.adapter.FoodSearchAdapter;
import com.google.firebase.auth.FirebaseAuth;

/**
 * FoodSearchFragment allows searching and selecting food items.
 */
public class FoodSearchFragment extends Fragment {

    private FragmentFoodSearchBinding binding;
    private NutritionRepository nutritionRepository;
    private FoodSearchAdapter adapter;
    private DietViewModel viewModel;
    private String mealType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFoodSearchBinding.inflate(inflater, container, false);
        return binding. getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nutritionRepository = new NutritionRepository(AppDatabase.getInstance(requireContext()));
        viewModel = new ViewModelProvider(this).get(DietViewModel.class);

        if (getArguments() != null) {
            mealType = getArguments().getString("mealType");
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewModel.setUserId(userId);

        setupRecyclerView();
        setupSearch();
    }

    private void setupRecyclerView() {
        adapter = new FoodSearchAdapter(food -> {
            showPortionDialog(food);
        });

        binding.recyclerFoodResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerFoodResults.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    searchFood(query);
                }
            }
        });
    }

    private void searchFood(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);

        nutritionRepository.searchFoods(query).observe(getViewLifecycleOwner(), foods -> {
            binding.progressBar.setVisibility(View.GONE);

            if (foods != null) {
                adapter.submitList(foods);
                binding.emptyState.setVisibility(foods.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showPortionDialog(Food food) {
        View dialogView = getLayoutInflater().inflate(com.fittrackpro.app.R.layout.dialog_portion_selection, null);

        com.google.android.material.textfield. TextInputEditText editPortion =
                dialogView.findViewById(com.fittrackpro.app.R.id.editPortion);
        android.widget.TextView textServingInfo =
                dialogView.findViewById(com.fittrackpro.app.R.id.textServingInfo);

        textServingInfo.setText("1 serving = " + food.getServingSize() + food.getServingUnit());
        editPortion.setText("1. 0");

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add " + food.getFoodName())
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String portionStr = editPortion.getText().toString().trim();
                    double portion = portionStr.isEmpty() ? 1.0 : Double.parseDouble(portionStr);

                    logFood(food, portion);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logFood(Food food, double portionMultiplier) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        viewModel.logMeal(
                food.getFoodId(),
                food.getFoodName(),
                mealType,
                portionMultiplier,
                food.getCalories(),
                food.getProtein(),
                food.getCarbs(),
                food.getFats()
        ).observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                android.widget.Toast.makeText(requireContext(), "Food logged!", android.widget.Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            } else {
                android.widget. Toast.makeText(requireContext(), "Failed to log food", android. widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}