package com.example.authenticationuseraccount.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.example.authenticationuseraccount.model.ListenHistory;
import com.example.authenticationuseraccount.model.business.Playlist;
import com.example.authenticationuseraccount.model.business.Song;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentAddPlaylistBottomSheet extends BottomSheetDialogFragment {

    private String userID;
    private String username;

    public FragmentAddPlaylistBottomSheet(String userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_add_playlist_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        TextView tvAddPlaylist = view.findViewById(R.id.playlist_name_input);
        Button btnAddPlaylist = view.findViewById(R.id.add_playlist_button);

        btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvAddPlaylist.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), "Please make name playlist", Toast.LENGTH_SHORT).show();
                }
                else {
                    List<Song> listSong = new ArrayList<>();
                    Playlist playlist = new Playlist(listSong, tvAddPlaylist.getText().toString(), userID, username);
                    addPlaylist(playlist);
                }
            }
        });

        return bottomSheetDialog;
    }

    @SuppressLint("CheckResult")
    private void addPlaylist(Playlist playlist) {

        ApiService.apiService.addPlaylist(playlist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    LogUtils.ApplicationLogD("API Add Playlist successfully: name: " + playlist.getPlaylistName());
                    dismiss();
                }, throwable -> {
                    LogUtils.ApplicationLogE("API Add Playlist Failed");
                });
    }
}
