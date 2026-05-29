package com.cookbook.app.ui.addrecipe;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cookbook.app.database.entity.RecipeEntity;
import com.cookbook.app.databinding.ActivityAddEditRecipeBinding;
import com.cookbook.app.repository.AppRepository;
import com.cookbook.app.util.Constants;

public class AddEditRecipeActivity extends AppCompatActivity {

    private ActivityAddEditRecipeBinding binding;
    private AppRepository repository;
    private RecipeEntity existingRecipe;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = AppRepository.getInstance(this);

        String recipeId = getIntent().getStringExtra(Constants.EXTRA_RECIPE_ID);
        if (recipeId != null && !recipeId.isEmpty()) {
            isEditMode = true;
            loadRecipeForEdit(Integer.parseInt(recipeId));
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Nowy przepis");
            }
        }

        binding.buttonSave.setOnClickListener(v -> saveRecipe());
    }

    private void loadRecipeForEdit(int id) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edytuj przepis");
        }
        binding.progressBar.setVisibility(View.VISIBLE);

        repository.getRecipeById(id, recipe -> runOnUiThread(() -> {
            binding.progressBar.setVisibility(View.GONE);
            if (recipe != null) {
                existingRecipe = recipe;
                fillForm(recipe);
                binding.buttonSave.setText("Zapisz zmiany");
            }
        }));
    }

    private void fillForm(RecipeEntity recipe) {
        binding.editTitle.setText(recipe.getTitle());
        binding.editCategory.setText(recipe.getCategory() != null ? recipe.getCategory() : "");
        binding.editServings.setText(String.valueOf(recipe.getServings()));
        binding.editIngredients.setText(
                recipe.getIngredients() != null ? recipe.getIngredients() : "");
        binding.editMeasures.setText(
                recipe.getMeasures() != null ? recipe.getMeasures() : "");
        binding.editInstructions.setText(
                recipe.getInstructions() != null ? recipe.getInstructions() : "");
    }

    private void saveRecipe() {
        String title = binding.editTitle.getText().toString().trim();
        String category = binding.editCategory.getText().toString().trim();
        String servingsStr = binding.editServings.getText().toString().trim();
        String ingredients = binding.editIngredients.getText().toString().trim();
        String measures = binding.editMeasures.getText().toString().trim();
        String instructions = binding.editInstructions.getText().toString().trim();

        if (title.isEmpty()) {
            binding.editTitle.setError("Podaj nazwę przepisu");
            binding.editTitle.requestFocus();
            return;
        }

        if (ingredients.isEmpty()) {
            binding.editIngredients.setError("Podaj składniki");
            binding.editIngredients.requestFocus();
            return;
        }

        if (instructions.isEmpty()) {
            binding.editInstructions.setError("Podaj instrukcje");
            binding.editInstructions.requestFocus();
            return;
        }

        int servings;
        try {
            servings = Integer.parseInt(servingsStr);
            if (servings < 1) servings = Constants.DEFAULT_SERVINGS;
        } catch (NumberFormatException e) {
            servings = Constants.DEFAULT_SERVINGS;
        }

        binding.buttonSave.setEnabled(false);

        if (isEditMode && existingRecipe != null) {
            existingRecipe.setTitle(title);
            existingRecipe.setCategory(category);
            existingRecipe.setServings(servings);
            existingRecipe.setIngredients(ingredients);
            existingRecipe.setMeasures(measures);
            existingRecipe.setInstructions(instructions);

            repository.updateRecipe(existingRecipe, () -> runOnUiThread(() -> {
                Toast.makeText(this, "Przepis zaktualizowany", Toast.LENGTH_SHORT).show();
                finish();
            }));
        } else {
            RecipeEntity newRecipe = new RecipeEntity(
                    title, category, ingredients, measures,
                    instructions, servings, System.currentTimeMillis());

            repository.insertRecipe(newRecipe, () -> runOnUiThread(() -> {
                Toast.makeText(this, "Przepis zapisany", Toast.LENGTH_SHORT).show();
                finish();
            }));
        }
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
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}