package com.example.authenticationuseraccount.activiy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.BannerAdapter;
import com.example.authenticationuseraccount.adapter.SuggestSongAdapter;
import com.example.authenticationuseraccount.adapter.TopTrendingAdapter;
import com.example.authenticationuseraccount.model.homepagemodel.Banner;
import com.example.authenticationuseraccount.model.homepagemodel.SuggestSong;
import com.example.authenticationuseraccount.model.homepagemodel.TopTrending;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class HomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private BannerAdapter bannerAdapter;
    private RecyclerView suggestRecyclerView, playListRecyclerView, topTrendingRecyclerView, newReleaseRecyclerView;

    private ImageView searchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // banner
        viewPager = findViewById(R.id.viewPager);
        circleIndicator = findViewById(R.id.circleIndicator);

        bannerAdapter = new BannerAdapter(this,getListBanner());
        viewPager.setAdapter(bannerAdapter);

        circleIndicator.setViewPager(viewPager);
        bannerAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());

        //suggestRecyclerView
        suggestRecyclerView = findViewById(R.id.suggestRecyclerView);
        suggestRecyclerView.setHasFixedSize(true);
        suggestRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<SuggestSong> listSuggestSong = new ArrayList<>();
        listSuggestSong.add(new SuggestSong(R.drawable.chungtacuahientai, "Chúng ta của hiện tại", "Sơn Tùng MTP"));
        listSuggestSong.add(new SuggestSong(R.drawable.anhlangoailecuaem, "Anh là ngoại lệ của em", "Phương Ly"));
        listSuggestSong.add(new SuggestSong(R.drawable.hoanghonnho, "Hoàng hôn nhớ", "Anh Tú"));
        listSuggestSong.add(new SuggestSong(R.drawable.freeflowkhonghut, "Free Flow Không Hút", "Ricky Start"));

        SuggestSongAdapter suggestSongAdapter = new SuggestSongAdapter(listSuggestSong);
        suggestRecyclerView.setAdapter(suggestSongAdapter);

        //playListRecyclerView
        playListRecyclerView = findViewById(R.id.playListRecyclerView);
        playListRecyclerView.setHasFixedSize(true);
        playListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //topTrendingRecyclerView
        topTrendingRecyclerView = findViewById(R.id.topTrendingRecyclerView);
        topTrendingRecyclerView.setHasFixedSize(true);
        topTrendingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<TopTrending> listTopTrending = new ArrayList<>();
        listTopTrending.add(new TopTrending(R.drawable.khoalybiet, "Khóa Ly Biệt", "Anh Tú"));
        listTopTrending.add(new TopTrending(R.drawable.nauchoeman, "Nấu cho em ăn", "Đen Vâu"));
        listTopTrending.add(new TopTrending(R.drawable.bandoi, "Bạn Đời", "Karik"));
        listTopTrending.add(new TopTrending(R.drawable.suytnuathi, "Suýt nữa thì", "Andiez"));

        TopTrendingAdapter topTrendingAdapter = new TopTrendingAdapter(listTopTrending);
        topTrendingRecyclerView.setAdapter(topTrendingAdapter);

        //newReleaseRecyclerView
        newReleaseRecyclerView = findViewById(R.id.newReleaseRecyclerView);
        newReleaseRecyclerView.setHasFixedSize(true);
        newReleaseRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // search Image View
        searchImageView = findViewById(R.id.searchIcon);
        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }
    private List<Banner> getListBanner() {
        List<Banner> list = new ArrayList<>();
        list.add(new Banner(R.drawable.banner1));
        list.add(new Banner(R.drawable.banner2));
        list.add(new Banner(R.drawable.banner3));
        list.add(new Banner(R.drawable.banner4));

        return list;
    }
}