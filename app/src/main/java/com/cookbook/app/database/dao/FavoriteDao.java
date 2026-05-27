package com.cookbook.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cookbook.app.database.entity.FavoriteEntity;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteEntity favorite);

    @Delete
    void delete(FavoriteEntity favorite);

    @Query("SELECT * FROM favorites ORDER BY rowid DESC")
    LiveData<List<FavoriteEntity>> getAllFavorites();

    @Query("SELECT * FROM favorites WHERE mealId = :mealId LIMIT 1")
    FavoriteEntity getByMealId(String mealId);

    @Query("DELETE FROM favorites WHERE mealId = :mealId")
    void deleteByMealId(String mealId);
}