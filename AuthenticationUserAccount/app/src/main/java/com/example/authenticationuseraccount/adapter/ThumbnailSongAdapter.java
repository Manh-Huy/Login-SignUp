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

public class ThumbnailSongAdapter extends RecyclerView.Adapter<ThumbnailSongAdapter.ThumbnailSongViewHolder> {

    private List<Song> mSongs;

    public ThumbnailSongAdapter(List<Song> listSong) {
        this.mSongs = listSong;
    }

    public void setData(List<Song> list) {
        this.mSongs = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ThumbnailSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thumbnail_song, parent, false);
        return new ThumbnailSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailSongViewHolder holder, int position) {
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

    public static class ThumbnailSongViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgThumbnail;
        private TextView tvSongTitle, tvArtist;

        public ThumbnailSongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            tvSongTitle = itemView.findViewById(R.id.tv_song_title);
            tvArtist = itemView.findViewById(R.id.tv_artist);
        }
    }
}
