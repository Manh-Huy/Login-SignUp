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
import com.example.authenticationuseraccount.model.business.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchedItemSongAdapter extends RecyclerView.Adapter<SearchedItemSongAdapter.SearchedItemSongViewHolder> {

    private Context mContext;
    private FragmentActivity fragmentActivity;
    private List<Song> listSong;

    public SearchedItemSongAdapter(Context context, FragmentActivity fragmentActivity, List<Song> song) {
        this.mContext = context;
        this.fragmentActivity = fragmentActivity;
        this.listSong = song;
    }

    @NonNull
    @Override
    public SearchedItemSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchedItemSongViewHolder(LayoutInflater.from(mContext).inflate(R.layout.searched_item_layout, parent, false));
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
                clickOpenSearchOptionBottomSheetFragment();
            }
        });
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
