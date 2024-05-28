package com.example.authenticationuseraccount.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.model.business.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // take and show song object

        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
        {
            return;
        }
        Song song = (Song) bundle.get("object_song");

        TextView tvCreatedAt = findViewById(R.id.tvCreatedAt);
        TextView tvArtist = findViewById(R.id.tvArtist);
        TextView tvAlbum = findViewById(R.id.tvAlbum);
        TextView tvImageURL = findViewById(R.id.tvImageURL);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvGenre = findViewById(R.id.tvGenre);
        TextView tvSongID = findViewById(R.id.tvSongID);
        TextView tvViews = findViewById(R.id.tvViews);
        TextView tvSongURL = findViewById(R.id.tvSongURL);


        tvCreatedAt.setText("Created At: " + song.getCreatedAt());
        tvArtist.setText("Artist: " + song.getArtist());
        tvAlbum.setText("Album: " + song.getAlbum());
        tvImageURL.setText("Image URL: " + song.getImageURL());
        tvName.setText("Name: " + song.getName());
        tvGenre.setText("Genre: " + song.getGenre());
        tvSongID.setText("Song ID: " + song.getSongID());
        tvViews.setText("Views: " + song.getViews());
        tvSongURL.setText("Song URL: " + song.getSongURL());


        // Logout function
        Button btnSignOut = findViewById(R.id.btnSignout);
        Button btnSignOutWithGoogle = findViewById(R.id.btnSignOutWithGoogle);
        Button btnDisconnect = findViewById(R.id.btnDisconnect);

        Button btnCalllAPI = findViewById(R.id.btnCallAPI);

        mAuth = FirebaseAuth.getInstance();

        // Configure google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginSignUpActivity.class));
            }
        });

        btnSignOutWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase sign out
                mAuth.signOut();

                // Google sign out
                mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.w(TAG, "Signed out of google");
                                Toast.makeText(MainActivity.this, "Signed out of google", Toast.LENGTH_SHORT).show();
                            }
                        });

                startActivity(new Intent(MainActivity.this, LoginSignUpActivity.class));
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase sign out
                mAuth.signOut();

                // Google revoke access
                mGoogleSignInClient.revokeAccess().addOnCompleteListener(MainActivity.this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Google Sign In failed, update UI appropriately
                                Log.w(TAG, "Revoked Access");
                                Toast.makeText(MainActivity.this, "Revoked Access", Toast.LENGTH_SHORT).show();
                            }
                        });

                startActivity(new Intent(MainActivity.this, LoginSignUpActivity.class));
            }
        });

        btnCalllAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService.apiService.callTestAPI().enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        List<User> object = response.body();
                        Gson gson = new Gson();
                        if(object != null){
                            Log.e("CallingApi", gson.toJson(object));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {

                    }
                });
            }
        });

    }
}