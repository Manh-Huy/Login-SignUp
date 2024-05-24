package com.example.authenticationuseraccount.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.BannerAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailGenreAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongNewAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongSmallAdapter;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.Genre;
import com.example.authenticationuseraccount.model.IClickGenreRecyclerViewListener;
import com.example.authenticationuseraccount.model.IClickSongRecyclerViewListener;
import com.example.authenticationuseraccount.model.Song;
import com.example.authenticationuseraccount.model.homepagemodel.Banner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.relex.circleindicator.CircleIndicator;

public class HomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private BannerAdapter bannerAdapter;
    private RecyclerView rcvQuickPick, rcvListenAgain, rcvRecommend, rcvNewRelease, rcvGenre;
    private ImageView searchImageView, imgMenuIcon;
    private Disposable mDisposable;
    private ThumbnailSongSmallAdapter mThumbnailSongSmallAdapter_QuickPick;
    private ThumbnailGenreAdapter mThumbnailGenreAdapter;
    private ThumbnailSongAdapter mThumbnailSongAdapter_ListenAgain;
    private ThumbnailSongAdapter mThumbnailSongAdapter_Recommend;
    private ThumbnailSongNewAdapter mThumbnailSongNewAdapter_NewRelease;
    private List<Genre> mLisGenre;
    private List<Song> mListSong;
    private List<Song> listSongQuickPick;
    private List<Song> listNewReleaseSong;
    private final int numberSongShowInQuickPick = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgMenuIcon = findViewById(R.id.menuIcon);
        viewPager = findViewById(R.id.viewPager);
        circleIndicator = findViewById(R.id.circleIndicator);
        searchImageView = findViewById(R.id.searchIcon);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        //Recycle View
        rcvQuickPick = findViewById(R.id.rcv_quick_pick);
        rcvQuickPick.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvListenAgain = findViewById(R.id.rcv_listen_again);
        rcvListenAgain.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvRecommend = findViewById(R.id.rcv_recommend);
        rcvRecommend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvNewRelease = findViewById(R.id.rcv_new_release);
        rcvNewRelease.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvGenre = findViewById(R.id.rcv_genre);
        rcvGenre.setLayoutManager(gridLayoutManager);

        mListSong = new ArrayList<>();
        mThumbnailSongSmallAdapter_QuickPick = new ThumbnailSongSmallAdapter(HomeActivity.this, mListSong, new IClickSongRecyclerViewListener() {
            @Override
            public void onClickItemSong(Song song) {
                onClickGoToMP3Player(song);
            }
        });
        mThumbnailSongNewAdapter_NewRelease = new ThumbnailSongNewAdapter(HomeActivity.this, mListSong, new IClickSongRecyclerViewListener() {
            @Override
            public void onClickItemSong(Song song) {
                onClickGoToMP3Player(song);
            }
        });
        mThumbnailSongAdapter_ListenAgain = new ThumbnailSongAdapter(HomeActivity.this, mListSong, new IClickSongRecyclerViewListener() {
            @Override
            public void onClickItemSong(Song song) {
                onClickGoToMP3Player(song);
            }
        });
        mThumbnailSongAdapter_Recommend = new ThumbnailSongAdapter(HomeActivity.this, mListSong, new IClickSongRecyclerViewListener() {
            @Override
            public void onClickItemSong(Song song) {
                onClickGoToMP3Player(song);
            }
        });
        mThumbnailGenreAdapter = new ThumbnailGenreAdapter(HomeActivity.this, mLisGenre, new IClickGenreRecyclerViewListener() {
            @Override
            public void onClickItemGenre(Genre genre) {

            }
        });

//        mThumbnailSongNewAdapter = new ThumbnailSongNewAdapter(HomeActivity.this, mListSong, new IClickSongRecyclerViewListener() {
//            @Override
//            public void onClickItemSong(Song song) {
//                onClickGoToMP3Player(song);
//            }
//        });

        //Banner
        bannerAdapter = new BannerAdapter(this, getListBanner());
        viewPager.setAdapter(bannerAdapter);
        circleIndicator.setViewPager(viewPager);
        bannerAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());


        imgMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginSignUpActivity.class);
                startActivity(intent);
            }
        });

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        getListSong();
    }

    private List<Genre> geListGenre()
    {
        List<Genre> list = new ArrayList<>();
        list.add(new Genre("01", "Nhạc Trẻ"));
        list.add(new Genre("02", "Trữ Tình"));
        list.add(new Genre("03", "Remix Việt"));
        list.add(new Genre("04", "Rap Việt"));
        list.add(new Genre("05", "Tiền Chiến"));
        list.add(new Genre("06", "Nhạc Trịnh"));
        list.add(new Genre("07", "Rock Việt"));
        list.add(new Genre("08", "Cách Mạng"));

        return list;
    }

    private List<Banner> getListBanner() {
        List<Banner> list = new ArrayList<>();
        list.add(new Banner(R.drawable.banner1));
        list.add(new Banner(R.drawable.banner2));
        list.add(new Banner(R.drawable.banner3));
        list.add(new Banner(R.drawable.banner4));
        return list;
    }

    private void getListSong() {
        ApiService.apiService.getSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<Song> songs) {
                        mListSong = songs;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.e("Call api error");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.d("Call api success");
                        listSongQuickPick = takeMusicWithHighView(numberSongShowInQuickPick, mListSong);
                        listNewReleaseSong = takeNewReLeaseMusic(mListSong);
                        mLisGenre = geListGenre();

                        mThumbnailSongSmallAdapter_QuickPick.setData(listSongQuickPick);
                        mThumbnailSongNewAdapter_NewRelease.setData(listNewReleaseSong);
                        mThumbnailGenreAdapter.setData(mLisGenre);
                        mThumbnailSongAdapter_ListenAgain.setData(mListSong);
                        mThumbnailSongAdapter_Recommend.setData(mListSong);

                        rcvQuickPick.setAdapter(mThumbnailSongSmallAdapter_QuickPick);
                        rcvNewRelease.setAdapter(mThumbnailSongNewAdapter_NewRelease);
                        rcvGenre.setAdapter(mThumbnailGenreAdapter);
                        rcvListenAgain.setAdapter(mThumbnailSongAdapter_ListenAgain);
                        rcvRecommend.setAdapter(mThumbnailSongAdapter_Recommend);

                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }

    private List<Song> takeMusicWithHighView(int numberSong, List<Song> listSong) {
        Collections.sort(listSong, new Comparator<Song>() {
            @Override
            public int compare(Song s1, Song s2) {
                int views1 = Integer.parseInt(s1.getViews());
                int views2 = Integer.parseInt(s2.getViews());
                return Integer.compare(views2, views1);
            }
        });

        List<Song> topSongs = new ArrayList<>();
        for (int i = 0; i < numberSong && i < listSong.size(); i++) {
            topSongs.add(listSong.get(i));
        }

        return topSongs;
    }

    private List<Song> takeNewReLeaseMusic(List<Song> listSong) {
        List<Song> newReleaseSongs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();

        for (Song song : listSong) {
            try {
                Date createdAtDate = sdf.parse(song.getCreatedAt());
                long diffInMillies = Math.abs(currentDate.getTime() - createdAtDate.getTime());
                long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);

                // Kiểm tra nếu ngày phát hành trong vòng 7 ngày
                if (diffInDays <= 7) {
                    newReleaseSongs.add(song);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return newReleaseSongs;
    }
    private void onClickGoToMP3Player(Song song) {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", song);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }

}
