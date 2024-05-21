package com.example.authenticationuseraccount.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.Song;

import java.util.List;

public class ThumbnailSongSmallAdapter extends RecyclerView.Adapter<ThumbnailSongSmallAdapter.ThumbnailSongSmallViewHolder> {

    private List<Song> mSongs;

    public ThumbnailSongSmallAdapter(List<Song> listSuggestSong) {
        this.mSongs = listSuggestSong;
    }

    public void setData(List<Song> list) {
        this.mSongs = list;
        notifyDataSetChanged();
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

        holder.imgThumbnail.setImageResource(song.getImage());
        holder.tvSongTitle.setText(song.getNameSong());
        holder.tvArtist.setText(song.getArtist());
    }

    @Override
    public int getItemCount() {
        if (mSongs != null) {
            return mSongs.size();
        }
        return 0;
    }

    public static class ThumbnailSongSmallViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgThumbnail;
        private TextView tvSongTitle, tvArtist;

        public ThumbnailSongSmallViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            tvSongTitle = itemView.findViewById(R.id.tv_song_title);
            tvArtist = itemView.findViewById(R.id.tv_artist);
        }
    }
}
