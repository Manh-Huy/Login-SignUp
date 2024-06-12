package com.example.authenticationuseraccount.fragment;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.AlbumActivity;
import com.example.authenticationuseraccount.activity.PremiumActivity;
import com.example.authenticationuseraccount.activity.SearchActivity;
import com.example.authenticationuseraccount.adapter.BannerAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailGenreAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongNewAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongSmallAdapter;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.Genre;
import com.example.authenticationuseraccount.model.IClickGenreRecyclerViewListener;
import com.example.authenticationuseraccount.model.IClickSongRecyclerViewListener;
import com.example.authenticationuseraccount.model.ListenHistory;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.model.homepagemodel.Banner;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.utils.ChillCornerRoomManager;
import com.example.authenticationuseraccount.utils.SocketIoManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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
    private RecyclerView rcvQuickPick, rcvListenAgain, rcvRecommend, rcvNewRelease, rcvGenre, rcvForgottenFavorite;
    private ImageView searchImageView, imgMenuIcon;
    private TextView tvListenAgain, tvRecommend, tvForgottenFavorite;
    private Disposable mDisposable;
    private ThumbnailSongSmallAdapter mThumbnailSongSmallAdapter_QuickPick;
    private ThumbnailSongSmallAdapter mThumbnailSongSmallAdapter_ForgottenFavorite;
    private ThumbnailGenreAdapter mThumbnailGenreAdapter;
    private ThumbnailSongAdapter mThumbnailSongAdapter_ListenAgain;
    private ThumbnailSongAdapter mThumbnailSongAdapter_Recommend;
    private ThumbnailSongNewAdapter mThumbnailSongNewAdapter_NewRelease;
    private List<Banner> mListBanner = new ArrayList<>();
    private List<Genre> mListGenre = new ArrayList<>();
    private List<Song> mListSong = new ArrayList<>();
    private List<Song> listNewReleaseSong;
    private final int numberSongShowInQuickPick = 10;
    private List<Song> listSongRecent = new ArrayList<>();
    private List<Song> listSongRecommend = new ArrayList<>();
    private List<Song> listSongQuickPick = new ArrayList<>();
    private List<Song> listForgottenFavorite = new ArrayList<>();

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
        tvListenAgain = view.findViewById(R.id.tv_listen_again);
        tvRecommend = view.findViewById(R.id.tv_recommend);
        tvForgottenFavorite = view.findViewById(R.id.tv_forgottenFavorite);

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
        rcvForgottenFavorite = view.findViewById(R.id.rcv_forgotten_favorite);
        rcvForgottenFavorite.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

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

        mThumbnailGenreAdapter = new ThumbnailGenreAdapter(getContext(), mListGenre, new IClickGenreRecyclerViewListener() {
            @Override
            public void onClickItemGenre(Genre genre) {
                // Handle genre click
                Intent intent = new Intent(getContext(), AlbumActivity.class);
                intent.putExtra("genre_key", genre);
                startActivity(intent);
            }
        });

        mThumbnailSongSmallAdapter_ForgottenFavorite = new ThumbnailSongSmallAdapter(getContext(), mListSong, new IClickSongRecyclerViewListener() {
            @Override
            public void onClickItemSong(Song song) {
                onClickGoToMP3Player(song);
            }
        });

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        if (user == null) {
            ShowUIForLocal();
        }
        else {
            getListSong();
        }

        if (mListBanner.isEmpty()) {
            LogUtils.ApplicationLogD("Keo API Banner");
            getListBanner();
        } else {
            LogUtils.ApplicationLogI("ko Keo API Banner");
            showBannerInRecyclerView();
        }

        if (mListGenre.isEmpty()) {
            LogUtils.ApplicationLogD("Keo API Genre");
            getListGenre();
        } else {
            LogUtils.ApplicationLogI("ko Keo API Genre");
            showGenreInRecyclerView();
        }

        imgMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getContext(), PremiumActivity.class);
                //getContext().startActivity(intent);
            }
        });

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

    @Override
    public void onResume() {
        super.onResume();
        mThumbnailSongSmallAdapter_QuickPick.notifyDataSetChanged();
    }

    private void ShowUIForLocal() {
        rcvListenAgain.setVisibility(GONE);
        rcvRecommend.setVisibility(GONE);
        rcvForgottenFavorite.setVisibility(GONE);

        tvListenAgain.setVisibility(GONE);
        tvRecommend.setVisibility(GONE);
        tvForgottenFavorite.setVisibility(GONE);

        if (mListSong.isEmpty()) {
            LogUtils.ApplicationLogE("Keo API Song");
            getListSong();
        } else {
            LogUtils.ApplicationLogE("ko Keo API Song");
            showSongLocalInRecyclerView();
        }
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
    private void getListGenre() {
        ApiService.apiService.getAllGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Genre>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Genre> genres) {
                        mListGenre = genres;
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api Genre error");
                    }

                    @Override
                    public void onComplete() {
                        showGenreInRecyclerView();
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
                        LogUtils.ApplicationLogE("Call api song error");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.ApplicationLogI("Call api song success");

                        if (user == null)
                        {
                            showSongLocalInRecyclerView();
                        }
                        else {
                            getUserRecentAndRecommendSong(user.getUid());
                        }
                    }
                });
    }

    private void getUserRecentAndRecommendSong(String userID) {
        ApiService.apiService.getUserRecentSong(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Song> songs) {
                        listSongRecent = songs;
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api recent (listen again) error");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.ApplicationLogI("Call api recent (listen again) complete");

                        if(listSongRecent.size() == 0) {
                            ShowUIForLocal();
                        }
                        else {
                            getUserRecommendSong(user.getUid());

                        }
                    }
                });
    }

    private void getUserRecommendSong(String userID) {
        ApiService.apiService.getUserRecommendSong(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Song> songs) {
                        listSongRecommend = songs;
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api recommend error");
                    }

                    @Override
                    public void onComplete() {
                        createQuickPickRandomSongsList();
                        getUserForgottenSong(user.getUid());
                    }
                });
    }

    private void getUserForgottenSong(String userID) {
        ApiService.apiService.getUserForgottenSong(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Song> songs) {
                        listForgottenFavorite = songs;
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api forgotten error");
                    }

                    @Override
                    public void onComplete() {
                        showSongInRecyclerView();
                    }
                });
    }

    private void createQuickPickRandomSongsList() {
        int numberSongRecent = 3;
        int numberSongRecommend = 3;

        if (listSongRecent.size() < 3) {
            numberSongRecent = listSongRecent.size();
        }
        if (listSongRecommend.size() < 3) {
            numberSongRecommend = listSongRecommend.size();
        }
        List<Song> recentSongsSubset = getRandomSubset(listSongRecent, numberSongRecent);
        List<Song> recommendSongsSubset = getRandomSubset(listSongRecommend, numberSongRecommend);

        listSongQuickPick.clear();
        listSongQuickPick.addAll(recentSongsSubset);
        listSongQuickPick.addAll(recommendSongsSubset);

        Set<String> songIDs = new HashSet<>();
        Iterator<Song> iterator = listSongQuickPick.iterator();

        while (iterator.hasNext()) {
            Song song = iterator.next();
            if (!songIDs.add(song.getSongID())) {
                // Nếu songID đã tồn tại trong set, xóa bài hát
                iterator.remove();
            }
        }
    }

    private List<Song> getRandomSubset(List<Song> sourceList, int count) {
        if (sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        Collections.shuffle(sourceList);
        return sourceList.subList(0, Math.min(count, sourceList.size()));
    }

    private void showSongInRecyclerView() {
        listNewReleaseSong = takeNewReLeaseMusic(mListSong);

        LogUtils.ApplicationLogE("Recent Count: " + listSongRecent.size());
        LogUtils.ApplicationLogE("Recommend Count: "+ listSongRecommend.size());
        LogUtils.ApplicationLogE("Forgotten Count: "+ listForgottenFavorite.size());

        mThumbnailSongSmallAdapter_QuickPick.setData(listSongQuickPick);
        mThumbnailSongNewAdapter_NewRelease.setData(listNewReleaseSong);
        mThumbnailGenreAdapter.setData(mListGenre);
        mThumbnailSongAdapter_ListenAgain.setData(listSongRecent);
        mThumbnailSongAdapter_Recommend.setData(listSongRecommend);
        mThumbnailSongSmallAdapter_ForgottenFavorite.setData(listForgottenFavorite);

        rcvQuickPick.setAdapter(mThumbnailSongSmallAdapter_QuickPick);
        rcvNewRelease.setAdapter(mThumbnailSongNewAdapter_NewRelease);
        rcvGenre.setAdapter(mThumbnailGenreAdapter);
        rcvListenAgain.setAdapter(mThumbnailSongAdapter_ListenAgain);
        rcvRecommend.setAdapter(mThumbnailSongAdapter_Recommend);
        rcvForgottenFavorite.setAdapter(mThumbnailSongSmallAdapter_ForgottenFavorite);
    }

    private void showSongLocalInRecyclerView() {
        listSongQuickPick = takeMusicWithHighView(numberSongShowInQuickPick, mListSong);
        listNewReleaseSong = takeNewReLeaseMusic(mListSong);

        LogUtils.ApplicationLogE("Quick pick local Count: " + listSongQuickPick.size());
        LogUtils.ApplicationLogE("New release Count: "+ listNewReleaseSong.size());

        mThumbnailSongSmallAdapter_QuickPick.setData(listSongQuickPick);
        mThumbnailSongNewAdapter_NewRelease.setData(listNewReleaseSong);

        rcvQuickPick.setAdapter(mThumbnailSongSmallAdapter_QuickPick);
        rcvNewRelease.setAdapter(mThumbnailSongNewAdapter_NewRelease);
    }
    private void showBannerInRecyclerView() {
        bannerAdapter = new BannerAdapter(getContext(), mListBanner);
        viewPager.setAdapter(bannerAdapter);
        circleIndicator.setViewPager(viewPager);
        bannerAdapter.registerAdapterDataObserver(circleIndicator.getAdapterDataObserver());

        autoSlideImages();
    }

    private void showGenreInRecyclerView() {
        mThumbnailGenreAdapter.setData(mListGenre);
        rcvGenre.setAdapter(mThumbnailGenreAdapter);
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
        //No Room
        if(ChillCornerRoomManager.getInstance().getCurrentUserId() == null){
            MediaItemHolder.getInstance().setMediaItem(song);
        }else{
            //Host Room
            if(ChillCornerRoomManager.getInstance().isCurrentUserHost()){
                String userID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().onAddSong(userID, song);
            }else{
                //Guest Room
                ErrorUtils.showError(getContext(),"Please Upgrade To Premium To Control Room Media!");
            }
        }

    }

    @UnstableApi
    @Override
    public void onStart() {
        super.onStart();

    }
}
