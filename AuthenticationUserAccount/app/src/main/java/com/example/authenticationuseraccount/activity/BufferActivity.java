package com.example.authenticationuseraccount.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.utils.SharedPreferencesManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class BufferActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer);

        mAuth = FirebaseAuth.getInstance();
        // Configure google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        progressBar = findViewById(R.id.progressBar);

        boolean isRememberMe = SharedPreferencesManager.getInstance(this).getBoolean("remember_me", false);

        if (!isRememberMe) {
            // Redirect to login screen if "Remember me" is false
            if (mAuth.getCurrentUser() != null)
            {
                signOut();
            }
        } else {
            // Continue with the buffer activity if "Remember me" is true
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }
        continueToHomeActivity();
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.w(TAG, "Signed out of google");
                Toast.makeText(BufferActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void continueToHomeActivity() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(BufferActivity.this, HomeActivity.class));
                finish();
            }
        }, 2000);
    }
}