package com.fittrackpro. app.ui.diet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation. NonNull;
import androidx.annotation.Nullable;
import androidx. fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fittrackpro.app.R;
import com.fittrackpro.app.databinding.FragmentDietBinding;
import com.fittrackpro.app.ui.diet.adapter.MealAdapter;
import com.fittrackpro.app.util.Constants;
import com.google.firebase.auth.FirebaseAuth;

/**
 * DietFragment displays daily nutrition tracking.
 */
public class DietFragment extends Fragment {

    private FragmentDietBinding binding;
    private DietViewModel viewModel;
    private MealAdapter mealAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDietBinding.inflate(inflater, container, false);
        return binding. getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DietViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupListeners();

        // Get current user ID
        String userId = FirebaseAuth. getInstance().getCurrentUser().getUid();
        viewModel.setUserId(userId);
    }

    private void setupRecyclerView() {
        mealAdapter = new MealAdapter();
        binding.recyclerMeals.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMeals. setAdapter(mealAdapter);
    }

    private void setupObservers() {
        // Nutrition profile
        viewModel.getNutritionProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                binding.layoutNutritionSetup.setVisibility(View. GONE);
                binding.layoutNutritionDashboard.setVisibility(View. VISIBLE);

                binding.textTargetCalories.setText(String.format("%. 0f", profile.getTargetCalories()));
                binding. textTargetProtein.setText(String.format("%.0fg", profile.getTargetProtein()));
                binding.textTargetCarbs.setText(String.format("%.0fg", profile.getTargetCarbs()));
                binding.textTargetFats.setText(String.format("%. 0fg", profile.getTargetFats()));
            } else {
                binding.layoutNutritionSetup.setVisibility(View.VISIBLE);
                binding.layoutNutritionDashboard.setVisibility(View.GONE);
            }
        });

        // Today's meals
        viewModel.getTodayMeals().observe(getViewLifecycleOwner(), meals -> {
            if (meals != null) {
                mealAdapter.submitList(meals);
            }
        });

        // Today's totals
        viewModel.getTodayCalories().observe(getViewLifecycleOwner(), calories -> {
            binding.textCurrentCalories.setText(String.format("%.0f", calories != null ? calories : 0.0));
            updateProgress(binding.progressCalories, calories,
                    viewModel.getNutritionProfile().getValue() != null ?
                            viewModel.getNutritionProfile().getValue().getTargetCalories() : 2000);
        });

        viewModel.getTodayProtein().observe(getViewLifecycleOwner(), protein -> {
            binding.textCurrentProtein.setText(String.format("%. 0fg", protein != null ? protein : 0.0));
            updateProgress(binding.progressProtein, protein,
                    viewModel.getNutritionProfile().getValue() != null ?
                            viewModel.getNutritionProfile().getValue().getTargetProtein() : 150);
        });

        viewModel.getTodayCarbs().observe(getViewLifecycleOwner(), carbs -> {
            binding.textCurrentCarbs.setText(String.format("%. 0fg", carbs != null ?  carbs : 0.0));
            updateProgress(binding.progressCarbs, carbs,
                    viewModel.getNutritionProfile().getValue() != null ?
                            viewModel.getNutritionProfile().getValue().getTargetCarbs() : 200);
        });

        viewModel.getTodayFats().observe(getViewLifecycleOwner(), fats -> {
            binding.textCurrentFats.setText(String.format("%. 0fg", fats != null ?  fats : 0.0));
            updateProgress(binding.progressFats, fats,
                    viewModel.getNutritionProfile().getValue() != null ?
                            viewModel.getNutritionProfile().getValue().getTargetFats() : 65);
        });
    }

    private void updateProgress(android.widget.ProgressBar progressBar, Double current, double target) {
        if (current != null && target > 0) {
            int progress = (int) ((current / target) * 100);
            progressBar.setProgress(Math.min(progress, 100));
        }
    }

    private void setupListeners() {
        binding.buttonSetupProfile.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_diet_to_setupNutritionProfile);
        });

        binding.buttonAddBreakfast.setOnClickListener(v -> navigateToFoodSearch(Constants.MEAL_BREAKFAST));
        binding.buttonAddLunch.setOnClickListener(v -> navigateToFoodSearch(Constants.MEAL_LUNCH));
        binding.buttonAddDinner.setOnClickListener(v -> navigateToFoodSearch(Constants.MEAL_DINNER));
        binding.buttonAddSnacks.setOnClickListener(v -> navigateToFoodSearch(Constants.MEAL_SNACKS));
    }

    private void navigateToFoodSearch(String mealType) {
        Bundle args = new Bundle();
        args.putString("mealType", mealType);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_to_foodSearch, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}