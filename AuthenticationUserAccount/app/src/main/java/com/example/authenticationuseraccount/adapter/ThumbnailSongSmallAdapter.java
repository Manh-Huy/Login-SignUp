package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.IClickSongRecyclerViewListener;
import com.example.authenticationuseraccount.model.business.Song;

import java.util.List;

public class ThumbnailSongSmallAdapter extends RecyclerView.Adapter<ThumbnailSongSmallAdapter.ThumbnailSongSmallViewHolder> {

    private List<Song> mSongs;
    private Context mContext;
    private IClickSongRecyclerViewListener iClickSongRecyclerViewListener;


    public void setData(List<Song> list) {
        this.mSongs = list;
        notifyDataSetChanged();
    }

    public ThumbnailSongSmallAdapter(Context mContext, List<Song> mListSong, IClickSongRecyclerViewListener listener) {
        this.mSongs = mListSong;
        this.mContext = mContext;
        this.iClickSongRecyclerViewListener = listener;
    }

    @NonNull
    @Override
    public ThumbnailSongSmallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thumbnail_song_small, parent, false);
        return new ThumbnailSongSmallAdapter.ThumbnailSongSmallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailSongSmallViewHolder holder, int position) {
        Song song = mSongs.get(position);
        if (song == null) return;

        holder.tvSongTitle.setText(song.getName());
        holder.tvArtist.setText(song.getArtist());
        holder.tvSongTitle.setSelected(true);
        holder.tvArtist.setSelected(true);
        Glide.with(mContext)
                .load(song.getImageURL())
                .into(holder.imgThumbnail);

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickSongRecyclerViewListener.onClickItemSong(song);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mSongs != null) {
            return mSongs.size();
        }
        return 0;
    }

    public static class ThumbnailSongSmallViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layoutItem;
        private ImageView imgThumbnail;
        private TextView tvSongTitle, tvArtist;

        public ThumbnailSongSmallViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            tvSongTitle = itemView.findViewById(R.id.tv_song_title);
            tvArtist = itemView.findViewById(R.id.tv_artist);
        }
    }
}
