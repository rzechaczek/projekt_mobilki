package com.cookbook.app.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class RecipeEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String category;
    private String ingredients;
    private String measures;
    private String instructions;
    private int servings;
    private long createdAt;

    public RecipeEntity(String title, String category, String ingredients,
                        String measures, String instructions, int servings, long createdAt) {
        this.title = title;
        this.category = category;
        this.ingredients = ingredients;
        this.measures = measures;
        this.instructions = instructions;
        this.servings = servings;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getMeasures() { return measures; }
    public void setMeasures(String measures) { this.measures = measures; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}