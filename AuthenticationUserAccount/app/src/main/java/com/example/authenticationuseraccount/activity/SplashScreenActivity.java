package com.example.authenticationuseraccount.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.utils.DataLocalManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Disposable mDisposable;

    private List<String> mListName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        // Configure google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        getNameAllInfoSongAndCheckUserLogin();
    }

    private void getNameAllInfoSongAndCheckUserLogin() {
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
                    }

                    @Override
                    public void onComplete() {
                        // Save to shared preference
                        Set<String> stringSet = new HashSet<>(mListName);
                        DataLocalManager.setNameAllInfoSong(stringSet);

                        if (!DataLocalManager.getRememberMeAccount()) {
                            // Redirect to login screen if "Remember me" is false
                            if (mAuth.getCurrentUser() != null)
                            {
                                signOut();
                            }
                            else {
                                Toast.makeText(SplashScreenActivity.this, "Chua dang nhap", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                        else
                        {
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
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