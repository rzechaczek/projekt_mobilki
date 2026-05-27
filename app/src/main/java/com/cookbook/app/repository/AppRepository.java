package com.cookbook.app.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.cookbook.app.database.AppDatabase;
import com.cookbook.app.database.dao.FavoriteDao;
import com.cookbook.app.database.dao.RecipeDao;
import com.cookbook.app.database.entity.FavoriteEntity;
import com.cookbook.app.database.entity.RecipeEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppRepository {

    private static AppRepository instance;

    private final RecipeDao recipeDao;
    private final FavoriteDao favoriteDao;
    private final ExecutorService executor;

    private AppRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        recipeDao = db.recipeDao();
        favoriteDao = db.favoriteDao();
        executor = Executors.newFixedThreadPool(4);
    }

    public static AppRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (AppRepository.class) {
                if (instance == null) {
                    instance = new AppRepository(context);
                }
            }
        }
        return instance;
    }

    public LiveData<List<RecipeEntity>> getAllRecipes() {
        return recipeDao.getAllRecipes();
    }

    public void insertRecipe(RecipeEntity recipe, Runnable onComplete) {
        executor.execute(() -> {
            recipeDao.insert(recipe);
            if (onComplete != null) onComplete.run();
        });
    }

    public void updateRecipe(RecipeEntity recipe, Runnable onComplete) {
        executor.execute(() -> {
            recipeDao.update(recipe);
            if (onComplete != null) onComplete.run();
        });
    }

    public void deleteRecipe(int id, Runnable onComplete) {
        executor.execute(() -> {
            recipeDao.deleteById(id);
            if (onComplete != null) onComplete.run();
        });
    }

    public void getRecipeById(int id, RecipeCallback callback) {
        executor.execute(() -> {
            RecipeEntity recipe = recipeDao.getById(id);
            if (callback != null) callback.onResult(recipe);
        });
    }

    public LiveData<List<FavoriteEntity>> getAllFavorites() {
        return favoriteDao.getAllFavorites();
    }

    public void addFavorite(FavoriteEntity favorite) {
        executor.execute(() -> favoriteDao.insert(favorite));
    }

    public void removeFavorite(String mealId) {
        executor.execute(() -> favoriteDao.deleteByMealId(mealId));
    }

    public void isFavorite(String mealId, BooleanCallback callback) {
        executor.execute(() -> {
            FavoriteEntity fav = favoriteDao.getByMealId(mealId);
            if (callback != null) callback.onResult(fav != null);
        });
    }

    public interface RecipeCallback {
        void onResult(RecipeEntity recipe);
    }

    public interface BooleanCallback {
        void onResult(boolean result);
    }
}