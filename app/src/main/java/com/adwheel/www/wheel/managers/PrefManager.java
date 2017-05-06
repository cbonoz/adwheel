package com.adwheel.www.wheel.managers;

import android.app.Application;
import android.content.SharedPreferences;

import static com.adwheel.www.wheel.WheelApplication.PREF_NAME;

/**
 * Created on 4/19/17.
 */
public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();
    public static final String GOLD_PURCHASE_LOC = "gold_purchase";
    public static final String SILVER_PURCHASE_LOC = "silver_purchase";
    public static final String PERFECT_SCORE = "perfect";

    private final SharedPreferences settings;

    public PrefManager(Application app) {
        settings = app.getSharedPreferences(PREF_NAME, 0);
    }

    public void saveBooleanPref(String location, boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(location, value);
        editor.apply();
    }

    public boolean getBooleanPref(String location, boolean defaultValue) {
        return settings.getBoolean(location, defaultValue);
    }

    public boolean getBooleanPref(String location) {
        return getBooleanPref(location, false);
    }

}
