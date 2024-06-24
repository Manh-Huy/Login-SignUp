package com.example.authenticationuseraccount.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.business.Playlist;

import java.util.List;

public class ItemPlaylistOptionAdapter extends RecyclerView.Adapter<ItemPlaylistOptionAdapter.ItemPlaylistOptionViewHolder> {
    private List<Playlist> mListItems;
    private int selectedPosition = -1;

    public ItemPlaylistOptionAdapter(List<Playlist> mListItems) {
        this.mListItems = mListItems;
    }

    @NonNull
    @Override
    public ItemPlaylistOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name_playlist, parent, false);
        return new ItemPlaylistOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemPlaylistOptionViewHolder holder, int position) {
        Playlist playlist = mListItems.get(position);
        if (playlist == null) {
            return;
        }
        holder.cbPlaylist.setText(playlist.getPlaylistName());
        holder.cbPlaylist.setOnCheckedChangeListener(null);
        holder.cbPlaylist.setChecked(position == selectedPosition);

        holder.cbPlaylist.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            } else {
                if (selectedPosition == holder.getAdapterPosition()) {
                    selectedPosition = -1;
                }
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

    public Playlist getSelectedPlaylist() {
        if (selectedPosition != -1) {
            return mListItems.get(selectedPosition);
        }
        return null;
    }

    public class ItemPlaylistOptionViewHolder extends RecyclerView.ViewHolder {
    private CheckBox cbPlaylist;
    private LinearLayout layoutItem;
    public ItemPlaylistOptionViewHolder(@NonNull View itemView) {
        super(itemView);
        cbPlaylist = itemView.findViewById(R.id.checkbox_item);
        layoutItem = itemView.findViewById(R.id.layout_item);
    }
}

}
