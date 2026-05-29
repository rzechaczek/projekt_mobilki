package com.cookbook.app.ui.myrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cookbook.app.database.entity.RecipeEntity;
import com.cookbook.app.databinding.FragmentMyRecipesBinding;
import com.cookbook.app.repository.AppRepository;
import com.cookbook.app.ui.adapter.RecipeAdapter;
import com.cookbook.app.ui.addrecipe.AddEditRecipeActivity;
import com.cookbook.app.ui.detail.RecipeDetailActivity;
import com.cookbook.app.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class MyRecipesFragment extends Fragment {

    private FragmentMyRecipesBinding binding;
    private RecipeAdapter adapter;
    private AppRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMyRecipesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = AppRepository.getInstance(requireContext());

        adapter = new RecipeAdapter();
        binding.recyclerMyRecipes.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerMyRecipes.setAdapter(adapter);

        adapter.setOnItemClickListener((id, source) -> {
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra(Constants.EXTRA_RECIPE_ID, id);
            intent.putExtra(Constants.EXTRA_RECIPE_SOURCE, Constants.SOURCE_LOCAL);
            startActivity(intent);
        });

        binding.fabAddRecipe.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddEditRecipeActivity.class));
        });

        repository.getAllRecipes().observe(getViewLifecycleOwner(), this::updateList);
    }

    private void updateList(List<RecipeEntity> recipes) {
        if (binding == null) return;

        List<RecipeAdapter.RecipeItem> items = new ArrayList<>();
        if (recipes != null) {
            for (RecipeEntity recipe : recipes) {
                items.add(new RecipeAdapter.RecipeItem(
                        String.valueOf(recipe.getId()),
                        recipe.getTitle(),
                        null,
                        recipe.getCategory(),
                        Constants.SOURCE_LOCAL));
            }
        }

        adapter.setItems(items);

        if (items.isEmpty()) {
            binding.recyclerMyRecipes.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerMyRecipes.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}