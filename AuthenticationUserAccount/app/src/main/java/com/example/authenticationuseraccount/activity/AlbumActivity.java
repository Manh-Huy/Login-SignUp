package com.example.authenticationuseraccount.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.SongAlbumAdapter;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.Genre;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AlbumActivity extends AppCompatActivity {
    private Disposable mDisposable;
    private TextView playlistTitle, songCount;
    private ImageButton btnMore;
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

        Intent intent = getIntent();
        Genre genre = (Genre) intent.getSerializableExtra("genre_key");

        if (genre != null) {
            playlistTitle.setText(genre.getName().toUpperCase());
            getListSongByGenre(genre.getName());
        }
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenOptionBottomSheet();
            }
        });

    }
    private void clickOpenOptionBottomSheet() {
        List<ItemSearchOption> itemSearchOptionList = new ArrayList<>();
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_add_to_playlist, "Thêm vào danh sách phát"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play, "Phát"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_heart, "Thích"));
        itemSearchOptionList.add(new ItemSearchOption(leveldown.kyle.icon_packs.R.drawable.shuffle_24px, "Phát ngẫu nhiên"));

        FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                switch (itemSearchOption.getText()) {
                    case "Thêm vào danh sách phát":
                        // Handle "Thích" action
                        Toast.makeText(AlbumActivity.this, "Thêm vào danh sách phát clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Phát":
                        // Handle "Tải xuống" action
                        Toast.makeText(AlbumActivity.this, "Phát clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Thích":
                        Toast.makeText(AlbumActivity.this, "Thích clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Phát ngẫu nhiên":
                        Toast.makeText(AlbumActivity.this, "Phát ngẫu nhiên clicked", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        // Handle default action
                        Toast.makeText(AlbumActivity.this, "Unknown option clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        fragmentSearchOptionBottomSheet.show(getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
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