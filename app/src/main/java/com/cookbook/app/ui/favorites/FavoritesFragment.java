package com.cookbook.app.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cookbook.app.database.entity.FavoriteEntity;
import com.cookbook.app.databinding.FragmentFavoritesBinding;
import com.cookbook.app.repository.AppRepository;
import com.cookbook.app.ui.adapter.RecipeAdapter;
import com.cookbook.app.ui.detail.RecipeDetailActivity;
import com.cookbook.app.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private RecipeAdapter adapter;
    private AppRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = AppRepository.getInstance(requireContext());

        adapter = new RecipeAdapter();
        binding.recyclerFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerFavorites.setAdapter(adapter);

        adapter.setOnItemClickListener((id, source) -> {
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra(Constants.EXTRA_RECIPE_ID, id);
            intent.putExtra(Constants.EXTRA_RECIPE_SOURCE, source);
            startActivity(intent);
        });

        repository.getAllFavorites().observe(getViewLifecycleOwner(), this::updateList);
    }

    private void updateList(List<FavoriteEntity> favorites) {
        if (binding == null) return;

        List<RecipeAdapter.RecipeItem> items = new ArrayList<>();
        if (favorites != null) {
            for (FavoriteEntity fav : favorites) {
                items.add(new RecipeAdapter.RecipeItem(
                        fav.getMealId(),
                        fav.getTitle(),
                        fav.getImage(),
                        fav.getCategory(),
                        Constants.SOURCE_API));
            }
        }

        adapter.setItems(items);

        if (items.isEmpty()) {
            binding.recyclerFavorites.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerFavorites.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}