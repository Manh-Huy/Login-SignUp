package com.example.authenticationuseraccount.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.model.business.User;
import com.example.authenticationuseraccount.utils.RealPathUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfileActivity extends AppCompatActivity {
    private Disposable mDisposable;
    private FirebaseUser user;
    private String signInMethodUser;
    private ImageView imgProfile;
    private TextView tvChangePicture, tvUsername, tvEmail, tvID, tvChangePassword;
    private Button btnUpdate;
    private static final int MY_REQUEST_CODE = 10;
    private File mFile;
    private String mMimeType;
    private String imageURLChange;
    private String usernameChange;


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

        checkUserType(user);

        if (Objects.equals(signInMethodUser, "Email")) {
            tvChangePassword.setVisibility(View.VISIBLE);
        } else if (Objects.equals(signInMethodUser, "Google")) {
            tvChangePassword.setVisibility(View.GONE);
        }

        tvUsername.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());
        tvID.setText(user.getUid());
        Uri photoUrl = user.getPhotoUrl();
        if (photoUrl != null && !photoUrl.toString().equals("")) {
            Glide.with(this)
                    .load(photoUrl)
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.ic_profile);
        }

        tvChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });

        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, ReauthenticationActivity.class);
                startActivity(intent);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tvUsername.getText().toString())) {
                    Toast.makeText(EditProfileActivity.this, "Please Enter username to change", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mFile == null) {
                    Toast.makeText(EditProfileActivity.this, "null cmnr", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(EditProfileActivity.this, "có ùi", Toast.LENGTH_SHORT).show();
                }

                if (tvUsername.getText() == user.getDisplayName()) {
                    usernameChange = null;
                }
                else {
                    usernameChange = tvUsername.getText().toString();
                    UpdateUsernameInFirebase(usernameChange);
                }

                updateUserProfile(user.getUid(), usernameChange, mMimeType, mFile);
            }
        });
    }

    private void checkUserType(FirebaseUser user) {
        String id = user.getUid();
        ApiService.apiService.getUserById(id).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User mUser = response.body();
                LogUtils.ApplicationLogD("Call thanh cong");
                if (mUser != null)
                {
                    signInMethodUser = mUser.getSignInMethod();
                }

            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    LogUtils.ApplicationLogE("onActivityResult: ");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }
                        Uri uri = data.getData();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            String strRealPath = RealPathUtil.getRealPath(getApplicationContext(), uri);
                            File file = new File(strRealPath);
                            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
                            mMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
                            mFile = file;
                            //Log.d("mimeType", mMimeType);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        imgProfile.setImageBitmap(bitmap);
                    }
                }
            }
    );
    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    private void updateUserProfile(String userID, String username, String mimeTypeFile, File image) {
        String strUsername = username.trim();

        RequestBody requestBodyUsername = RequestBody.create(MediaType.parse("multipart/form-data"), strUsername);
        RequestBody requestBodyAva = RequestBody.create(MediaType.parse(mimeTypeFile), image);

        MultipartBody.Part multipartBodyAvt = MultipartBody.Part.createFormData("imageURL", image.getName(), requestBodyAva);

        ApiService.apiService.updateUserProfile(userID, requestBodyUsername, multipartBodyAvt).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LogUtils.ApplicationLogE("Call API thanh cong");
                imageURLChange = response.body();

                if (mFile != null) {
                    UpdateAvatarUserInFirebase(imageURLChange);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                LogUtils.ApplicationLogE("Call API that bai");
                LogUtils.ApplicationLogE(t.getMessage());
                LogUtils.ApplicationLogE(t.toString());
                LogUtils.ApplicationLogE(t.getStackTrace().toString());
            }
        });


    }

    private void UpdateUsernameInFirebase(String username) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Update username Successful", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void UpdateAvatarUserInFirebase(String imageURL) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(imageURL))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Update avatar Successful", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    public void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }

}
