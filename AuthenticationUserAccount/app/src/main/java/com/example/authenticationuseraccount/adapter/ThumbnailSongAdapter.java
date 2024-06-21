package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.IClickSongRecyclerViewListener;
import com.example.authenticationuseraccount.model.business.Song;

import java.util.List;

public class ThumbnailSongAdapter extends RecyclerView.Adapter<ThumbnailSongAdapter.ThumbnailSongViewHolder> {
    private IClickSongRecyclerViewListener iClickSongRecyclerViewListener;
    private List<Song> mSongs;
    private Context mContext;

    public ThumbnailSongAdapter(Context mContext, List<Song> mSongs, IClickSongRecyclerViewListener listener) {
        this.mSongs = mSongs;
        this.mContext = mContext;
        this.iClickSongRecyclerViewListener = listener;
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

    public static class ThumbnailSongViewHolder extends RecyclerView.ViewHolder {
        private CardView layoutItem;
        private ImageView imgThumbnail;
        private TextView tvSongTitle, tvArtist;

        public ThumbnailSongViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            tvSongTitle = itemView.findViewById(R.id.tv_song_title);
            tvArtist = itemView.findViewById(R.id.tv_artist);
        }
    }
}
