package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.homepagemodel.Banner;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private Context mContext;
    private List<Banner> mListBanner;
    public BannerAdapter(Context mContext, List<Banner> mListBanner) {
        this.mContext = mContext;
        this.mListBanner = mListBanner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Banner banner = mListBanner.get(position);
        if(banner != null) {
            Glide.with(mContext)
                    .load(banner.getImageURL())
                    .into(holder.imgBanner);
        }

        holder.cardViewBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = banner.getLink();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Intent chooser = Intent.createChooser(intent, "Open with");
                try {
                    mContext.startActivity(chooser);
                } catch (Exception e) {
                    Toast.makeText(mContext, "Cannot open link", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return !mListBanner.isEmpty() ? mListBanner.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgBanner;
        private CardView cardViewBanner;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.banner);
            cardViewBanner = itemView.findViewById(R.id.cardView);
        }
    }
}
