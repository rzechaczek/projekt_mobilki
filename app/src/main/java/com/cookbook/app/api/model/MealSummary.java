package com.cookbook.app.api.model;

import com.google.gson.annotations.SerializedName;

public class MealSummary {

    @SerializedName("idMeal")
    private String id;

    @SerializedName("strMeal")
    private String name;

    @SerializedName("strMealThumb")
    private String thumbnail;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getThumbnail() { return thumbnail; }
}