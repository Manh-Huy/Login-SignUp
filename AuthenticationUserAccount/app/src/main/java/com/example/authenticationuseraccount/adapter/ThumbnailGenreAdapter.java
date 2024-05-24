package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.Genre;
import com.example.authenticationuseraccount.model.IClickSongRecyclerViewListener;
import com.example.authenticationuseraccount.model.IClickGenreRecyclerViewListener;


import java.util.List;

public class ThumbnailGenreAdapter extends RecyclerView.Adapter<ThumbnailGenreAdapter.ThumbnailGenreViewHolder> {
    private IClickGenreRecyclerViewListener iClickGenreRecyclerViewListener;
    private List<Genre> mGenres;
    private Context mContext;

    public ThumbnailGenreAdapter(Context mcontext, List<Genre> mGenres, IClickGenreRecyclerViewListener listener)
    {
        this.mContext = mcontext;
        this.mGenres = mGenres;
        this.iClickGenreRecyclerViewListener = listener;
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

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickGenreRecyclerViewListener.onClickItemGenre(genre);
            }
        });
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
        private LinearLayout layoutItem;
        private TextView tvGenreName;

        public ThumbnailGenreViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            tvGenreName = itemView.findViewById(R.id.tv_genre_name);

        }
    }
}
