package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.PLaylistActivity;
import com.example.authenticationuseraccount.model.business.Playlist;

import java.util.ArrayList;
import java.util.List;

public class ThumbnailPlaylistAdapter extends RecyclerView.Adapter<ThumbnailPlaylistAdapter.ThumbnailSongViewHolder> {
    private List<Playlist> mPlaylists;
    private Context mContext;

    public ThumbnailPlaylistAdapter(List<Playlist> mPlaylists, Context mContext) {
        this.mPlaylists = mPlaylists;
        this.mContext = mContext;
    }

    public void setData(List<Playlist> list) {
        this.mPlaylists = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ThumbnailSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thumbnail_playlist, parent, false);
        return new ThumbnailSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailSongViewHolder holder, int position) {
        Playlist playlist = mPlaylists.get(position);
        if (playlist == null) return;

        holder.tvPlaylistTitle.setText(playlist.getPlaylistName());
        int songCount = playlist.getListSong().size();
        holder.tvSongCount.setText(songCount + " songs");

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PLaylistActivity.class);
                intent.putExtra("playlist", playlist);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mPlaylists != null) {
            return mPlaylists.size();
        }
        return 0;
    }

    public static class ThumbnailSongViewHolder extends RecyclerView.ViewHolder {
        private CardView layoutItem;
        private TextView tvPlaylistTitle, tvSongCount;

        public ThumbnailSongViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            tvPlaylistTitle = itemView.findViewById(R.id.tv_playlist_title);
            tvSongCount = itemView.findViewById(R.id.tv_song_count);
        }
    }
}
