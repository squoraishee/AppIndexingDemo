package com.vmn.appindexingdemo;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

/**
 * Create an app indexing application
 */
public class AppIndexingApplication extends Application {

    private static AppIndexingApplication instance;

    public static String EMPTY = "";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static AppIndexingApplication getInstance() {
        return instance;
    }
}
