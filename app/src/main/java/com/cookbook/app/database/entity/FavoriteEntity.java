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
    private String thumbnail;
    private String category;

    public FavoriteEntity(@NonNull String mealId, String title, String thumbnail, String category) {
        this.mealId = mealId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.category = category;
    }

    @NonNull
    public String getMealId() { return mealId; }
    public void setMealId(@NonNull String mealId) { this.mealId = mealId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}