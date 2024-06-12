package com.example.authenticationuseraccount.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.SongAlbumAdapter;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;

public class FavAndHisSongActivity extends AppCompatActivity {
    FragmentActivity fragmentActivity;
    private Disposable mDisposable;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private LinearLayout layoutNoData;
    private TextView tvSongTitle, tvUserName;
    private ImageButton btnMore, btnBack;
    private ImageView imgProfile;
    private RecyclerView songRecyclerView;
    private SongAlbumAdapter songAdapter;
    private List<Song> listSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_and_his_song);

        layoutNoData = findViewById(R.id.layout_no_data);
        btnMore = findViewById(R.id.more_button);
        btnBack = findViewById(R.id.backButton);
        songRecyclerView = findViewById(R.id.recyclerViewSong);
        tvSongTitle = findViewById(R.id.songTitle);
        imgProfile = findViewById(R.id.userImage);
        tvUserName = findViewById(R.id.userName);

        songRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fragmentActivity = FavAndHisSongActivity.this;

        showUserProfile();

        Intent intent = getIntent();
        String typeShow = (String) intent.getSerializableExtra("type_show");

        if (Objects.equals(typeShow, "Fav")) {
            tvSongTitle.setText("YOUR FAVORITE");
            listSong = MediaItemHolder.getInstance().getListLoveSong();
            updateUI();
            songAdapter = new SongAlbumAdapter(getApplicationContext(), fragmentActivity, listSong);
            songRecyclerView.setAdapter(songAdapter);
        } else if (Objects.equals(typeShow, "His")) {
            tvSongTitle.setText("YOUR HISTORY");
            listSong = MediaItemHolder.getInstance().getListRecentSong();
            updateUI();
            songAdapter = new SongAlbumAdapter(getApplicationContext(), fragmentActivity, listSong);
            songRecyclerView.setAdapter(songAdapter);
        }

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenOptionBottomSheet();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showUserProfile() {
        Uri photoUrl = user.getPhotoUrl();
        if (photoUrl != null && !photoUrl.toString().equals("")) {
            Glide.with(this)
                    .load(photoUrl)
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.ic_profile);
        }
        tvUserName.setText(user.getDisplayName());
    }

    private void updateUI() {
        if (listSong.isEmpty()) {
            layoutNoData.setVisibility(View.VISIBLE);
            songRecyclerView.setVisibility(View.GONE);
        } else {
            layoutNoData.setVisibility(View.GONE);
            songRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void clickOpenOptionBottomSheet() {
        List<ItemSearchOption> itemSearchOptionList = new ArrayList<>();
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play_arrow, "Phát tất cả"));
        itemSearchOptionList.add(new ItemSearchOption(leveldown.kyle.icon_packs.R.drawable.shuffle_24px, "Phát ngẫu nhiên"));

        FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                switch (itemSearchOption.getText()) {
                    case "Phát tất cả":
                        // Handle "Tải xuống" action
                        Toast.makeText(FavAndHisSongActivity.this, "Phát tất cả clicked", Toast.LENGTH_SHORT).show();
                        break;

                    case "Phát ngẫu nhiên":
                        Toast.makeText(FavAndHisSongActivity.this, "Phát ngẫu nhiên clicked", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        // Handle default action
                        Toast.makeText(FavAndHisSongActivity.this, "Unknown option clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        fragmentSearchOptionBottomSheet.show(getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }


    @Override
    public void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }
}