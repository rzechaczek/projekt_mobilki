package com.cookbook.app.api;

import com.cookbook.app.api.model.CategoryListResponse;
import com.cookbook.app.api.model.MealDetailResponse;
import com.cookbook.app.api.model.MealListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealDbApi {

    @GET("categories.php")
    Call<CategoryListResponse> getCategories();

    @GET("filter.php")
    Call<MealListResponse> getMealsByCategory(@Query("c") String category);

    @GET("search.php")
    Call<MealListResponse> searchMeals(@Query("s") String query);

    @GET("lookup.php")
    Call<MealDetailResponse> getMealById(@Query("i") String id);

    @GET("random.php")
    Call<MealDetailResponse> getRandomMeal();
}