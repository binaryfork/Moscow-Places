package com.moscowplaces.network.endpoints;

import com.moscowplaces.network.entities.Category;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

public interface Categories {

    @GET("/categories")
    void getList(
            Callback<List<Category>> callback
    );

}
