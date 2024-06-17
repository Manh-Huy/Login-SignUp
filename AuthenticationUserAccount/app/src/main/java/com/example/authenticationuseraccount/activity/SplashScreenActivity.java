package com.example.authenticationuseraccount.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.common.PermissionManager;
import com.example.authenticationuseraccount.model.business.LocalSong;
import com.example.authenticationuseraccount.model.business.User;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.utils.CustomDownloadManager;
import com.example.authenticationuseraccount.utils.DataLocalManager;
import com.example.authenticationuseraccount.utils.LocalMusicLoader;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Disposable mDisposable;
    private List<String> mListName = new ArrayList<>();
    private boolean isCallApiGetUser;
    private boolean isCallApiGetUserLoveSong;
    private boolean isCallApiGetAllSongInfo;

    private int delayMoveActivityTime = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        askingPermission();


    }

    private void askingPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    !Environment.isExternalStorageManager()) {
                permissionsNeeded.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            }

            if (!permissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), Constants.PERMISSION_REQUEST_CODE);
            }else{
                processWithAppLogic();
            }
        }else{
            processWithAppLogic();
        }
    }

    private void processWithAppLogic(){
        MediaItemHolder.getInstance().setListLocalSong(LocalMusicLoader.getInstance().loadMusic(SplashScreenActivity.this));
        isCallApiGetUser = false;
        isCallApiGetUserLoveSong = false;
        isCallApiGetAllSongInfo = false;

        // Configure google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        getNameAllSongInfo();
        checkUserLogin();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // Handle the case where the permission was not granted
                    Toast.makeText(this, "Permission " + permissions[i] + " denied", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            MediaItemHolder.getInstance().setListLocalSong(LocalMusicLoader.getInstance().loadMusic(SplashScreenActivity.this));
            isCallApiGetUser = false;
            isCallApiGetUserLoveSong = false;
            isCallApiGetAllSongInfo = false;

            // Configure google Sign in
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID)
                    .requestEmail()
                    .build();

            mAuth = FirebaseAuth.getInstance();
            // Build a GoogleSignInClient with the options specified by gso
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            getNameAllSongInfo();
            checkUserLogin();
            // All permissions are granted, proceed with your logic
        }
    }

    private void getNameAllSongInfo() {
        ApiService.apiService.getNameAllInfoSong()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<String> strings) {
                        mListName = strings;
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api error");
                        /*startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        finish();*/
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.ApplicationLogD("Call api getNameAllInfoSong Complete");
                        // Save to shared preference
                        Set<String> stringSet = new HashSet<>(mListName);
                        DataLocalManager.setNameAllInfoSong(stringSet);

                        isCallApiGetAllSongInfo = true;
                        isActivityDone();
                    }
                });
    }

    private void checkUserLogin() {
        if (!DataLocalManager.getRememberMeAccount()) {
            // Redirect to login screen if "Remember me" is false
            if (mAuth.getCurrentUser() != null) {
                signOut();
            } else {
                Toast.makeText(SplashScreenActivity.this, "Chua dang nhap", Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        finish();
                    }
                }, delayMoveActivityTime);
            }
        } else {
            if (mAuth.getCurrentUser() != null) {
                String Uid = mAuth.getCurrentUser().getUid();
//                getUserLoveSong(Uid);
                getUserByID(Uid);
            } else {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        finish();
                    }
                }, delayMoveActivityTime);
            }
        }
    }

    private void getUserByID(String userID) {
        ApiService.apiService.getUserById(userID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User apiUser = response.body();
                    User.getInstance();

                    // Update singleton instance with data from API
                    User.getInstance().setUserID(apiUser.getUserID());
                    User.getInstance().setUsername(apiUser.getUsername());
                    User.getInstance().setEmail(apiUser.getEmail());
                    User.getInstance().setRole(apiUser.getRole());
                    User.getInstance().setSignInMethod(apiUser.getSignInMethod());
                    if (apiUser.getExpiredDatePremium() != null) {
                        User.getInstance().setExpiredDatePremium(apiUser.getExpiredDatePremium());
                    }
                    if (apiUser.getImageURL() != null) {
                        User.getInstance().setImageURL(apiUser.getImageURL());
                    }

                    isCallApiGetUser = true;
                    isActivityDone();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }


    private void isActivityDone() {
        if (isCallApiGetUser && isCallApiGetAllSongInfo) {
            LogUtils.ApplicationLogI("ApiGetUser: " + isCallApiGetUser + " ApiGetAllSong: " + isCallApiGetAllSongInfo);
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            finish();
        } else {
            LogUtils.ApplicationLogI("ApiGetUser: " + isCallApiGetUser + " ApiGetAllSong: " + isCallApiGetAllSongInfo);
        }

    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.w(TAG, "Signed out of google");
                Toast.makeText(SplashScreenActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }
}