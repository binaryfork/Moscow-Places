package com.moscowplaces.network.endpoints;

import com.moscowplaces.network.entities.Content;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface Contents {

    @GET("/contents")
    void region(
            @Query("region") String region,
            @Query("type") String type,
            @Query("limit") Integer limit,
            Callback<List<Content>> callback
    );

}
