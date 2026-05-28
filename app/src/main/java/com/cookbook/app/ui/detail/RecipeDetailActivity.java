package com.cookbook.app.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.cookbook.app.R;
import com.cookbook.app.api.ApiClient;
import com.cookbook.app.api.model.Meal;
import com.cookbook.app.api.model.MealDetailResponse;
import com.cookbook.app.database.entity.FavoriteEntity;
import com.cookbook.app.database.entity.RecipeEntity;
import com.cookbook.app.databinding.ActivityRecipeDetailBinding;
import com.cookbook.app.repository.AppRepository;
import com.cookbook.app.ui.adapter.IngredientAdapter;
import com.cookbook.app.ui.addrecipe.AddEditRecipeActivity;
import com.cookbook.app.util.Constants;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {

    private ActivityRecipeDetailBinding binding;
    private IngredientAdapter ingredientAdapter;
    private AppRepository repository;

    private String recipeId;
    private String source;
    private boolean isFavorite = false;

    private Meal currentApiMeal;
    private int currentServings = Constants.DEFAULT_SERVINGS;
    private int baseServings = Constants.DEFAULT_SERVINGS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = AppRepository.getInstance(this);

        ingredientAdapter = new IngredientAdapter();
        binding.recyclerIngredients.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerIngredients.setAdapter(ingredientAdapter);
        binding.recyclerIngredients.setNestedScrollingEnabled(false);

        recipeId = getIntent().getStringExtra(Constants.EXTRA_RECIPE_ID);
        source = getIntent().getStringExtra(Constants.EXTRA_RECIPE_SOURCE);

        if (recipeId == null || source == null) {
            finish();
            return;
        }

        if (Constants.SOURCE_LOCAL.equals(source)) {
            loadLocalRecipe();
        } else {
            loadApiRecipe();
        }

        setupPortionScaler();
    }

    private void loadApiRecipe() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.fabFavorite.setVisibility(View.GONE);
        binding.buttonEditRecipe.setVisibility(View.GONE);
        binding.buttonDeleteRecipe.setVisibility(View.GONE);

        binding.fabFavorite.setOnClickListener(v -> toggleFavorite());

        ApiClient.getInstance().getMealById(recipeId).enqueue(new Callback<MealDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealDetailResponse> call,
                                   @NonNull Response<MealDetailResponse> response) {
                if (isFinishing() || isDestroyed()) return;
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getMeal() != null) {
                    currentApiMeal = response.body().getMeal();
                    populateApiMeal(currentApiMeal);
                    checkFavoriteStatus();
                    binding.fabFavorite.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(RecipeDetailActivity.this,
                            "Nie udało się załadować przepisu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealDetailResponse> call,
                                  @NonNull Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(RecipeDetailActivity.this,
                        "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateApiMeal(Meal meal) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(meal.getName());
        }
        binding.textTitle.setText(meal.getName());
        binding.textCategory.setText(meal.getCategory() != null ? meal.getCategory() : "");
        binding.textArea.setVisibility(View.VISIBLE);
        binding.textArea.setText(meal.getArea() != null ? meal.getArea() : "");
        binding.textInstructions.setText(meal.getInstructions());

        Glide.with(this)
                .load(meal.getThumbnail())
                .placeholder(R.drawable.ic_recipe_placeholder)
                .error(R.drawable.ic_recipe_placeholder)
                .into(binding.imageMeal);

        baseServings = Constants.DEFAULT_SERVINGS;
        currentServings = baseServings;
        binding.textServingsCount.setText(String.valueOf(currentServings));
        ingredientAdapter.setData(meal.getIngredients(), meal.getMeasures());
    }

    private void loadLocalRecipe() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.fabFavorite.setVisibility(View.GONE);
        binding.buttonEditRecipe.setVisibility(View.GONE);
        binding.buttonDeleteRecipe.setVisibility(View.GONE);

        int id = Integer.parseInt(recipeId);
        repository.getRecipeById(id, recipe -> runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) return;
            binding.progressBar.setVisibility(View.GONE);
            if (recipe != null) {
                populateLocalRecipe(recipe);
                binding.buttonEditRecipe.setVisibility(View.VISIBLE);
                binding.buttonDeleteRecipe.setVisibility(View.VISIBLE);
            }
        }));

        binding.buttonEditRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditRecipeActivity.class);
            intent.putExtra(Constants.EXTRA_RECIPE_ID, recipeId);
            startActivity(intent);
        });

        binding.buttonDeleteRecipe.setOnClickListener(v -> confirmDelete());
    }

    private void populateLocalRecipe(RecipeEntity recipe) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(recipe.getTitle());
        }
        binding.textTitle.setText(recipe.getTitle());
        binding.textCategory.setText(recipe.getCategory() != null ? recipe.getCategory() : "");
        binding.textArea.setVisibility(View.GONE);
        binding.textInstructions.setText(recipe.getInstructions());
        binding.imageMeal.setImageResource(R.drawable.ic_recipe_placeholder);

        List<String> ingredients = Arrays.asList(recipe.getIngredients().split("\n"));
        List<String> measures = Arrays.asList(recipe.getMeasures().split("\n"));

        baseServings = recipe.getServings() > 0
                ? recipe.getServings()
                : Constants.DEFAULT_SERVINGS;
        currentServings = baseServings;
        binding.textServingsCount.setText(String.valueOf(currentServings));
        ingredientAdapter.setData(ingredients, measures);
    }

    private void setupPortionScaler() {
        binding.buttonDecreaseServings.setOnClickListener(v -> {
            if (currentServings > 1) {
                currentServings--;
                binding.textServingsCount.setText(String.valueOf(currentServings));
                ingredientAdapter.setScaleFactor((float) currentServings / baseServings);
            }
        });

        binding.buttonIncreaseServings.setOnClickListener(v -> {
            currentServings++;
            binding.textServingsCount.setText(String.valueOf(currentServings));
            ingredientAdapter.setScaleFactor((float) currentServings / baseServings);
        });
    }

    private void checkFavoriteStatus() {
        repository.isFavorite(recipeId, result -> runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) return;
            isFavorite = result;
            updateFavoriteIcon();
        }));
    }

    private void toggleFavorite() {
        if (isFavorite) {
            repository.removeFavorite(recipeId);
            isFavorite = false;
            Toast.makeText(this, "Usunięto z ulubionych", Toast.LENGTH_SHORT).show();
        } else {
            if (currentApiMeal != null) {
                FavoriteEntity fav = new FavoriteEntity(
                        currentApiMeal.getId(),
                        currentApiMeal.getName(),
                        currentApiMeal.getThumbnail(),
                        currentApiMeal.getCategory());
                repository.addFavorite(fav);
                isFavorite = true;
                Toast.makeText(this, "Dodano do ulubionych", Toast.LENGTH_SHORT).show();
            }
        }
        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        binding.fabFavorite.setImageResource(
                isFavorite
                        ? R.drawable.ic_favorite_filled
                        : R.drawable.ic_favorite_border);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Usuń przepis")
                .setMessage("Czy na pewno chcesz usunąć ten przepis?")
                .setPositiveButton("Usuń", (dialog, which) ->
                        repository.deleteRecipe(Integer.parseInt(recipeId), () ->
                                runOnUiThread(this::finish)))
                .setNegativeButton("Anuluj", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.SOURCE_LOCAL.equals(source) && recipeId != null) {
            int id = Integer.parseInt(recipeId);
            repository.getRecipeById(id, recipe -> runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (recipe != null) populateLocalRecipe(recipe);
            }));
        }
    }
}