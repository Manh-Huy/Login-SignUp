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

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.SongPlaylistAdapter;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.Playlist;
import com.example.authenticationuseraccount.model.business.Song;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PLaylistActivity extends AppCompatActivity {
    private Disposable mDisposable;
    FirebaseUser user;
    private TextView playlistTitle, songCount;
    private ImageButton btnMore;
    private ImageView imgDeletePlaylist;
    private RecyclerView songRecyclerView;
    private SongPlaylistAdapter songAlbumAdapter;
    private List<Song> playListSong;
    private Playlist playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        songCount = findViewById(R.id.song_count);
        playlistTitle = findViewById(R.id.playlist_title);
        songRecyclerView = findViewById(R.id.rcv_song_list);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        btnMore = findViewById(R.id.more_button);
        imgDeletePlaylist = findViewById(R.id.img_delete_playlist);

        playListSong = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        playlist = (Playlist) intent.getSerializableExtra("playlist");

        if (playlist != null) {
            String playlistName = playlist.getPlaylistName();
            getSpecificPlaylist(user.getUid(), playlistName);
            playlistTitle.setText(playlistName.toUpperCase());
            String count = playlist.getListSong().size() + " bài hát";
            songCount.setText(count);
        }

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenOptionBottomSheet();
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
    private void clickOpenOptionBottomSheet() {
        List<ItemSearchOption> itemSearchOptionList = new ArrayList<>();
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_add_to_playlist, "Thêm vào danh sách phát"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play_arrow, "Phát"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_heart, "Thích"));
        itemSearchOptionList.add(new ItemSearchOption(leveldown.kyle.icon_packs.R.drawable.shuffle_24px, "Phát ngẫu nhiên"));

        FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                switch (itemSearchOption.getText()) {
                    case "Thêm vào danh sách phát":
                        Toast.makeText(PLaylistActivity.this, "Thêm vào danh sách phát clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Phát":
                        Toast.makeText(PLaylistActivity.this, "Phát clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Thích":
                        Toast.makeText(PLaylistActivity.this, "Thích clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Phát ngẫu nhiên":
                        Toast.makeText(PLaylistActivity.this, "Phát ngẫu nhiên clicked", Toast.LENGTH_SHORT).show();
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
                        FragmentActivity fragmentActivity = PLaylistActivity.this;
                        List<Playlist> playlistList = new ArrayList<>();
                        songAlbumAdapter = new SongPlaylistAdapter(getApplicationContext(), fragmentActivity, playListSong, playlist);
                        songRecyclerView.setAdapter(songAlbumAdapter);
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
}