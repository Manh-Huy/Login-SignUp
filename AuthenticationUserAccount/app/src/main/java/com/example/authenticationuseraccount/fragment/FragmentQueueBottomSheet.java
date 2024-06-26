package com.example.authenticationuseraccount.fragment;

import android.app.Dialog;
import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.QueueSongAdapter;
import com.example.authenticationuseraccount.adapter.ThumbnailSongSmallAdapter;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.model.IClickSongRecyclerViewListener;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentQueueBottomSheet extends BottomSheetDialogFragment implements IClickSongRecyclerViewListener {
    private MediaMetadata currentMedia;
    private List<Song> mListItems;

    private Context mContext;

    private QueueSongAdapter songSmallAdapter;

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

    public void notifyChanges() {
        songSmallAdapter.notifyDataSetChanged();
        //songSmallAdapter = new QueueSongAdapter(getContext(), mListItems, this);
        onClickItemSong(MediaItemHolder.getInstance().getListSongs().get(MediaItemHolder.getInstance().getMediaController().getCurrentMediaItemIndex()));
    }

    public void setCurrentMediaItem(MediaMetadata mediaItem) {
        this.currentMedia = mediaItem;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.mContext = getContext();
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_queue_bottom_sheet, null);
        imgCurrentSong = view.findViewById(R.id.currently_playing_image);
        tvCurrentSongName = view.findViewById(R.id.currently_playing_text);
        tvCurrentSongArtist = view.findViewById(R.id.currently_playing_artist);
        RecyclerView rcvData = view.findViewById(R.id.recycler_view_queue);

        byte[] art = currentMedia.artworkData;
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);

        //Current Playing
        imgCurrentSong.setImageBitmap(bitmap);
        tvCurrentSongName.setText(currentMedia.title);
        tvCurrentSongName.setSelected(true);
        tvCurrentSongArtist.setText(currentMedia.artist);
        tvCurrentSongArtist.setSelected(true);
        bottomSheetDialog.setContentView(view);

        //Current Queue Playlist
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvData.setLayoutManager(linearLayoutManager);

        songSmallAdapter = new QueueSongAdapter(getContext(), mListItems, this);
        rcvData.setAdapter(songSmallAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        rcvData.addItemDecoration(itemDecoration);


        return bottomSheetDialog;
    }

    private ImageView imgCurrentSong;
    private TextView tvCurrentSongName, tvCurrentSongArtist;

    @Override
    public void onClickItemSong(Song song) {

        if (song.getImageURL() != null) {
            Glide.with(mContext)
                    .load(song.getImageURL())
                    .into(imgCurrentSong);
        } else { //Local Song
            Glide.with(mContext)
                    .load(song.getImageData())
                    .into(imgCurrentSong);
        }

        tvCurrentSongName.setText(song.getName());
        tvCurrentSongArtist.setText(song.getArtist());
        tvCurrentSongName.setSelected(true);
        tvCurrentSongArtist.setSelected(true);
    }
}
