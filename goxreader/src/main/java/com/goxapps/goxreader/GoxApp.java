package com.goxapps.goxreader;

import android.util.Log;

import com.orm.SugarApp;

/**
 * Created by tochkov.
 */
public class GoxApp extends SugarApp {

    private static GoxApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static GoxApp get(){
        return instance;
    }
}
