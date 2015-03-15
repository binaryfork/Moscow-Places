package com.moscowplaces;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

public class BaseApplication extends Application {
    @Override
    public void onCreate()
    {
        super.onCreate();

        // Here you start using the ActiveAndroid library.
        ActiveAndroid.initialize(this);
    }
}
