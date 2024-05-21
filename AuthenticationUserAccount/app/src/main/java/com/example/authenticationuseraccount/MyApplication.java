package com.example.authenticationuseraccount;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication2";

    @Override
    public void onCreate() {
        super.onCreate();
    }



}
