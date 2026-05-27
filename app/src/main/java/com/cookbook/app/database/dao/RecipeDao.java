package com.cookbook.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cookbook.app.database.entity.RecipeEntity;

import java.util.List;

@Dao
public interface RecipeDao {

    @Insert
    long insert(RecipeEntity recipe);

    @Update
    void update(RecipeEntity recipe);

    @Delete
    void delete(RecipeEntity recipe);

    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    LiveData<List<RecipeEntity>> getAllRecipes();

    @Query("SELECT * FROM recipes WHERE id = :id")
    RecipeEntity getById(int id);

    @Query("DELETE FROM recipes WHERE id = :id")
    void deleteById(int id);
}