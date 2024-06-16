package com.example.authenticationuseraccount.fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.EditProfileActivity;
import com.example.authenticationuseraccount.activity.FavAndHisSongActivity;
import com.example.authenticationuseraccount.activity.LoginSignUpActivity;
import com.example.authenticationuseraccount.activity.PremiumActivity;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.User;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.installations.Utils;

public class FragmentProfile extends Fragment {
    private RelativeLayout layoutLogout;
    private LinearLayout layoutLoveSong, layoutHistorySong, layoutLogin, layoutRole;
    private Button loginButton, logoutButton, editProfileButton;
    private ImageView profileImage;
    private TextView profileName, tvRole, tvNumLove, tvNumHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        layoutLogin = view.findViewById(R.id.layout_Login);
        layoutLogout = view.findViewById(R.id.layout_Logout);
        layoutRole = view.findViewById(R.id.layout_Role);
        tvRole = view.findViewById(R.id.tv_Role);
        tvNumLove = view.findViewById(R.id.tv_num_love_song);
        tvNumHistory = view.findViewById(R.id.tv_num_history);
        layoutLoveSong = view.findViewById(R.id.layout_love_song);
        layoutHistorySong = view.findViewById(R.id.layout_history_song);

        loginButton = view.findViewById(R.id.loginButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        profileImage = view.findViewById(R.id.profileImage);
        profileName = view.findViewById(R.id.profileName);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginSignUpActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        layoutLoveSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typeShow = "Fav";
                Intent intent = new Intent(getContext(), FavAndHisSongActivity.class);
                intent.putExtra("type_show", typeShow);
                startActivity(intent);
            }
        });

        layoutHistorySong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typeShow = "His";
                Intent intent = new Intent(getContext(), FavAndHisSongActivity.class);
                intent.putExtra("type_show", typeShow);
                startActivity(intent);
            }
        });

        layoutRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.getInstance().getRole().equals(Constants.PREMIUM_USER))
                    ErrorUtils.showError(getContext()," 🔥🔥🔥 You Are Premium 🔥🔥🔥");
                else {
                    Intent intent = new Intent(getContext(),PremiumActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private void Logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        GoogleSignInClient mGoogleSignInClient;

        // Configure google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.w(TAG, "Signed out of google");
                updateUI(FirebaseAuth.getInstance().getCurrentUser());
            }
        });
    }

    public void updateUI(FirebaseUser user) {
        LogUtils.ApplicationLogE("Goi dc ui");
        if (user == null) {
            layoutLogin.setVisibility(View.GONE);
            layoutLogout.setVisibility(View.VISIBLE);
        } else {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutLogout.setVisibility(View.GONE);

            if (User.getInstance().getRole().equals(Constants.PREMIUM_USER))
                tvRole.setText(Constants.PREMIUM_USER + " 🔥");
            else {
                tvRole.setText(Constants.NORMAL_USER + " 🎉");
            }
            tvNumLove.setText(MediaItemHolder.getInstance().getListLoveSong().size() + " songs");
            tvNumHistory.setText(MediaItemHolder.getInstance().getListRecentSong().size() + " songs");

            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null && !photoUrl.toString().equals("")) {
                Glide.with(this)
                        .load(photoUrl)
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_profile);
            }

            profileName.setText(user.getDisplayName());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI(FirebaseAuth.getInstance().getCurrentUser());
    }
}
