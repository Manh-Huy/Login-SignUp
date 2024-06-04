package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;

import java.util.ArrayList;
import java.util.List;

public class ItemQueueAdapter extends RecyclerView.Adapter<ItemQueueAdapter.ItemQueueViewHolder> {
    private List<MediaItem> mListItems;

    public ItemQueueAdapter(List<MediaItem> mListItems) {
        this.mListItems = mListItems;
    }

    @NonNull
    @Override
    public ItemQueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue_song, parent, false);
        return new ItemQueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemQueueViewHolder holder, int position) {
        MediaItem mediaItem = mListItems.get(position);
        if (mediaItem == null) {
            return;
        }
        holder.tvNameSong.setText(mediaItem.mediaMetadata.title);
        holder.tvNameArtist.setText(mediaItem.mediaMetadata.artist);

        byte[] art = mediaItem.mediaMetadata.artworkData;
        Bitmap bitmap = null;
        if (art != null) {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
        }
        holder.imgSong.setImageBitmap(bitmap);

        holder.tvOverflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.itemQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Click: " + mediaItem.mediaMetadata.title, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListItems != null) {
            return mListItems.size();
        }
        return 0;
    }

    public class ItemQueueViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout itemQueue;
        private ImageView imgSong;
        private TextView tvNameSong, tvNameArtist, tvOverflowMenu;
        public ItemQueueViewHolder(@NonNull View itemView) {
            super(itemView);
            itemQueue = itemView.findViewById(R.id.item_queue);
            imgSong = itemView.findViewById(R.id.imageview_song);
            tvNameSong = itemView.findViewById(R.id.tv_nameSong);
            tvNameArtist = itemView.findViewById(R.id.tv_name_artist);
            tvOverflowMenu = itemView.findViewById(R.id.overflow_menu);
        }
    }
}
