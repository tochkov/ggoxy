package com.goxapps.goxreader.util;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by tochkov.
 */
public class TimeTracker {

    public static final String TAG = "TIMER ";
    public static HashMap<String, Long> timers = new HashMap<>();

    public static void start(String label) {

        timers.put(label, System.currentTimeMillis());
    }

    public static void stop(String label) {

        Long stopTime = timers.get(label);

        if(stopTime == null){
            Log.e(TAG, "Timer " + label + " does not exist");
        } else {
            Log.e(TAG + label, String.valueOf(System.currentTimeMillis() - stopTime));
        }
    }


}
