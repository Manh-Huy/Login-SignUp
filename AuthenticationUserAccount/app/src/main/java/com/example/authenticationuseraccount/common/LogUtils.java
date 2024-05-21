package com.example.authenticationuseraccount.common;

import android.util.Log;

public class LogUtils {
    private static final String TAG = Constants.APP_NAME;

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }
}
