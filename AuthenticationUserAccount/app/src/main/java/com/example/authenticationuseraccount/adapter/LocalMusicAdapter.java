package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.model.business.LocalSong;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder> {

    private List<LocalSong> musicList;
    private Context mContext;

    public LocalMusicAdapter(Context context, List<LocalSong> musicList) {
        this.mContext = context;
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searched_song_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalSong song = musicList.get(position);
        holder.tvSongName.setText(song.getTitle());
        holder.tvArtistName.setText(song.getArtistName());
        holder.tvAlbumName.setText(song.getAlbumName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorUtils.showError(mContext, "Clicked");

                Song songtest = new Song();
                MediaItemHolder.getInstance().getListSongs().add(songtest);

                Uri songUri = Uri.parse(song.getData());
                MediaItem mediaItem = MediaItem.fromUri(songUri);
                MediaItemHolder.getInstance().getMediaController().setMediaItem(mediaItem);
            }
        });
        /*Glide.with(mContext)
                .load(song.getImageURL())
                .into(holder.imgSong);
        holder.tvOverflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenOptionBottomSheetFragment(song);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        if (musicList != null) {
            return musicList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSongName, tvArtistName, tvAlbumName, tvOverflowMenu;
        private ImageView imgSong;
        private RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_container);
            tvSongName = itemView.findViewById(R.id.tv_nameSong);
            tvArtistName = itemView.findViewById(R.id.tv_name_artist);
            tvAlbumName = itemView.findViewById(R.id.tv_album_name);
            imgSong = itemView.findViewById(R.id.imageview_song);
            tvOverflowMenu = itemView.findViewById(R.id.overflow_menu);
        }
    }
}

