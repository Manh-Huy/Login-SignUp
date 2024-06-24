package com.example.authenticationuseraccount.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.ItemPlaylistOptionAdapter;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.Playlist;
import com.example.authenticationuseraccount.model.business.Song;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentPlaylistOptionBottomSheet extends BottomSheetDialogFragment {
    private List<Playlist> mListItems;
    private String userID;
    private Song selectedSong;

    public FragmentPlaylistOptionBottomSheet(List<Playlist> mListItems, String userID, Song selectedSong) {
        this.mListItems = mListItems;
        this.userID = userID;
        this.selectedSong = selectedSong;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_playlist_option_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        RecyclerView rcvSearchOption =view.findViewById(R.id.rcv_search_option);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvSearchOption.setLayoutManager(linearLayoutManager);

        ItemPlaylistOptionAdapter itemPlaylistOptionAdapter = new ItemPlaylistOptionAdapter(mListItems);

        rcvSearchOption.setAdapter(itemPlaylistOptionAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rcvSearchOption.addItemDecoration(itemDecoration);

        Button btnAddToPlaylist = view.findViewById(R.id.btn_add_to_playlist);
        btnAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Playlist selectedPlaylist = itemPlaylistOptionAdapter.getSelectedPlaylist();
                if (selectedPlaylist == null) {
                    Toast.makeText(getContext(), "No items selected", Toast.LENGTH_SHORT).show();
                } else {
                    addSongToPlaylist(userID, selectedPlaylist.getPlaylistName(), selectedSong.getSongID());
                    Toast.makeText(getContext(), "Selected item: " + selectedPlaylist.getPlaylistName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return bottomSheetDialog;
    }

    @SuppressLint("CheckResult")
    private void addSongToPlaylist(String userID, String playlistName, String songID) {
        ApiService.apiService.addSongToPlaylist(userID, playlistName, songID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    LogUtils.ApplicationLogD("Call API Add Song to Playlist Successfully");
                    dismiss();
                }, throwable -> {
                    LogUtils.ApplicationLogE("Call API Add Song to Playlist Failed");
                });
    }
}
