package com.example.authenticationuseraccount.common;

import android.util.Log;

public class LogUtils {
    private static final String TAG = Constants.APP_NAME;

    public static void d(String tag, String message) {
        Log.d(tag, message);
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
    }

    public static void ApplicationLogE(String message) {
        Log.e(TAG, message);
    }

    public static void ApplicationLogD(String message) {
        Log.d(TAG, message);
    }

    public static void ApplicationLogI(String message) {
        Log.i(TAG, message);
    }
}
