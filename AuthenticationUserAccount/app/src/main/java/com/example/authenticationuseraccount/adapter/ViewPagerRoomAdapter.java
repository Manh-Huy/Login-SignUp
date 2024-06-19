package com.example.authenticationuseraccount.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.authenticationuseraccount.fragment.ConversationFragment;
import com.example.authenticationuseraccount.fragment.ParticipantsFragment;

import java.util.ArrayList;

public class ViewPagerRoomAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> arraytitle = new ArrayList<>();

    public ViewPagerRoomAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.arraytitle = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return arraytitle.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        arraytitle.add(title);
    }

}
