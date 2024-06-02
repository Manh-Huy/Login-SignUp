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
import com.example.authenticationuseraccount.adapter.SearchSongAdapter;
import com.example.authenticationuseraccount.adapter.SearchedItemSongAdapter;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.SearchHistory;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.model.business.User;
import com.example.authenticationuseraccount.utils.DataLocalManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private Disposable mDisposable; // để tạm test
    private List<Song> mListSong; // để tạm test
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private RecyclerView searchRecycleView;
    private SearchView searchView;
    private ImageView micIcon;
    private TextView cancelTextView;
    private ProgressBar progressBar;
    private FrameLayout overlayLayout;
    private List<String> listNameAllInfoSong;
    private SearchSongAdapter searchSongAdapter;
    private SearchedItemSongAdapter searchedItemSongAdapter;
    SearchHistory searchHistoryByUserId = new SearchHistory();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        progressBar = findViewById(R.id.progressBar);
        overlayLayout = findViewById(R.id.overlayLayout);

        getSong(); // để tạm test

        //searchRecycleView
        searchRecycleView = findViewById(R.id.searchRecyclerView);
        searchRecycleView.setLayoutManager(new LinearLayoutManager(this));
        searchSongAdapter = new SearchSongAdapter(getApplicationContext(), new ArrayList<>(),false, new SearchSongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String query) {
                searchView.setQuery(query, false);
            }
        });
        searchRecycleView.setAdapter(searchSongAdapter);

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
                //filterList(query);
                List<Song> resultSongs = new ArrayList<>();
                for (Song song : mListSong) {
                    if (song.getName().toLowerCase().contains(query.toLowerCase())) {
                        resultSongs.add(song);
                    }
                }
                if (resultSongs.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "No result return", Toast.LENGTH_SHORT).show();
                }
                else {
                    FragmentActivity fragmentActivity = SearchActivity.this;
                    searchedItemSongAdapter = new SearchedItemSongAdapter(getApplicationContext(), fragmentActivity, resultSongs);
                    searchRecycleView.setAdapter(searchedItemSongAdapter);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchRecycleView.setAdapter(searchSongAdapter);
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
    private void getSong() { // để tạm test
        progressBar.setVisibility(View.VISIBLE);
        overlayLayout.setVisibility(View.VISIBLE);
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
                        progressBar.setVisibility(View.GONE);
                        overlayLayout.setVisibility(View.GONE);
                    }
                });
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
    protected void onDestroy() { // để tạm test
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }
}