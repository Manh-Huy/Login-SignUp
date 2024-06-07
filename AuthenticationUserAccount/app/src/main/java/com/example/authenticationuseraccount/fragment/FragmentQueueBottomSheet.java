package com.example.authenticationuseraccount.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaMetadata;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.QueueSongAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongSmallAdapter;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.model.IClickSongRecyclerViewListener;
import com.example.authenticationuseraccount.model.business.Song;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentQueueBottomSheet extends BottomSheetDialogFragment {
    private MediaMetadata currentMedia;
    private List<Song> mListItems;

    public FragmentQueueBottomSheet(MediaMetadata currentMedia, List<Song> mListItems) {
        this.currentMedia = currentMedia;
        this.mListItems = mListItems;
    }

    public FragmentQueueBottomSheet() {
        this.mListItems = new ArrayList<>();
    }

    public void setListItems(List<Song> mediaItems) {
        this.mListItems = mediaItems;
    }

    public void addMediaItem(Song mediaItem) {
        this.mListItems.add(mediaItem);
    }

    public void setCurrentMediaItem(MediaMetadata mediaItem) {
        this.currentMedia = mediaItem;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_queue_bottom_sheet, null);

        ImageView imgCurrentSong = view.findViewById(R.id.currently_playing_image);
        byte[] art = currentMedia.artworkData;
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
        imgCurrentSong.setImageBitmap(bitmap);

        TextView tvCurrentSongName = view.findViewById(R.id.currently_playing_text);
        tvCurrentSongName.setText(currentMedia.title);
        tvCurrentSongName.setSelected(true);
        TextView tvCurrentSongArtist = view.findViewById(R.id.currently_playing_artist);
        tvCurrentSongArtist.setText(currentMedia.artist);
        tvCurrentSongArtist.setSelected(true);

        bottomSheetDialog.setContentView(view);

        RecyclerView rcvData = view.findViewById(R.id.recycler_view_queue);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvData.setLayoutManager(linearLayoutManager);

        QueueSongAdapter songSmallAdapter = new QueueSongAdapter(getContext(), mListItems, new IClickSongRecyclerViewListener() {
            @Override
            public void onClickItemSong(Song song) {
                 ErrorUtils.showError(getContext(),"Click!!!");
            }
        });
        rcvData.setAdapter(songSmallAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rcvData.addItemDecoration(itemDecoration);
        return bottomSheetDialog;
    }
}
