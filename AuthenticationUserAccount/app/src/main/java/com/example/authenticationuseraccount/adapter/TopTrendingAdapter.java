package com.example.authenticationuseraccount.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.homepagemodel.TopTrending;

import java.util.List;

public class TopTrendingAdapter extends RecyclerView.Adapter<TopTrendingAdapter.TopTrendingViewHolder> {

    private List<TopTrending> topTrendingList;

    public TopTrendingAdapter(List<TopTrending> topTrendingList){
        this.topTrendingList = topTrendingList;
    }

    @NonNull
    @Override
    public TopTrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_trending_layout, parent, false);
        return new TopTrendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopTrendingViewHolder holder, int position) {
        holder.topTrendingImage.setImageResource(topTrendingList.get(position).getImage());
        holder.nameSong.setText(topTrendingList.get(position).getNameSong());
        holder.nameArtist.setText(topTrendingList.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return topTrendingList.size();
    }

    public class TopTrendingViewHolder extends RecyclerView.ViewHolder{

        private ImageView topTrendingImage;
        private TextView nameSong;
        private TextView nameArtist;

        public TopTrendingViewHolder(@NonNull View itemView) {
            super(itemView);
            topTrendingImage = itemView.findViewById(R.id.toptrendingImage);
            nameSong = itemView.findViewById(R.id.nameSongTopTrending);
            nameArtist = itemView.findViewById(R.id.nameArtistTopTrending);
        }
    }
}
