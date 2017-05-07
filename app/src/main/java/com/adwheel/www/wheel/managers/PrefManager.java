package com.adwheel.www.wheel.managers;

import android.app.Application;
import android.content.SharedPreferences;

import static com.adwheel.www.wheel.WheelApplication.PREF_NAME;

/**
 * Created on 4/19/17.
 */
public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();

    private final SharedPreferences settings;

    public PrefManager(Application app) {
        settings = app.getSharedPreferences(PREF_NAME, 0);
    }

    // ** Setter methods ** //

    public void saveBoolean(String location, boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(location, value);
        editor.apply();
    }

    public void saveString(String location, String value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(location, value);
        editor.apply();
    }

    public void saveLong(String location, long value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(location, value);
        editor.apply();
    }

    // ** Getter methods ** //

    public long getLong(String location) {
       return settings.getLong(location , -1L);
    }

    public long getLong(String location, long defaultValue) {
        return  settings.getLong(location, defaultValue);
    }

    public boolean getBoolean(String location, Boolean defaultValue) {
        return settings.getBoolean(location, defaultValue);
    }

    public boolean getBoolean(String location) {
        return getBoolean(location, false);
    }

    public String getString(String location, String defaultValue) {
        return settings.getString(location, defaultValue);
    }

}
