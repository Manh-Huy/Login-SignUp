package com.example.authenticationuseraccount.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.SongAlbumAdapter;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.Genre;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.utils.ChillCornerRoomManager;
import com.example.authenticationuseraccount.utils.SocketIoManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AlbumActivity extends AppCompatActivity {
    private Disposable mDisposable;
    private TextView playlistTitle, songCount;
    private ImageButton btnMore, btnPlay, btnLove, btnRandom;
    private RecyclerView songRecyclerView;
    private SongAlbumAdapter songAlbumAdapter;
    private List<Song> listSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        songCount = findViewById(R.id.song_count);
        playlistTitle = findViewById(R.id.playlist_title);
        songRecyclerView = findViewById(R.id.rcv_song_list);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        btnMore = findViewById(R.id.more_button);
        btnPlay = findViewById(R.id.play_button);
        btnLove = findViewById(R.id.like_button);
        btnRandom = findViewById(R.id.random_button);

        Intent intent = getIntent();
        Genre genre = (Genre) intent.getSerializableExtra("genre_key");

        if (genre != null) {
            playlistTitle.setText(genre.getName().toUpperCase());
            getListSongByGenre(genre.getName());
        }

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

    }

    FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet;

    private void clickOpenOptionBottomSheet() {
        List<ItemSearchOption> itemSearchOptionList = new ArrayList<>();
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_add_to_playlist, Constants.ACTION_ADD_TO_PLAYLIST));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play_arrow, Constants.ACTION_ADD_TO_QUEUE));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_heart, Constants.ACTION_LOVE));
        itemSearchOptionList.add(new ItemSearchOption(leveldown.kyle.icon_packs.R.drawable.shuffle_24px, Constants.ACTION_ADD_RANDOM_PLAYLIST));

        fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                switch (itemSearchOption.getText()) {
                    case Constants.ACTION_ADD_TO_PLAYLIST:
                        Toast.makeText(AlbumActivity.this, "Thêm vào danh sách phát clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.ACTION_ADD_TO_QUEUE:
                        addPlayListToQueue(listSong);
                        break;
                    case Constants.ACTION_LOVE:
                        Toast.makeText(AlbumActivity.this, "Thích clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.ACTION_ADD_RANDOM_PLAYLIST:
                        setPlayListRandom(listSong);
                        break;
                    default:
                        Toast.makeText(AlbumActivity.this, "Unknown option clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        fragmentSearchOptionBottomSheet.show(getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }

    private void setPlayList(List<Song> listSong) {
        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            MediaItemHolder.getInstance().setListAlbumMediaItem(listSong);
            Toast.makeText(AlbumActivity.this, "Playlist Added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //Host Room
            if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                String userID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().setPlaylist(userID, listSong);
            } else {
                //Guest Room
                ErrorUtils.showError(AlbumActivity.this, "Only Host Can Change The Playlist!");
            }
        }
    }

    private void setPlayListRandom(List<Song> listSong) {
        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            MediaItemHolder.getInstance().setListAlbumMediaItemRandom(listSong);
            Toast.makeText(AlbumActivity.this, "Random Playlist Added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //Host Room
            if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                String userID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().setPlaylistRandom(userID, listSong);
            } else {
                //Guest Room
                ErrorUtils.showError(AlbumActivity.this, "Only Host Can Change The Playlist!");
            }
        }
    }

    private void addPlayListToQueue(List<Song> listSong) {
        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            MediaItemHolder.getInstance().addListAlbumMediaItem(listSong);
            Toast.makeText(AlbumActivity.this, "Playlist Added To Queue", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //Host Room
            if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                String userID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().addPlaylistToQueue(userID, listSong);
            } else {
                //Guest Room
                ErrorUtils.showError(AlbumActivity.this, "Only Host Can Change The Playlist!");
            }
        }

    }

    private void getListSongByGenre(String nameGenre) {
        ApiService.apiService.getSongByGenre(nameGenre)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Song> songs) {
                        listSong = songs;
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api Get Song by genre error");
                    }

                    @Override
                    public void onComplete() {
                        String count = listSong.size() + " bài hát";
                        songCount.setText(count);

                        FragmentActivity fragmentActivity = AlbumActivity.this;
                        songAlbumAdapter = new SongAlbumAdapter(getApplicationContext(), fragmentActivity, listSong);
                        songRecyclerView.setAdapter(songAlbumAdapter);
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