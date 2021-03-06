package com.adwheel.www.wheel.managers;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();

    private static final String PREF_NAME = "prefs";

    private final SharedPreferences settings;
    private final Gson gson;

    public PrefManager(Application app, Gson gson) {
        this.settings = app.getSharedPreferences(PREF_NAME, 0);
        this.gson = gson;
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

    public void saveInt(String location, int value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(location, value);
        editor.apply();
    }

    public void saveJsonPreference(String key, Object value) {
        String jsonString = gson.toJson(value);
        saveString(key, jsonString);
    }

    // ** Getter methods ** //

    public long getLong(String location) {
        return settings.getLong(location, -1L);
    }

    public long getLong(String location, long defaultValue) {
        return settings.getLong(location, defaultValue);
    }

    public int getInt(String location, int defaultValue) {
        return settings.getInt(location, defaultValue);
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

    public <T> T getJsonPreference(String key, Class<T> objectClass, T defaultValue)
            throws JsonParseException {
        String value = getString(key, null);
        if (value == null) {
            return defaultValue;
        }
        return gson.fromJson(value, objectClass);
    }

}
