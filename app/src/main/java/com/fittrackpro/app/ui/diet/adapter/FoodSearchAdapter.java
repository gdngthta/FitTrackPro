package com.fittrackpro.app.ui.diet.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview. widget.RecyclerView;

import com.fittrackpro.app.databinding.ItemFoodSearchBinding;
import com. fittrackpro.app. data.model.Food;

/**
 * Adapter for food search results.
 */
public class FoodSearchAdapter extends ListAdapter<Food, FoodSearchAdapter. FoodViewHolder> {

    private final OnFoodClickListener listener;

    public interface OnFoodClickListener {
        void onFoodClick(Food food);
    }

    public FoodSearchAdapter(OnFoodClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Food> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Food>() {
                @Override
                public boolean areItemsTheSame(@NonNull Food oldItem, @NonNull Food newItem) {
                    return oldItem.getFoodId().equals(newItem.getFoodId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Food oldItem, @NonNull Food newItem) {
                    return oldItem.getFoodId().equals(newItem.getFoodId());
                }
            };

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodSearchBinding binding = ItemFoodSearchBinding.inflate(
                LayoutInflater. from(parent.getContext()), parent, false
        );
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodSearchBinding binding;

        public FoodViewHolder(ItemFoodSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Food food, OnFoodClickListener listener) {
            binding.textFoodName.setText(food.getFoodName());

            if (food.getBrand() != null && !food.getBrand().isEmpty()) {
                binding.textFoodBrand.setText(food. getBrand());
                binding.textFoodBrand. setVisibility(android.view.View.VISIBLE);
            } else {
                binding.textFoodBrand.setVisibility(android.view.View.GONE);
            }

            binding.textCalories.setText(String.format("%.0f cal", food.getCalories()));
            binding.textProtein.setText(String.format("P: %.1fg", food.getProtein()));
            binding.textCarbs.setText(String.format("C: %. 1fg", food.getCarbs()));
            binding.textFats.setText(String.format("F: %.1fg", food.getFats()));
            binding.textServing.setText(food.getServingSize() + food.getServingUnit());

            binding.cardFood.setOnClickListener(v -> listener. onFoodClick(food));
        }
    }
}