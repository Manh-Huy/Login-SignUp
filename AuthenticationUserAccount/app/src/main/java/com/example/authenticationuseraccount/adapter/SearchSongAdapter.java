package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.homepagemodel.SearchSong;

import java.util.List;

public class SearchSongAdapter extends RecyclerView.Adapter<SearchSongAdapter.SearchSongViewHolder> {

    private Boolean isSearching = false;
    public interface OnItemClickListener {
        void onItemClick(String query);
    }

    Context context;
    List<SearchSong> items;
    private OnItemClickListener listener;

    public SearchSongAdapter(Context context, List<SearchSong> items, boolean isSearching,  OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.isSearching = isSearching;
    }

    public void setFilteredList(List<SearchSong> filteredList, boolean isSeaching)
    {
        this.items = filteredList;
        this.isSearching = isSeaching;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchSongViewHolder(LayoutInflater.from(context).inflate(R.layout.search_song_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchSongViewHolder holder, int position) {

        if(!isSearching){
            holder.btnHistory.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }else{
            holder.btnHistory.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.nameSong.setText(items.get(position).getSong());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(items.get(adapterPosition).getSong());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SearchSongViewHolder extends RecyclerView.ViewHolder {
        private TextView nameSong;
        private ImageButton btnHistory, btnDelete;

        public SearchSongViewHolder(@NonNull View itemView) {
            super(itemView);
            nameSong = itemView.findViewById(R.id.songTextView);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnHistory = itemView.findViewById(R.id.btn_history);
        }
    }
}
