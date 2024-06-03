package com.example.authenticationuseraccount.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.LoginSignUpActivity;
import com.example.authenticationuseraccount.activity.MediaPlayerActivity;
import com.example.authenticationuseraccount.activity.SearchActivity;
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
import com.example.authenticationuseraccount.model.ListenHistory;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.model.homepagemodel.Banner;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.service.MusicService;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.relex.circleindicator.CircleIndicator3;


public class FragmentHome extends Fragment {

    private FirebaseUser user;
    private ViewPager2 viewPager;
    private CircleIndicator3 circleIndicator;
    private BannerAdapter bannerAdapter;
    private RecyclerView rcvQuickPick, rcvListenAgain, rcvRecommend, rcvNewRelease, rcvGenre;
    private ImageView searchImageView, imgMenuIcon;
    private Disposable mDisposable;
    private ThumbnailSongSmallAdapter mThumbnailSongSmallAdapter_QuickPick;
    private ThumbnailGenreAdapter mThumbnailGenreAdapter;
    private ThumbnailSongAdapter mThumbnailSongAdapter_ListenAgain;
    private ThumbnailSongAdapter mThumbnailSongAdapter_Recommend;
    private ThumbnailSongNewAdapter mThumbnailSongNewAdapter_NewRelease;
    private List<Banner> mListBanner = new ArrayList<>();
    private List<Genre> mLisGenre = new ArrayList<>();
    private List<Song> mListSong = new ArrayList<>();
    private List<Song> listSongQuickPick;
    private List<Song> listNewReleaseSong;
    private List<ListenHistory> mListUserListenHistory;
    private final int numberSongShowInQuickPick = 10;

    Timer mTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        imgMenuIcon = view.findViewById(R.id.menuIcon);
        viewPager = view.findViewById(R.id.viewPager);
        circleIndicator = view.findViewById(R.id.circleIndicator);
        searchImageView = view.findViewById(R.id.searchIcon);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);

        // Recycle View
        rcvQuickPick = view.findViewById(R.id.rcv_quick_pick);
        rcvQuickPick.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rcvListenAgain = view.findViewById(R.id.rcv_listen_again);
        rcvListenAgain.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvRecommend = view.findViewById(R.id.rcv_recommend);
        rcvRecommend.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvNewRelease = view.findViewById(R.id.rcv_new_release);
        rcvNewRelease.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvGenre = view.findViewById(R.id.rcv_genre);
        rcvGenre.setLayoutManager(gridLayoutManager);

        mThumbnailSongSmallAdapter_QuickPick = new ThumbnailSongSmallAdapter(getContext(), mListSong, new IClickSongRecyclerViewListener() {
            @Override
            public void onClickItemSong(Song song) {
                onClickGoToMP3Player(song);
            }
        });

        mThumbnailSongNewAdapter_NewRelease = new ThumbnailSongNewAdapter(getContext(), mListSong, new IClickSongRecyclerViewListener() {
            @Override
            public void onClickItemSong(Song song) {
                onClickGoToMP3Player(song);
            }
        });

        mThumbnailSongAdapter_ListenAgain = new ThumbnailSongAdapter(getContext(), mListSong, new IClickSongRecyclerViewListener() {
            @Override
            public void onClickItemSong(Song song) {
                onClickGoToMP3Player(song);
            }
        });

        mThumbnailSongAdapter_Recommend = new ThumbnailSongAdapter(getContext(), mListSong, new IClickSongRecyclerViewListener() {
            @Override
            public void onClickItemSong(Song song) {
                onClickGoToMP3Player(song);
            }
        });

        mThumbnailGenreAdapter = new ThumbnailGenreAdapter(getContext(), mLisGenre, new IClickGenreRecyclerViewListener() {
            @Override
            public void onClickItemGenre(Genre genre) {
                // Handle genre click
            }
        });

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        if (mListSong.isEmpty()) {
            LogUtils.ApplicationLogE("Keo API Song");
            getListSong();
        } else {
            LogUtils.ApplicationLogE("ko Keo API Song");
            showSongInRecyclerView();
        }

        if (mListBanner.isEmpty()) {
            LogUtils.ApplicationLogE("Keo API Banner");
            getListBanner();
        } else {
            LogUtils.ApplicationLogE("ko Keo API Banner");
            showBannerInRecyclerView();
        }

        return view;
    }

    private void autoSlideImages() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if( mListBanner != null){
                            int currentItem = viewPager.getCurrentItem();
                            int totalItem = mListBanner.size() - 1;
                            if (currentItem < totalItem) {
                                currentItem++;
                                viewPager.setCurrentItem(currentItem);
                            } else {
                                viewPager.setCurrentItem(0);
                            }
                        }

                    }
                });
            }
        }, 500, 3000);
    }

    private List<Genre> geListGenre() {
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

    private void getListBanner() {
        ApiService.apiService.getBanners()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Banner>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<Banner> banners) {
                        mListBanner = banners;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api Banner error");
                    }

                    @Override
                    public void onComplete() {
                       showBannerInRecyclerView();
                    }
                });

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
                        LogUtils.ApplicationLogE("Call api error");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.ApplicationLogD("Call api success");
                        showSongInRecyclerView();
                    }
                });
    }

    private void getUserListenHistory(String userID) {
        ApiService.apiService.getUserListenHistory(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ListenHistory>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<ListenHistory> listenHistories) {
                        mListUserListenHistory = listenHistories;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api user history error");
                    }

                    @Override
                    public void onComplete() {
                        // Handle completion
                    }
                });
    }

    private void showSongInRecyclerView() {
        mLisGenre = geListGenre();
        listSongQuickPick = takeMusicWithHighView(numberSongShowInQuickPick, mListSong);
        listNewReleaseSong = takeNewReLeaseMusic(mListSong);

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
    private void showBannerInRecyclerView() {
        bannerAdapter = new BannerAdapter(getContext(), mListBanner);
        viewPager.setAdapter(bannerAdapter);
        circleIndicator.setViewPager(viewPager);
        bannerAdapter.registerAdapterDataObserver(circleIndicator.getAdapterDataObserver());

        autoSlideImages();
    }


    @Override
    public void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
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
        /*Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("SongObject", song);
        intent.putExtras(bundle);
        startActivity(intent);*/
        MediaItem mediaItem = MediaItem.fromUri(song.getSongURL());
        MediaItemHolder.getInstance().getMediaController().setMediaItem(mediaItem);
    }

    @UnstableApi
    @Override
    public void onStart() {
        super.onStart();

    }
}
