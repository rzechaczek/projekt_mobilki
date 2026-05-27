package com.cookbook.app.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MealDetailResponse {

    @SerializedName("meals")
    private List<Meal> meals;

    public Meal getMeal() {
        if (meals != null && !meals.isEmpty()) {
            return meals.get(0);
        }
        return null;
    }
}