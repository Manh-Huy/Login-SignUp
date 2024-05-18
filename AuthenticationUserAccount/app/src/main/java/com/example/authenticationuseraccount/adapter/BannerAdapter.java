package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.homepagemodel.Banner;


import java.util.List;

public class BannerAdapter extends PagerAdapter {
    private Context mContext;
    private List<Banner> mListBanner;

    public BannerAdapter(Context mContext, List<Banner> mListBanner) {
        this.mContext = mContext;
        this.mListBanner = mListBanner;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.banner_layout, container, false);

        ImageView imgBanner = view.findViewById(R.id.banner);
        Banner banner = mListBanner.get(position);
        if(banner != null) {
            Glide.with(mContext).load(banner.getResourceId()).into(imgBanner);
        }

        //Add view to viewgroup
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        if(mListBanner != null) {
            return  mListBanner.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
