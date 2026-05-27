package com.cookbook.app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cookbook.app.database.dao.FavoriteDao;
import com.cookbook.app.database.dao.RecipeDao;
import com.cookbook.app.database.entity.FavoriteEntity;
import com.cookbook.app.database.entity.RecipeEntity;

@Database(
        entities = {RecipeEntity.class, FavoriteEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract RecipeDao recipeDao();
    public abstract FavoriteDao favoriteDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "cookbook_database"
                            )
                            .build();
                }
            }
        }
        return instance;
    }
}