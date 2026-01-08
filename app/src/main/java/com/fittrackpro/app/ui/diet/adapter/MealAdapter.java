package com.fittrackpro.app.ui.diet.adapter;

import android.view. LayoutInflater;
import android. view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget. RecyclerView;

import com.fittrackpro.app.databinding.ItemMealLoggedBinding;
import com.fittrackpro.app.data. model.MealLogged;

/**
 * Adapter for logged meals.
 */
public class MealAdapter extends ListAdapter<MealLogged, MealAdapter.MealViewHolder> {

    public MealAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<MealLogged> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MealLogged>() {
                @Override
                public boolean areItemsTheSame(@NonNull MealLogged oldItem, @NonNull MealLogged newItem) {
                    return oldItem.getLogId().equals(newItem.getLogId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull MealLogged oldItem, @NonNull MealLogged newItem) {
                    return oldItem. getLogId().equals(newItem.getLogId());
                }
            };

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMealLoggedBinding binding = ItemMealLoggedBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new MealViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        private final ItemMealLoggedBinding binding;

        public MealViewHolder(ItemMealLoggedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MealLogged meal) {
            binding. textFoodName.setText(meal.getFoodName());
            binding.textPortion.setText(String.format("x%. 1f", meal.getPortionMultiplier()));
            binding.textCalories.setText(String.format("%.0f cal", meal. getCalories()));
            binding. textProtein.setText(String. format("P: %.1fg", meal.getProtein()));
            binding.textCarbs.setText(String.format("C: %.1fg", meal.getCarbs()));
            binding.textFats. setText(String.format("F:  %.1fg", meal.getFats()));
        }
    }
}