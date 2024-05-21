package com.example.authenticationuseraccount.common;

import android.content.Context;
import android.widget.Toast;

public class ErrorUtils {
    public static void showError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
