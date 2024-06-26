package com.example.authenticationuseraccount.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.SplashScreenActivity;
import com.example.authenticationuseraccount.adapter.LocalMusicAdapter;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.LocalSong;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.utils.LocalMusicLoader;

import java.util.ArrayList;
import java.util.List;


public class FragmentLibrary extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private Context mContext;
    private List<LocalSong> musicList;
    private LocalMusicAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private  FragmentActivity fragmentActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        mContext = getContext();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        musicList = MediaItemHolder.getInstance().getListLocalSong();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        fragmentActivity = (FragmentActivity) getActivity();

        adapter = new LocalMusicAdapter(mContext,fragmentActivity, musicList);
        recyclerView.addItemDecoration(new DividerItemDecoration(fragmentActivity,DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onRefresh() {
        LogUtils.ApplicationLogI("musicListbe4: " + musicList.size());
        adapter.setData(LocalMusicLoader.getInstance().loadMusic(fragmentActivity));
        LogUtils.ApplicationLogI("musicListAfter: " + musicList.size());
        swipeRefreshLayout.setRefreshing(false);
    }


}
