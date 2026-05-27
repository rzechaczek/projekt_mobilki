package com.cookbook.app.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MealListResponse {

    @SerializedName("meals")
    private List<MealSummary> meals;

    public List<MealSummary> getMeals() { return meals; }
}