package com.moscowplaces;

public class PhotoUrlUtil {

    private static final String BASE_URL = "http://api.iknow.travel/photo/cover/";

    public static String buildUrl(String filename) {
        return BASE_URL + filename + "_720x400.jpg";
    }
}