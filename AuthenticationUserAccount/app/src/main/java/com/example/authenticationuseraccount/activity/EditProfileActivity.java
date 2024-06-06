package com.example.authenticationuseraccount.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authenticationuseraccount.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends AppCompatActivity {
    private FirebaseUser user;
    private ImageView imgProfile;
    private TextView tvChangePicture, tvUsername, tvEmail, tvID, tvChangePassword;
    private Button btnUpdate;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();

        imgProfile = findViewById(R.id.img_profile);
        tvChangePicture = findViewById(R.id.tv_change_picture);
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        tvID = findViewById(R.id.tv_id);
        tvChangePassword = findViewById(R.id.tv_change_password);
        btnUpdate = findViewById(R.id.btn_update);

        tvUsername.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());
        tvID.setText(user.getUid());

        tvChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(EditProfileActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tvUsername.getText().toString())) {
                    Toast.makeText(EditProfileActivity.this, "Please Enter username to change", Toast.LENGTH_SHORT).show();
                    return;
                }

//                UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder()
//                        .setDisplayName(tvUsername.getText().toString());
//
//                if (selectedImageUri != null) {
//                    profileUpdatesBuilder.setPhotoUri(selectedImageUri);
//                }
//
//                UserProfileChangeRequest profileUpdates = profileUpdatesBuilder.build();
//
//                user.updateProfile(profileUpdates)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_LONG).show();
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });
                Toast.makeText(EditProfileActivity.this, selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfile.setImageURI(selectedImageUri);
        }
    }
}
