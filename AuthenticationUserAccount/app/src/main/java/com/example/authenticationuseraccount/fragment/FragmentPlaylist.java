package com.example.authenticationuseraccount.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.ThumbnailPlaylistAdapter;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.Playlist;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentPlaylist extends Fragment {
    FirebaseUser user;
    private TextView playlistCount;
    private ImageView imgAdd;
    private RecyclerView rcvPlaylist;
    private LinearLayout layoutNoData;
    private List<Playlist> playList;
    private ThumbnailPlaylistAdapter thumbnailPlaylistAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();

        playlistCount = view.findViewById(R.id.playlist_count);
        imgAdd = view.findViewById(R.id.img_add);
        rcvPlaylist = view.findViewById(R.id.rcv_playlist);
        layoutNoData = view.findViewById(R.id.layout_no_data);

        updateUI();

        rcvPlaylist.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        thumbnailPlaylistAdapter = new ThumbnailPlaylistAdapter(playList, getContext());

        if (user == null) {
            updateUI();
        }
        else {
            getPlaylistUserByID(user.getUid());
        }

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    Toast.makeText(getContext(), "Login to add playlist", Toast.LENGTH_SHORT).show();
                }
                else {
                    FragmentAddPlaylistBottomSheet fragmentPlaylistOptionBottomSheet = new FragmentAddPlaylistBottomSheet(user.getUid(), user.getDisplayName());
                    fragmentPlaylistOptionBottomSheet.show(requireActivity().getSupportFragmentManager(), fragmentPlaylistOptionBottomSheet.getTag());
                }

            }
        });
        return view;
    }

    private void updateUI() {
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

    private void getPlaylistUserByID(String userID) {
        ApiService.apiService.getPLayListByID(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Playlist>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Playlist> playlists) {
                        playList.addAll(playlists);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api get playlist error");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.ApplicationLogE("Call api get playlist thanh cong");
                        updateUI();
                        thumbnailPlaylistAdapter.setData(playList);
                        rcvPlaylist.setAdapter(thumbnailPlaylistAdapter);
                    }
                });
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }




}
