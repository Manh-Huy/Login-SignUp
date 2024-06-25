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
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.Playlist;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.utils.ChillCornerRoomManager;
import com.example.authenticationuseraccount.utils.SocketIoManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavAndHisSongActivity extends AppCompatActivity {
    FragmentActivity fragmentActivity;
    private Disposable mDisposable;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private LinearLayout layoutNoData;
    private TextView tvSongTitle, tvUserName;
    private ImageButton btnMore, btnBack, btnPlay, btnRandom;
    private ImageView imgProfile;
    private RecyclerView songRecyclerView;
    private SongAlbumAdapter songAdapter;
    private List<Song> listSong;
    private List<Playlist> listUserPlaylist = new ArrayList<>();

    String typeShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_and_his_song);
        btnPlay = findViewById(R.id.play_button);
        btnRandom = findViewById(R.id.random_button);
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
        getPlaylistUserByID(user.getUid());

        Intent intent = getIntent();
        typeShow = (String) intent.getSerializableExtra("type_show");

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayList(listSong);
            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayListRandom(listSong);
            }
        });

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
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play_arrow, Constants.ACTION_ADD_TO_QUEUE));
        itemSearchOptionList.add(new ItemSearchOption(leveldown.kyle.icon_packs.R.drawable.shuffle_24px, Constants.ACTION_ADD_RANDOM_PLAYLIST));

        FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                switch (itemSearchOption.getText()) {
                    case Constants.ACTION_ADD_TO_QUEUE:
                        addPlayListToQueue(listSong);
                        break;
                    case Constants.ACTION_ADD_RANDOM_PLAYLIST:
                        setPlayListRandom(listSong);
                        break;
                    default:
                        Toast.makeText(FavAndHisSongActivity.this, "Unknown option clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        fragmentSearchOptionBottomSheet.show(getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }


    private void setPlayList(List<Song> listSong) {
        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            MediaItemHolder.getInstance().setListAlbumMediaItem(listSong);
            Toast.makeText(FavAndHisSongActivity.this, "Playlist Added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //Host Room
            if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                String userID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().setPlaylist(userID, listSong);
                finish();
            } else {
                //Guest Room
                ErrorUtils.showError(FavAndHisSongActivity.this, "Only Host Can Change The Playlist!");
            }
        }
    }

    private void addPlayListToQueue(List<Song> listSong) {
        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            MediaItemHolder.getInstance().addListAlbumMediaItem(listSong);
            Toast.makeText(FavAndHisSongActivity.this, "Playlist Added To Queue", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //Host Room
            if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                String userID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().addPlaylistToQueue(userID, listSong);
                finish();
            } else {
                //Guest Room
                ErrorUtils.showError(FavAndHisSongActivity.this, "Only Host Can Change The Playlist!");
            }
        }

    }

    private void setPlayListRandom(List<Song> listSong) {
        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            MediaItemHolder.getInstance().setListAlbumMediaItemRandom(listSong);
            Toast.makeText(FavAndHisSongActivity.this, tvSongTitle.getText() + " Randomly Added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //Host Room
            if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                String userID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().setPlaylistRandom(userID, listSong);
                finish();
            } else {
                //Guest Room
                ErrorUtils.showError(FavAndHisSongActivity.this, "Only Host Can Change The Playlist!");
            }
        }
    }

    private void getPlaylistUserByID(String userID) {
        ApiService.apiService.getPLayListByID(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Playlist>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<Playlist> playlists) {
                        listUserPlaylist.addAll(playlists);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api get playlist error");
                    }

                    @Override
                    public void onComplete() {
                        if (Objects.equals(typeShow, "Fav")) {
                            tvSongTitle.setText("YOUR FAVORITE");
                            listSong = MediaItemHolder.getInstance().getListLoveSong();
                            updateUI();
                            songAdapter = new SongAlbumAdapter(getApplicationContext(), fragmentActivity, listSong, listUserPlaylist);
                            songRecyclerView.setAdapter(songAdapter);
                        } else if (Objects.equals(typeShow, "His")) {
                            tvSongTitle.setText("YOUR HISTORY");
                            listSong = MediaItemHolder.getInstance().getListRecentSong();
                            updateUI();
                            songAdapter = new SongAlbumAdapter(getApplicationContext(), fragmentActivity, listSong, listUserPlaylist);
                            songRecyclerView.setAdapter(songAdapter);
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