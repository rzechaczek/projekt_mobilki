package com.cookbook.app.api.model;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("idCategory")
    private String id;

    @SerializedName("strCategory")
    private String name;

    @SerializedName("strCategoryThumb")
    private String thumbnail;

    @SerializedName("strCategoryDescription")
    private String description;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getThumbnail() { return thumbnail; }
    public String getDescription() { return description; }
}