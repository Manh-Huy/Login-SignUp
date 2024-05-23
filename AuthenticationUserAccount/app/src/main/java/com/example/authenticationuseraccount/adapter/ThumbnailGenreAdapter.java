package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.Genre;

import java.util.List;

public class ThumbnailGenreAdapter extends RecyclerView.Adapter<ThumbnailGenreAdapter.ThumbnailGenreViewHolder> {

    private List<Genre> mGenres;
    private Context mContext;

    public ThumbnailGenreAdapter(Context mcontext, List<Genre> mGenres)
    {
        this.mContext = mcontext;
        this.mGenres = mGenres;
    }
    public void setData(List<Genre> list) {
        this.mGenres = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ThumbnailGenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thumbnail_genre, parent, false);
        return new ThumbnailGenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailGenreViewHolder holder, int position) {
        Genre genre = mGenres.get(position);
        if (genre == null)
        {
            return;
        }
        holder.tvGenreName.setText(genre.getName());
    }

    @Override
    public int getItemCount() {
        if (mGenres != null)
        {
            return mGenres.size();
        }
        return 0;
    }

    public static class ThumbnailGenreViewHolder extends RecyclerView.ViewHolder {

        private TextView tvGenreName;

        public ThumbnailGenreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGenreName = itemView.findViewById(R.id.tv_genre_name);

        }
    }
}
