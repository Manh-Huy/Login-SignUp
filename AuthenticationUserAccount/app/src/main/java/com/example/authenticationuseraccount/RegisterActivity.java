package com.example.authenticationuseraccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword, inputUsername;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputEmail = findViewById(R.id.editTextEmail);
        inputPassword = findViewById(R.id.editTextPassword);
        inputUsername = findViewById(R.id.editTextUsername);

        mAuth = FirebaseAuth.getInstance();

        Button btnRegister = findViewById(R.id.btnRegister);
        ImageView imageViewSignWithGoogle = findViewById(R.id.signWithGoogle);

        // Configure google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        imageViewSignWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                final String username = inputUsername.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_LONG).show();
                                    Log.e("MyTag", task.getException().toString());
                                } else {
                                    // Lưu tên người dùng vào Firebase Authentication
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user != null) {
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(emailVerificationTask -> {
                                                    if (emailVerificationTask.isSuccessful()) {
                                                        // Email verification sent successfully
                                                        Toast.makeText(getApplicationContext(), "Send email successfully", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(RegisterActivity.this, EmailConfirmActivity.class));
                                                        finish();

                                                    } else {
                                                        // Failed to send verification email
                                                        Toast.makeText(getApplicationContext(), "Send email unsuccessfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .build();
                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("TAG", "User profile updated.");
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this, "Google Sign in Succeeded",  Toast.LENGTH_LONG).show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google Sign in Failed " + e,  Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "signInWithCredential:success: currentUser: " + user.getEmail());
                            Toast.makeText(RegisterActivity.this, "Firebase Authentication Succeeded ",  Toast.LENGTH_LONG).show();

                            if (isFirstTimeLogin())
                            {
                                String signInMethod = "Google";
                                String userUid = null;
                                String userName = null;
                                String userEmail = null;
                                String userPhotoUrl = null;
                                for (UserInfo profile : user.getProviderData()) {
                                    userUid = profile.getUid();
                                    userName = profile.getDisplayName();
                                    userEmail = profile.getEmail();
                                    userPhotoUrl = profile.getPhotoUrl().toString();
                                }
                                Toast.makeText(getApplicationContext(), "UID: " + userUid + "\nName: " + userName + "\nEmail: " + userEmail + "\nPhotoUrl: " + userPhotoUrl + "\nsignInMethod: " + signInMethod, Toast.LENGTH_LONG).show();
                            }
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Firebase Authentication failed:" + task.getException(),  Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean isFirstTimeLogin() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Kiểm tra xem người dùng đã từng đăng nhập trước đó chưa
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isFirstTime = sharedPreferences.getBoolean(user.getUid(), true);

            if (isFirstTime) {
                // Nếu là lần đầu tiên, đánh dấu rằng đã đăng nhập lần đầu
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(user.getUid(), false);
                editor.apply();
            }

            return isFirstTime;
        } else {
            // Nếu không có người dùng hiện tại, không thể xác định lần đầu tiên
            return false;
        }
    }
}