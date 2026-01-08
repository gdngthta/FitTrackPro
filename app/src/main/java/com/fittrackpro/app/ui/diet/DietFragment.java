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
        // Use individual recycler views for each meal type instead of one combined
    }

    private void setupObservers() {
        // Nutrition profile - show/hide setup button
        viewModel.getNutritionProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                binding.buttonSetupNutrition.setVisibility(View.GONE);
                binding.scrollContent.setVisibility(View.VISIBLE);
            } else {
                binding.buttonSetupNutrition.setVisibility(View.VISIBLE);
                binding.scrollContent.setVisibility(View.GONE);
            }
        });

        // Today's meals
        viewModel.getTodayMeals().observe(getViewLifecycleOwner(), meals -> {
            if (meals != null) {
                mealAdapter.submitList(meals);
            }
        });

        // Today's totals - Update progress indicators
        viewModel.getTodayCalories().observe(getViewLifecycleOwner(), calories -> {
            double target = viewModel.getNutritionProfile().getValue() != null ?
                    viewModel.getNutritionProfile().getValue().getTargetCalories() : 2000;
            binding.textCaloriesProgress.setText(String.format("%.0f / %.0f kcal", 
                    calories != null ? calories : 0.0, target));
            updateProgress(binding.progressCalories, calories, target);
        });

        viewModel.getTodayProtein().observe(getViewLifecycleOwner(), protein -> {
            double target = viewModel.getNutritionProfile().getValue() != null ?
                    viewModel.getNutritionProfile().getValue().getTargetProtein() : 150;
            binding.textProteinProgress.setText(String.format("%.0f / %.0f g", 
                    protein != null ? protein : 0.0, target));
            updateProgress(binding.progressProtein, protein, target);
        });

        viewModel.getTodayCarbs().observe(getViewLifecycleOwner(), carbs -> {
            double target = viewModel.getNutritionProfile().getValue() != null ?
                    viewModel.getNutritionProfile().getValue().getTargetCarbs() : 200;
            binding.textCarbsProgress.setText(String.format("%.0f / %.0f g", 
                    carbs != null ? carbs : 0.0, target));
            updateProgress(binding.progressCarbs, carbs, target);
        });

        viewModel.getTodayFats().observe(getViewLifecycleOwner(), fats -> {
            double target = viewModel.getNutritionProfile().getValue() != null ?
                    viewModel.getNutritionProfile().getValue().getTargetFats() : 65;
            binding.textFatsProgress.setText(String.format("%.0f / %.0f g", 
                    fats != null ? fats : 0.0, target));
            updateProgress(binding.progressFats, fats, target);
        });
    }

    private void updateProgress(com.google.android.material.progressindicator.LinearProgressIndicator progressBar, Double current, double target) {
        if (current != null && target > 0) {
            int progress = (int) ((current / target) * 100);
            progressBar.setProgress(Math.min(progress, 100));
        }
    }

    private void setupListeners() {
        binding.buttonSetupNutrition.setOnClickListener(v -> {
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