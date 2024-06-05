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
import androidx.fragment.app.FragmentManager;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.ItemQueueAdapter;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentQueueBottomSheet extends BottomSheetDialogFragment {
    private MediaItem currentMedia;
    private List<MediaItem> mListItems;

    public FragmentQueueBottomSheet(MediaItem currentMedia, List<MediaItem> mListItems) {
        this.currentMedia = currentMedia;
        this.mListItems = mListItems;
    }

    public FragmentQueueBottomSheet() {
        this.mListItems = new ArrayList<>();
    }

    public void setListItems(List<MediaItem> mediaItems) {
        this.mListItems = mediaItems;
    }

    public void addMediaItem(MediaItem mediaItem) {
        this.mListItems.add(mediaItem);
    }

    public void setCurrentMediaItem(MediaItem mediaItem) {
        this.currentMedia = currentMedia;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_queue_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        ImageView imgCurrentSong = view.findViewById(R.id.currently_playing_image);
        //byte[] art = currentMedia.mediaMetadata.artworkData;
        byte[] art = MediaItemHolder.getInstance().getMediaController().getMediaMetadata().artworkData;
        Bitmap bitmap = null;
        if (art != null) {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
        }
        imgCurrentSong.setImageBitmap(bitmap);

        TextView tvCurrentSongName = view.findViewById(R.id.currently_playing_text);
        tvCurrentSongName.setText(MediaItemHolder.getInstance().getMediaController().getMediaMetadata().title);

        TextView tvCurrentSongArtist = view.findViewById(R.id.currently_playing_artist);
        tvCurrentSongArtist.setText(MediaItemHolder.getInstance().getMediaController().getMediaMetadata().artist);

        RecyclerView rcvData = view.findViewById(R.id.recycler_view_queue);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvData.setLayoutManager(linearLayoutManager);

        ItemQueueAdapter itemQueueAdapter = new ItemQueueAdapter(mListItems);
        rcvData.setAdapter(itemQueueAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rcvData.addItemDecoration(itemDecoration);
        return bottomSheetDialog;
    }
}
