package com.example.authenticationuseraccount.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.SongPlaylistAdapter;
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
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PLaylistActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Disposable mDisposable;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseUser user;
    private TextView playlistTitle, songCount;
    private ImageButton btnMore, btnPlay, btnRandom;
    private ImageView imgDeletePlaylist, imgPLaylist;
    private RecyclerView songRecyclerView;
    private SongPlaylistAdapter songAlbumAdapter;
    private List<Song> playListSong;
    private Playlist playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        imgPLaylist = findViewById(R.id.playlist_image);
        songCount = findViewById(R.id.song_count);
        playlistTitle = findViewById(R.id.playlist_title);
        songRecyclerView = findViewById(R.id.rcv_song_list);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        btnMore = findViewById(R.id.more_button);
        btnPlay = findViewById(R.id.play_button);
        btnRandom = findViewById(R.id.random_button);
        imgDeletePlaylist = findViewById(R.id.img_delete_playlist);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        playListSong = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        playlist = (Playlist) intent.getSerializableExtra("playlist");

        if (playlist != null) {
            String playlistName = playlist.getPlaylistName();
            playlistTitle.setText(playlistName.toUpperCase());
            getSpecificPlaylist(user.getUid(), playlistName);
        }

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenOptionBottomSheet();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayList(playListSong);
            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayListRandom(playListSong);
            }
        });
        
        imgDeletePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playlist != null) {
                    deletePlaylist(user.getUid(), playlist.getPlaylistName());
                    Toast.makeText(PLaylistActivity.this, "Deleted playlist!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setPlayList(List<Song> listSong) {
        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            MediaItemHolder.getInstance().setListAlbumMediaItem(listSong);
            Toast.makeText(PLaylistActivity.this, "Playlist Added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //Host Room
            if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                String userID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().setPlaylist(userID, listSong);
            } else {
                //Guest Room
                ErrorUtils.showError(PLaylistActivity.this, "Only Host Can Change The Playlist!");
            }
        }
    }

    private void setPlayListRandom(List<Song> listSong) {
        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            MediaItemHolder.getInstance().setListAlbumMediaItemRandom(listSong);
            Toast.makeText(PLaylistActivity.this, "Random Playlist Added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //Host Room
            if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                String userID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().setPlaylistRandom(userID, listSong);
            } else {
                //Guest Room
                ErrorUtils.showError(PLaylistActivity.this, "Only Host Can Change The Playlist!");
            }
        }
    }

    private void addPlayListToQueue(List<Song> listSong) {
        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            MediaItemHolder.getInstance().addListAlbumMediaItem(listSong);
            Toast.makeText(PLaylistActivity.this, "Playlist Added To Queue", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //Host Room
            if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                String userID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().addPlaylistToQueue(userID, listSong);
            } else {
                //Guest Room
                ErrorUtils.showError(PLaylistActivity.this, "Only Host Can Change The Playlist!");
            }
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
                        addPlayListToQueue(playListSong);
                        break;
                    case Constants.ACTION_ADD_RANDOM_PLAYLIST:
                        setPlayListRandom(playListSong);
                        break;
                    default:
                        Toast.makeText(PLaylistActivity.this, "Unknown option clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        fragmentSearchOptionBottomSheet.show(getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }

    private void getSpecificPlaylist(String userID, String playlistName) {
        ApiService.apiService.getSpecificPlaylist(userID, playlistName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<Song> songs) {
                        playListSong.addAll(songs);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api getSpecificPlaylist error");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.ApplicationLogE("Call api getSpecificPlaylist successfully");

                        String count = playListSong.size() + " Songs";
                        songCount.setText(count);

                        FragmentActivity fragmentActivity = PLaylistActivity.this;
                        songAlbumAdapter = new SongPlaylistAdapter(getApplicationContext(), fragmentActivity, playListSong, playlist);
                        songRecyclerView.setAdapter(songAlbumAdapter);
                        Random random = new Random();
                        int randomIndex = random.nextInt(playListSong.size());
                        Glide.with(PLaylistActivity.this).load(playListSong.get(randomIndex).getImageURL()).into(imgPLaylist);

                        swipeRefreshLayout.setRefreshing(false);

                    }
                });

    }

    @SuppressLint("CheckResult")
    private void deletePlaylist(String userID, String playlistName) {
        ApiService.apiService.deletePlaylist(userID, playlistName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    LogUtils.ApplicationLogD("Call API delete Playlist Successfully");
                    finish();
                }, throwable -> {
                    LogUtils.ApplicationLogE("Call API delete Playlist Failed");
                });
    }

    @Override
    public void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        playListSong.clear();
        String playlistName = playlist.getPlaylistName();
        getSpecificPlaylist(user.getUid(), playlistName);
        playlistTitle.setText(playlistName.toUpperCase());
    }
}