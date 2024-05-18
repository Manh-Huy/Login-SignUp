package com.example.authenticationuseraccount.activiy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.SearchSongAdapter;
import com.example.authenticationuseraccount.model.homepagemodel.SearchSong;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private RecyclerView searchRecycleView;
    private SearchView searchView;
    private ImageView micIcon;
    private TextView cancelTextView;
    private List<SearchSong> fullListSearchSong = new ArrayList<>();
    private List<SearchSong> listSearchSong = new ArrayList<>();
    private SearchSongAdapter searchSongAdapter;
    private SharedPreferences sharedPreferences;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userUidHistory =  user + "History";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sharedPreferences = getSharedPreferences("SearchHistory", Context.MODE_PRIVATE);;

        //searchRecycleView
        searchRecycleView = findViewById(R.id.searchRecyclerView);
        searchRecycleView.setLayoutManager(new LinearLayoutManager(this));

        fullListSearchSong.add(new SearchSong("Drunk text"));
        fullListSearchSong.add(new SearchSong("Stay"));
        fullListSearchSong.add(new SearchSong("Anh là ngoại lệ của em"));
        fullListSearchSong.add(new SearchSong("Buồn hay vui"));
        fullListSearchSong.add(new SearchSong("Chúng ta của hiện tại"));
        fullListSearchSong.add(new SearchSong("Thương Ly Biệt"));
        fullListSearchSong.add(new SearchSong("Lệ lưu ly"));
        fullListSearchSong.add(new SearchSong("Tường là"));

        searchSongAdapter = new SearchSongAdapter(getApplicationContext(), listSearchSong,false, new SearchSongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String query) {
                searchView.setQuery(query, false);
            }
        });
        searchRecycleView.setAdapter(searchSongAdapter);

        // show search history
        showSearchHistory();

        // search view
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

    private void filterList(String text) {
        List<SearchSong> filteredList = new ArrayList<>();
        if (!text.isEmpty()) {
            for (SearchSong item : fullListSearchSong) {
                if (item.getSong().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        searchSongAdapter.setFilteredList(filteredList, true);
    }

    private void saveSearchQuery(String query) {
        List<String> searchHistory = new ArrayList<>(sharedPreferences.getStringSet(userUidHistory, new HashSet<>()));
        searchHistory.add(query);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(userUidHistory, new HashSet<>(searchHistory));
        editor.apply();
    }

    private void showSearchHistory() {
        Set<String> searchHistorySet = sharedPreferences.getStringSet(userUidHistory, new HashSet<>());
        List<String> searchHistoryList = new ArrayList<>(searchHistorySet);
        Log.e(TAG, "showSearchHistory: " + searchHistoryList);
        List<SearchSong> historyList = new ArrayList<>();
        for (String query : searchHistoryList) {
            historyList.add(new SearchSong(query));
        }
        searchSongAdapter.setFilteredList(historyList,false);
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
}