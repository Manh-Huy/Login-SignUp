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
import com.example.authenticationuseraccount.adapter.ThumbnailSongAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongSmallAdapter;
import com.example.authenticationuseraccount.model.Song;
import com.example.authenticationuseraccount.model.homepagemodel.Banner;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class HomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private BannerAdapter bannerAdapter;
    private RecyclerView rcvQuickPick, rcvListenAgain, rcvRecommend;

    private ImageView searchImageView , imgMenuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgMenuIcon = findViewById(R.id.menuIcon);
        imgMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginSignUpActivity.class);
                startActivity(intent);
            }
        });

        // banner
        viewPager = findViewById(R.id.viewPager);
        circleIndicator = findViewById(R.id.circleIndicator);

        bannerAdapter = new BannerAdapter(this,getListBanner());
        viewPager.setAdapter(bannerAdapter);

        circleIndicator.setViewPager(viewPager);
        bannerAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());

        //Quick Pick recycle view
        rcvQuickPick = findViewById(R.id.rcv_quick_pick);
        rcvQuickPick.setHasFixedSize(true);
        rcvQuickPick.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        List<Song> listQuickPickSong = new ArrayList<>();
        listQuickPickSong.add(new Song(R.drawable.chungtacuahientai, "Chúng ta của hiện tại", "Sơn Tùng MTP"));
        listQuickPickSong.add(new Song(R.drawable.anhlangoailecuaem, "Anh là ngoại lệ của em", "Phương Ly"));
        listQuickPickSong.add(new Song(R.drawable.hoanghonnho, "Hoàng hôn nhớ", "Anh Tú"));

        ThumbnailSongSmallAdapter thumbnailSongSmallAdapter = new ThumbnailSongSmallAdapter(listQuickPickSong);
        rcvQuickPick.setAdapter(thumbnailSongSmallAdapter);

        //Listen Again recycle view
        rcvListenAgain = findViewById(R.id.rcv_listen_again);
        rcvListenAgain.setHasFixedSize(false);
        rcvListenAgain.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ThumbnailSongAdapter listenAgainSongAdapter = new ThumbnailSongAdapter(getListSong());
        rcvListenAgain.setAdapter(listenAgainSongAdapter);

        //Recommend recycle view
        rcvRecommend = findViewById(R.id.rcv_recommend);
        rcvRecommend.setHasFixedSize(true);
        rcvRecommend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ThumbnailSongAdapter recommendSongAdapter = new ThumbnailSongAdapter(getListSong());
        rcvRecommend.setAdapter(recommendSongAdapter);


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

    private List<Song> getListSong(){
        List<Song> list =new ArrayList<>();
        list.add(new Song(R.drawable.khoalybiet, "Khóa Ly Biệt", "Anh Tú"));
        list.add(new Song(R.drawable.nauchoeman, "Nấu cho em ăn", "Đen Vâu"));
        list.add(new Song(R.drawable.bandoi, "Bạn Đời", "Karik"));
        list.add(new Song(R.drawable.suytnuathi, "Suýt nữa thì", "Andiez"));
        return list;
    }
}
