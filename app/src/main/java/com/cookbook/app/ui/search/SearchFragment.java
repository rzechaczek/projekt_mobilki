package com.cookbook.app.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cookbook.app.api.ApiClient;
import com.cookbook.app.api.model.MealListResponse;
import com.cookbook.app.api.model.MealSummary;
import com.cookbook.app.databinding.FragmentSearchBinding;
import com.cookbook.app.ui.adapter.RecipeAdapter;
import com.cookbook.app.ui.detail.RecipeDetailActivity;
import com.cookbook.app.util.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private RecipeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new RecipeAdapter();
        binding.recyclerResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerResults.setAdapter(adapter);

        adapter.setOnItemClickListener((id, source) -> {
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra(Constants.EXTRA_RECIPE_ID, id);
            intent.putExtra(Constants.EXTRA_RECIPE_SOURCE, source);
            startActivity(intent);
        });

        binding.buttonSearch.setOnClickListener(v -> performSearch());

        binding.editSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = binding.editSearch.getText().toString().trim();

        if (query.isEmpty()) {
            binding.inputLayoutSearch.setError("Wpisz nazwę przepisu");
            return;
        }

        binding.inputLayoutSearch.setError(null);
        hideKeyboard();

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyView.setVisibility(View.GONE);
        adapter.setItems(new ArrayList<>());

        ApiClient.getInstance().searchMeals(query).enqueue(new Callback<MealListResponse>() {
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
                                meal.getThumbnail(), "",
                                Constants.SOURCE_API));
                    }
                    adapter.setItems(items);
                    binding.emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    binding.emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealListResponse> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                binding.progressBar.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && binding.editSearch.getWindowToken() != null) {
            imm.hideSoftInputFromWindow(binding.editSearch.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}