package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.business.Song;

import java.util.List;

public class SearchedItemSongAdapter extends RecyclerView.Adapter<SearchedItemSongAdapter.SearchedItemSongViewHolder> {

    Context mContext;
    List<Song> listSong;

    public SearchedItemSongAdapter(Context context, List<Song> song) {
        this.mContext = context;
        this.listSong = song;
    }

    @NonNull
    @Override
    public SearchedItemSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchedItemSongAdapter.SearchedItemSongViewHolder(LayoutInflater.from(mContext).inflate(R.layout.searched_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedItemSongViewHolder holder, int position) {
        Song song = listSong.get(position);
        if (song == null) return;
        holder.songName.setText(song.getName());
        holder.artistName.setText(song.getArtist());
        holder.albumName.setText(song.getAlbum());
        Glide.with(mContext)
                .load(song.getImageURL())
                .into(holder.songImage);

        holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Hiển thị menu tùy chọn
                    PopupMenu popupMenu = new PopupMenu(mContext, holder.overflowMenu);
                    popupMenu.inflate(R.menu.option_search_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int itemId = item.getItemId();
                            if (itemId == R.id.mnu_item_like) {
                                Toast.makeText(mContext, "Đã thích", Toast.LENGTH_LONG).show();
                            } else if (itemId == R.id.mnu_item_download) {
                                Toast.makeText(mContext, "Đã tải xuống", Toast.LENGTH_LONG).show();
                            } else if (itemId == R.id.mnu_item_add_to_playlist) {
                                Toast.makeText(mContext, "Đã thêm vào danh sách phát", Toast.LENGTH_LONG).show();
                            } else if (itemId == R.id.mnu_item_play_next) {
                                Toast.makeText(mContext, "Sẽ phát tiếp theo", Toast.LENGTH_LONG).show();
                            } else if (itemId == R.id.mnu_item_add_to_queue) {
                                Toast.makeText(mContext, "Đã thêm vào hàng đợi", Toast.LENGTH_LONG).show();
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (listSong != null) {
            return listSong.size();
        }
        return 0;
    }

    public class SearchedItemSongViewHolder extends RecyclerView.ViewHolder {
        private TextView songName, artistName, albumName, overflowMenu;
        private ImageView songImage;

        public SearchedItemSongViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.tv_nameSong);
            artistName = itemView.findViewById(R.id.tv_name_artist);
            albumName = itemView.findViewById(R.id.tv_album_name);
            songImage = itemView.findViewById(R.id.imageview_song);
            overflowMenu = itemView.findViewById(R.id.overflow_menu);
        }
    }
}
