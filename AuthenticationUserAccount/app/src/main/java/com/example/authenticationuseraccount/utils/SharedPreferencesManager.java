package com.example.authenticationuseraccount.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static SharedPreferencesManager instance;
    private SharedPreferences sharedPreferences;

    private static final String PREFERENCES_NAME = "my_shared_preferences";

    // Private constructor to prevent instantiation
    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    // Public method to provide access to the singleton instance
    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    // Method to get SharedPreferences
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    // Example methods to put and get data
    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // Add more methods as needed to interact with SharedPreferences

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

// And so on for other data types...

}

