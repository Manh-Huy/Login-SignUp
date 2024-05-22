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
import com.example.authenticationuseraccount.adapter.ThumbnailSongAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongNewAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongSmallAdapter;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.Song;
import com.example.authenticationuseraccount.model.homepagemodel.Banner;

import java.util.ArrayList;
import java.util.List;

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
    private RecyclerView rcvQuickPick, rcvListenAgain, rcvRecommend;
    private ImageView searchImageView, imgMenuIcon;
    private Disposable mDisposable;
    private ThumbnailSongSmallAdapter mThumbnailSongSmallAdapter;
    private ThumbnailSongAdapter mThumbnailSongAdapter;
    private ThumbnailSongNewAdapter mThumbnailSongNewAdapter;
    private List<Song> mListSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgMenuIcon = findViewById(R.id.menuIcon);
        viewPager = findViewById(R.id.viewPager);
        circleIndicator = findViewById(R.id.circleIndicator);
        searchImageView = findViewById(R.id.searchIcon);

        //Recycle View
        rcvQuickPick = findViewById(R.id.rcv_quick_pick);
        rcvQuickPick.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvListenAgain = findViewById(R.id.rcv_listen_again);
        rcvListenAgain.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvRecommend = findViewById(R.id.rcv_recommend);
        rcvRecommend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        mListSong = new ArrayList<>();
        mThumbnailSongSmallAdapter = new ThumbnailSongSmallAdapter(HomeActivity.this,mListSong);
        mThumbnailSongAdapter = new ThumbnailSongAdapter(HomeActivity.this, mListSong);
        mThumbnailSongNewAdapter = new ThumbnailSongNewAdapter(HomeActivity.this, mListSong);

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
                        mThumbnailSongSmallAdapter.setData(mListSong);
                        //mThumbnailSongNewAdapter.setData(mListSong);
                        mThumbnailSongAdapter.setData(mListSong);
                        rcvQuickPick.setAdapter(mThumbnailSongSmallAdapter);
                        rcvRecommend.setAdapter(mThumbnailSongAdapter);
                        rcvListenAgain.setAdapter(mThumbnailSongAdapter);
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
}
