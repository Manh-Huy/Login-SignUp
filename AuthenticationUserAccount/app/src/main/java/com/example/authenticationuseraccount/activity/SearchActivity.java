package com.example.authenticationuseraccount.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.SearchFilterAdapter;
import com.example.authenticationuseraccount.adapter.SearchSongAdapter;
import com.example.authenticationuseraccount.adapter.SearchedItemAdapter;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.SearchHistory;
import com.example.authenticationuseraccount.model.SearchResult;
import com.example.authenticationuseraccount.model.business.Album;
import com.example.authenticationuseraccount.model.business.Artist;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.utils.DataLocalManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private Disposable mDisposable;
    private List<Object> listItemsResult = new ArrayList<>();
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private RecyclerView searchRecyclerView, filterRecyclerView;
    private SearchView searchView;
    private ImageView micIcon;
    private TextView cancelTextView;
    private ProgressBar progressBar;
    private FrameLayout overlayLayout;
    private List<String> listNameAllInfoSong;
    private SearchSongAdapter searchSongAdapter;
    private SearchFilterAdapter searchFilterAdapter;
    private SearchedItemAdapter searchedItemSongAdapter;
    SearchHistory searchHistoryByUserId = new SearchHistory();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        progressBar = findViewById(R.id.progressBar);
        overlayLayout = findViewById(R.id.overlayLayout);

        //searchRecycleView
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchSongAdapter = new SearchSongAdapter(getApplicationContext(), new ArrayList<>(),false, new SearchSongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String query) {
                searchView.setQuery(query, false);
            }
        });
        searchRecyclerView.setAdapter(searchSongAdapter);

        // filterRecyclerView
        List<String> searchFilters = new ArrayList<>();
        searchFilters.add("All");
        searchFilters.add("Song");
        searchFilters.add("Artist");
        searchFilters.add("Album");

        filterRecyclerView = findViewById(R.id.filterRecyclerView);
        searchFilterAdapter = new SearchFilterAdapter(searchFilters);
        filterRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        filterRecyclerView.setAdapter(searchFilterAdapter);

        addSearchHistoryFromLocal();

        progressBar.setVisibility(View.VISIBLE);
        overlayLayout.setVisibility(View.VISIBLE);

        showSearchHistory();

        // Lấy các thông tin của ba hát
        Set<String> stringSet = new HashSet<>();
        stringSet = DataLocalManager.getNameAllInfoSong();
        listNameAllInfoSong = new ArrayList<>(stringSet);

        progressBar.setVisibility(View.GONE);
        overlayLayout.setVisibility(View.GONE);

        // Search View
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                saveSearchQuery(query);
                listItemsResult.clear();

                // Lấy vị trí của mục được chọn
                String selectedFilter = searchFilterAdapter.getSelectedFilter();
                switch(selectedFilter) {
                    case "All":
                        getSearchAllResult(query);
                        break;
                    case "Song":
                        getSearchSongResult(query);
                        break;
                    case "Artist":
                        getSearchArtistResult(query);
                        break;
                    case "Album":
                        getSearchAlbumResult(query);
                        break;
                    default:
                        break;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchRecyclerView.setAdapter(searchSongAdapter);
                if (newText.isEmpty()) {
                    showSearchHistory();
                } else {
                    filterList(newText);
                }
                return true;
            }
        });

        // speak
        micIcon = findViewById(R.id.micIcon);
        micIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        // back
        cancelTextView = findViewById(R.id.cancelTextView);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getSearchAllResult(String query) {
        progressBar.setVisibility(View.VISIBLE);
        overlayLayout.setVisibility(View.VISIBLE);
        ApiService.apiService.getSearchAllResult(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SearchResult>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<SearchResult> searchResults) {

                        for (SearchResult result : searchResults) {
                            if ("song".equals(result.getType())) {
                                Song song = new Gson().fromJson(new Gson().toJson(result.getData()), Song.class);
                                listItemsResult.add(song);
                            } else if ("artist".equals(result.getType())) {
                                Artist artist = new Gson().fromJson(new Gson().toJson(result.getData()), Artist.class);
                                listItemsResult.add(artist);
                            } else if ("album".equals(result.getType())) {
                                Album album = new Gson().fromJson(new Gson().toJson(result.getData()), Album.class);
                                listItemsResult.add(album);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api search all result error");
                    }

                    @Override
                    public void onComplete() {
                        showSearchResult(listItemsResult);

                        progressBar.setVisibility(View.GONE);
                        overlayLayout.setVisibility(View.GONE);
                    }
                });
    }
    private void getSearchSongResult(String query) {
        progressBar.setVisibility(View.VISIBLE);
        overlayLayout.setVisibility(View.VISIBLE);
        ApiService.apiService.getSearchSongResult(query, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<Song> songs) {
                        listItemsResult.addAll(songs);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api search song result error");
                    }

                    @Override
                    public void onComplete() {
                        showSearchResult(listItemsResult);

                        progressBar.setVisibility(View.GONE);
                        overlayLayout.setVisibility(View.GONE);
                    }
                });

    }
    private void getSearchArtistResult(String query) {
        progressBar.setVisibility(View.VISIBLE);
        overlayLayout.setVisibility(View.VISIBLE);
        ApiService.apiService.getSearchArtistResult(query, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Artist>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<Artist> artists) {
                        listItemsResult.addAll(artists);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api search artist result error");
                    }

                    @Override
                    public void onComplete() {
                        showSearchResult(listItemsResult);

                        progressBar.setVisibility(View.GONE);
                        overlayLayout.setVisibility(View.GONE);
                    }
                });
    }
    private void getSearchAlbumResult(String query) {
        progressBar.setVisibility(View.VISIBLE);
        overlayLayout.setVisibility(View.VISIBLE);
        ApiService.apiService.getSearchAlbumResult(query, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Album>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<Album> albums) {
                        listItemsResult.addAll(albums);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api search album result error");
                    }

                    @Override
                    public void onComplete() {
                        showSearchResult(listItemsResult);

                        progressBar.setVisibility(View.GONE);
                        overlayLayout.setVisibility(View.GONE);
                    }
                });
    }

    private void showSearchResult(List<Object> list) {
        FragmentActivity fragmentActivity = SearchActivity.this;
        searchedItemSongAdapter = new SearchedItemAdapter(getApplicationContext(), fragmentActivity, list);
        searchRecyclerView.setAdapter(searchedItemSongAdapter);
    }
    private void filterList(String text) {
        List<String> filteredList = new ArrayList<>();
        if (!text.isEmpty()) {
            for (String item : listNameAllInfoSong) {
                if (item != null && item.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        searchSongAdapter.setFilteredList(filteredList, true);
    }

    private void saveSearchQuery(String query) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
        {
            DataLocalManager.setHistorySearch(query);

        }
        else
        {
            String idUser = user.getUid();
            addSearchHistory(idUser, query);
        }
    }

    private void addSearchHistoryFromLocal() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
        {
           return;
        }

        Set<String> searchHistoryLocalList = DataLocalManager.getHistorySearch();
        String idUser = user.getUid();
        for (String query : searchHistoryLocalList) {
            addSearchHistory(idUser, query);
        }
    }

    @SuppressLint("CheckResult")
    private void addSearchHistory(String userID, String query) {
        ApiService.apiService.addSearchHistory(userID, query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    LogUtils.ApplicationLogD("Succefully");
                }, throwable -> {
                    LogUtils.ApplicationLogE("Failed");
                });
    }

    private void showSearchHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null)
        {
            Set<String> searchHistoryList = DataLocalManager.getHistorySearch();
            List<String> searchArrayList = new ArrayList<>(searchHistoryList);
            searchSongAdapter.setFilteredList(searchArrayList,false);
        }
        else
        {
            String idUser = user.getUid();
            getSearchHistoryById(idUser);
        }
    }

    private void getSearchHistoryById(String idUser) {
        ApiService.apiService.getSearchHistoryById(idUser).enqueue(new Callback<SearchHistory>() {
            @Override
            public void onResponse(Call<SearchHistory> call, Response<SearchHistory> response) {
                searchHistoryByUserId = response.body();
                if (searchHistoryByUserId != null) {
                    searchSongAdapter.setFilteredList(searchHistoryByUserId.getHistory(), false);
                } else {
                    LogUtils.ApplicationLogE("Search history is null");
                }
            }

            @Override
            public void onFailure(Call<SearchHistory> call, Throwable t) {
                LogUtils.ApplicationLogE("Call api getSearchHistoryById error");
            }
        });
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView.setQuery(result.get(0), false);
                }
                break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }
}