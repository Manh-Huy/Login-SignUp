package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.Album;
import com.example.authenticationuseraccount.model.business.Artist;
import com.example.authenticationuseraccount.model.business.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchedItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SONG = 1;
    private static final int VIEW_TYPE_ARTIST = 2;
    private static final int VIEW_TYPE_ALBUM = 3;
    private Context mContext;
    private FragmentActivity fragmentActivity;
    private List<Object> listItems;

    public SearchedItemAdapter(Context context, FragmentActivity fragmentActivity, List<Object> song) {
        this.mContext = context;
        this.fragmentActivity = fragmentActivity;
        this.listItems = song;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_SONG:
                View songView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_searched_song_result, parent, false);
                return new SearchedSongViewHolder(songView);
            case VIEW_TYPE_ARTIST:
                View artistView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_searched_artist_result, parent, false);
                return new SearchedArtistViewHolder(artistView);
            case VIEW_TYPE_ALBUM:
                View albumView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_searched_album_result, parent, false);
                return new SearchedAlbumViewHolder(albumView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = listItems.get(position);
        if (item == null) {
            return;
        }
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SONG:
                Song song = (Song) item;
                SearchedSongViewHolder searchedSongViewHold =(SearchedSongViewHolder) holder;
                searchedSongViewHold.tvSongName.setText(song.getName());
                searchedSongViewHold.tvArtistName.setText(song.getArtist());
                searchedSongViewHold.tvAlbumName.setText(song.getAlbum());
                Glide.with(mContext)
                        .load(song.getImageURL())
                        .into(searchedSongViewHold.imgSong);

                searchedSongViewHold.tvOverflowMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickOpenSearchOptionBottomSheetFragment();
                    }
                });
                break;

            case VIEW_TYPE_ARTIST:
                Artist artist = (Artist) item;
                SearchedArtistViewHolder searchedArtistViewHolder = (SearchedArtistViewHolder) holder;
                searchedArtistViewHolder.tvArtistName.setText((artist.getName()));
                Glide.with(mContext)
                        .load(artist.getImageURL())
                        .into(searchedArtistViewHolder.imgArtist);
                break;
            case VIEW_TYPE_ALBUM:
                Album album = (Album) item;
                SearchedAlbumViewHolder searchedAlbumViewHolder = (SearchedAlbumViewHolder) holder;
                searchedAlbumViewHolder.tvAlbumName.setText(album.getName());
                Glide.with(mContext)
                        .load(album.getImageURL())
                        .into(searchedAlbumViewHolder.imgAlbum);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    private void clickOpenSearchOptionBottomSheetFragment() {
        List<ItemSearchOption> itemSearchOptionList = new ArrayList<>();
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_heart, "Thích"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_download, "Tải xuống"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_add_to_playlist, "Thêm vào danh sách phát"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play_next, "Phát tiếp theo"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_add_to_queue, "Thêm vào hàng đợi"));

        FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                Toast.makeText(mContext, itemSearchOption.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        fragmentSearchOptionBottomSheet.show(fragmentActivity.getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }

    @Override
    public int getItemCount() {
        if (listItems != null) {
            return listItems.size();
        }
        return 0;
    }
    @Override
    public int getItemViewType(int position) {
        Object item = listItems.get(position);
        if (item instanceof Song) {
            return VIEW_TYPE_SONG;
        } else if (item instanceof Artist) {
            return VIEW_TYPE_ARTIST;
        } else if (item instanceof Album) {
            return VIEW_TYPE_ALBUM;
        }
        return -1;
    }

    public class SearchedSongViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSongName, tvArtistName, tvAlbumName, tvOverflowMenu;
        private ImageView imgSong;

        public SearchedSongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongName = itemView.findViewById(R.id.tv_nameSong);
            tvArtistName = itemView.findViewById(R.id.tv_name_artist);
            tvAlbumName = itemView.findViewById(R.id.tv_album_name);
            imgSong = itemView.findViewById(R.id.imageview_song);
            tvOverflowMenu = itemView.findViewById(R.id.overflow_menu);
        }
    }
    public class SearchedArtistViewHolder extends RecyclerView.ViewHolder {
        private TextView tvArtistName;
        private ImageView imgArtist;

        public SearchedArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            imgArtist = itemView.findViewById(R.id.img_artist);
        }
    }
    public class SearchedAlbumViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAlbumName;
        private ImageView imgAlbum;

        public SearchedAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlbumName = itemView.findViewById(R.id.tv_album_name);
            imgAlbum = itemView.findViewById(R.id.img_album);
        }
    }

}
