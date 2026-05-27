package com.cookbook.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cookbook.app.api.ApiClient;
import com.cookbook.app.api.model.Category;
import com.cookbook.app.api.model.CategoryListResponse;
import com.cookbook.app.api.model.MealListResponse;
import com.cookbook.app.api.model.MealSummary;
import com.cookbook.app.databinding.FragmentHomeBinding;
import com.cookbook.app.ui.adapter.RecipeAdapter;
import com.cookbook.app.ui.detail.RecipeDetailActivity;
import com.cookbook.app.util.Constants;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecipeAdapter adapter;
    private String selectedCategory = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new RecipeAdapter();
        binding.recyclerRecipes.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerRecipes.setAdapter(adapter);

        adapter.setOnItemClickListener((id, source) -> {
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra(Constants.EXTRA_RECIPE_ID, id);
            intent.putExtra(Constants.EXTRA_RECIPE_SOURCE, source);
            startActivity(intent);
        });

        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(false);
            loadCategories();
        });

        loadCategories();
    }

    private void loadCategories() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyView.setVisibility(View.GONE);

        ApiClient.getInstance().getCategories().enqueue(new Callback<CategoryListResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryListResponse> call,
                                   @NonNull Response<CategoryListResponse> response) {
                if (!isAdded()) return;
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCategories() != null) {
                    List<Category> categories = response.body().getCategories();
                    buildCategoryChips(categories);
                    if (!categories.isEmpty()) {
                        selectedCategory = categories.get(0).getName();
                        loadMealsByCategory(selectedCategory);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryListResponse> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                binding.progressBar.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(),
                        "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildCategoryChips(List<Category> categories) {
        binding.chipGroupCategories.removeAllViews();

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            Chip chip = new Chip(requireContext());
            chip.setText(category.getName());
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            if (i == 0) chip.setChecked(true);

            chip.setOnClickListener(v -> {
                if (!category.getName().equals(selectedCategory)) {
                    selectedCategory = category.getName();
                    loadMealsByCategory(selectedCategory);
                }
            });

            binding.chipGroupCategories.addView(chip);
        }
    }

    private void loadMealsByCategory(String category) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyView.setVisibility(View.GONE);
        adapter.setItems(new ArrayList<>());

        ApiClient.getInstance().getMealsByCategory(category)
                .enqueue(new Callback<MealListResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MealListResponse> call,
                                           @NonNull Response<MealListResponse> response) {
                        if (!isAdded()) return;
                        binding.progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getMeals() != null) {
                            List<RecipeAdapter.RecipeItem> items = new ArrayList<>();
                            for (MealSummary meal : response.body().getMeals()) {
                                items.add(new RecipeAdapter.RecipeItem(
                                        meal.getId(), meal.getName(),
                                        meal.getThumbnail(), category,
                                        Constants.SOURCE_API));
                            }
                            adapter.setItems(items);
                            binding.emptyView.setVisibility(
                                    items.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MealListResponse> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        binding.progressBar.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(),
                                "Błąd ładowania przepisów", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}