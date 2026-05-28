package com.cookbook.app.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class FavoriteEntity {

    @PrimaryKey
    @NonNull
    private String mealId;
    private String title;
    private String image;
    private String category;

    public FavoriteEntity(@NonNull String mealId, String title, String image, String category) {
        this.mealId = mealId;
        this.title = title;
        this.image = image;
        this.category = category;
    }

    @NonNull
    public String getMealId() { return mealId; }
    public void setMealId(@NonNull String mealId) { this.mealId = mealId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}