package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.IClickSongRecyclerViewListener;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.Wave;

import java.util.List;

public class QueueSongAdapter extends RecyclerView.Adapter<QueueSongAdapter.ThumbnailSongSmallViewHolder> {

    private List<Song> mSongs;
    private Context mContext;
    private IClickSongRecyclerViewListener iClickSongRecyclerViewListener;


    public void setData(List<Song> list) {
        this.mSongs = list;
        notifyDataSetChanged();
    }

    public QueueSongAdapter(Context mContext, List<Song> mListSong, IClickSongRecyclerViewListener listener) {
        this.mSongs = mListSong;
        this.mContext = mContext;
        this.iClickSongRecyclerViewListener = listener;
    }

    @NonNull
    @Override
    public ThumbnailSongSmallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LogUtils.ApplicationLogE("onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue_song, parent, false);
        return new QueueSongAdapter.ThumbnailSongSmallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailSongSmallViewHolder holder, int position) {
        LogUtils.ApplicationLogE("onBindViewHolder");
        Song song = mSongs.get(position);
        if (song == null) return;

        LogUtils.ApplicationLogE("Holder Index: " + position);
        LogUtils.ApplicationLogE("Media Index: " + MediaItemHolder.getInstance().getMediaController().getCurrentMediaItemIndex());
        if (position == MediaItemHolder.getInstance().getMediaController().getCurrentMediaItemIndex()) {
            LogUtils.ApplicationLogE("Same Index => Start Animation");
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.imgThumbnail.setVisibility(View.INVISIBLE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
            Glide.with(mContext)
                    .load(song.getImageURL())
                    .into(holder.imgThumbnail);
        }
        holder.tvSongTitle.setText(song.getName());
        holder.tvArtist.setText(song.getArtist());
        holder.tvSongTitle.setSelected(true);
        holder.tvArtist.setSelected(true);

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
        private ProgressBar progressBar;

        public ThumbnailSongSmallViewHolder(@NonNull View itemView) {
            super(itemView);
            LogUtils.ApplicationLogE("ThumbnailSongSmallViewHolder");
            layoutItem = itemView.findViewById(R.id.layout_item);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            tvSongTitle = itemView.findViewById(R.id.tv_song_title);
            tvArtist = itemView.findViewById(R.id.tv_artist);
            progressBar = itemView.findViewById(R.id.rcv_progress_bar);
            progressBar.setIndeterminateDrawable(new ChasingDots());
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
