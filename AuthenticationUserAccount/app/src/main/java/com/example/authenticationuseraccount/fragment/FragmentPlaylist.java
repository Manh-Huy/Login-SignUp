package com.example.authenticationuseraccount.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.ThumbnailPlaylistAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongSmallAdapter;
import com.example.authenticationuseraccount.model.business.Playlist;

import java.util.ArrayList;
import java.util.List;

public class FragmentPlaylist extends Fragment {
    private TextView playlistCount;
    private RecyclerView rcvPlaylist;
    private LinearLayout layoutNoData;
    private List<Playlist> playList;
    private ThumbnailPlaylistAdapter thumbnailPlaylistAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playList = new ArrayList<>();

        playlistCount = view.findViewById(R.id.playlist_count);
        rcvPlaylist = view.findViewById(R.id.rcv_playlist);
        layoutNoData = view.findViewById(R.id.layout_no_data);

        UpdateUI();

        rcvPlaylist.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        thumbnailPlaylistAdapter = new ThumbnailPlaylistAdapter(playList, getContext());
        thumbnailPlaylistAdapter.setData(playList);
        rcvPlaylist.setAdapter(thumbnailPlaylistAdapter);
        return view;
    }

    private void UpdateUI() {
        playlistCount.setText(playList.size() + " playlists");

        if (playList.size() == 0) {
            layoutNoData.setVisibility(View.VISIBLE);
            rcvPlaylist.setVisibility(View.GONE);
        }
        else {
            layoutNoData.setVisibility(View.GONE);
            rcvPlaylist.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }




}
