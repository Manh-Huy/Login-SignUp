package com.example.authenticationuseraccount.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.homepagemodel.SuggestSong;

import java.util.List;

public class SuggestSongAdapter extends RecyclerView.Adapter<SuggestSongAdapter.SuggestSongViewHolder> {

    private List<SuggestSong> suggestSongList;

    public SuggestSongAdapter(List<SuggestSong> suggestSongList){
        this.suggestSongList = suggestSongList;
    }

    @NonNull
    @Override
    public SuggestSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggest_song_layout, parent, false);
        return new SuggestSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestSongViewHolder holder, int position) {
        holder.suggestImage.setImageResource(suggestSongList.get(position).getImage());
        holder.nameSong.setText(suggestSongList.get(position).getNameSong());
        holder.nameArtist.setText(suggestSongList.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return suggestSongList.size();
    }

    public class SuggestSongViewHolder extends RecyclerView.ViewHolder {

        private ImageView suggestImage;
        private TextView nameSong;
        private TextView nameArtist;
        public SuggestSongViewHolder(@NonNull View itemView) {

            super(itemView);

            suggestImage = itemView.findViewById(R.id.suggestImage);
            nameSong = itemView.findViewById(R.id.nameSong);
            nameArtist = itemView.findViewById(R.id.nameArtist);

        }
    }
}
