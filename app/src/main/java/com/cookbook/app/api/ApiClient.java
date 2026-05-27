package com.cookbook.app.api;

import com.cookbook.app.util.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static MealDbApi instance;

    public static MealDbApi getInstance() {
        if (instance == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            instance = retrofit.create(MealDbApi.class);
        }
        return instance;
    }
}