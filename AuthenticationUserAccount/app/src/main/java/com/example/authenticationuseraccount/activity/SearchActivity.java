package com.example.authenticationuseraccount.activity;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.SearchSongAdapter;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.utils.DataLocalManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {
    private Disposable mDisposable;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private RecyclerView searchRecycleView;
    private SearchView searchView;
    private ImageView micIcon;
    private TextView cancelTextView;
    private ProgressBar progressBar;
    private FrameLayout overlayLayout;
    private List<Song> mListSong = new ArrayList<>();
    private SearchSongAdapter searchSongAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        progressBar = findViewById(R.id.progressBar);
        overlayLayout = findViewById(R.id.overlayLayout);

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

        DataLocalManager.mergeLocalWithAccountHistorySearch();

        showSearchHistory();

        getSong();

        // Search View
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                saveSearchQuery(query);
                filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
    private void getSong() {
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
            for (Song song : mListSong) {
                String songName = song.getName();
                if (songName != null && songName.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(songName);
                }
            }
        }
        searchSongAdapter.setFilteredList(filteredList, true);
    }

    private void saveSearchQuery(String query) {
        DataLocalManager.setHistorySearch(query);
    }

    private void showSearchHistory() {
        Set<String> searchHistoryList = DataLocalManager.getHistorySearch();
        List<String> searchArrayList = new ArrayList<>(searchHistoryList);
        searchSongAdapter.setFilteredList(searchArrayList,false);
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