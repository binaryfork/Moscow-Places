package com.moscowplaces.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moscowplaces.network.endpoints.Contents;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class Places {

    private static final String API_URL = "http://api.iknow.travel/";

    private RestAdapter restAdapter;
    private static Places instance;

    public static Places getInstance() {
        if (instance == null) {
            instance = new Places();
        }
        return instance;
    }

    private RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            restAdapter = new RestAdapter.Builder()
                    .setConverter(new GsonConverter(gson))
                    .setEndpoint(API_URL)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addHeader("X-Api-Version", "4");
                            request.addHeader("Accept", "application/json");
                        }
                    }).build();
        }

        return restAdapter;
    }

    public Contents contents() {
        return getRestAdapter().create(Contents.class);
    }
}
