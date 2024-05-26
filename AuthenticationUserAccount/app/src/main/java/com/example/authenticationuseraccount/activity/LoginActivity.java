package com.example.authenticationuseraccount.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.User;
import com.example.authenticationuseraccount.utils.DataLocalManager;
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

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;

    private CheckBox checkBoxRememberMe;
    private TextView textViewForgotPass;
    private FirebaseAuth mAuth;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.editTextEmail);
        inputPassword = findViewById(R.id.editTextPassword);
        checkBoxRememberMe = findViewById(R.id.checkbox_remember_me);
        textViewForgotPass = findViewById(R.id.textView_forgot_pass);

        Button btnLogin = findViewById(R.id.btnLogin);
        ImageView imageViewSignWithGoogle = findViewById(R.id.signWithGoogle);

        mAuth = FirebaseAuth.getInstance();
        // Configure google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isRememberMe = checkBoxRememberMe.isChecked();
                DataLocalManager.setRememberMeAccount(isRememberMe);
                loginUser(email, password);
            }
        });

        imageViewSignWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        textViewForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {

                                checkFirstTimeLogin(user, "Email");

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
                            }
                        }
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
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this, "Google Sign in Succeeded", Toast.LENGTH_LONG).show();
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google Sign in Failed " + e, Toast.LENGTH_LONG).show();
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
                            Toast.makeText(LoginActivity.this, "Firebase Authentication Succeeded ", Toast.LENGTH_LONG).show();

                            checkFirstTimeLogin(user, "Google");

                            boolean isRememberMe = checkBoxRememberMe.isChecked();
                            DataLocalManager.setRememberMeAccount(isRememberMe);

                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Firebase Authentication failed:" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private User getUserInfo(FirebaseUser user, String signInMethod) {
        User userInfo = new User();
//        for (UserInfo profile : user.getProviderData()) {
//            userInfo.setUserID(profile.getUid());
//            userInfo.setUsername(profile.getDisplayName());
//            userInfo.setEmail(profile.getEmail());
//            userInfo.setImageURL(profile.getPhotoUrl().toString());
//            userInfo.setSignInMethod(signInMethod);
//        }

        userInfo.setUserID(user.getUid());
        userInfo.setUsername(user.getDisplayName());
        userInfo.setEmail(user.getEmail());
        if (user.getPhotoUrl() != null) {
            userInfo.setImageURL(user.getPhotoUrl().toString());
        } else {
            userInfo.setImageURL("");
        }
        userInfo.setSignInMethod(signInMethod);
        return userInfo;
    }

    private void addUser(User user) {
        ApiService.apiService.addUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(LoginActivity.this, "Call Api Success ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void checkFirstTimeLogin(FirebaseUser user, String signInMethod) {
        String id = user.getUid();
        ApiService.apiService.getUserById(id).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User mUser = response.body();
                LogUtils.d("Call thanh cong");
                if (mUser == null)
                {
                    User userInfo = getUserInfo(user, signInMethod);
                    addUser(userInfo);
                    Toast.makeText(getApplicationContext(), "UID: " + userInfo.getUserID() + "\nName: "
                            + userInfo.getUsername() + "\nEmail: " + userInfo.getEmail() + "\nPhotoUrl: "
                            + userInfo.getImageURL() + "\nsignInMethod: " + userInfo.getSignInMethod(), Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }


}
