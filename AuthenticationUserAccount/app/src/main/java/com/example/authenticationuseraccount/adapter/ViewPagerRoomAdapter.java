package com.example.authenticationuseraccount.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.authenticationuseraccount.fragment.ConversationFragment;
import com.example.authenticationuseraccount.fragment.ParticipantsFragment;

public class ViewPagerRoomAdapter extends FragmentStatePagerAdapter {

    private ParticipantsFragment participantsFragment;
    private ConversationFragment conversationFragment;
    public ViewPagerRoomAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        participantsFragment = new ParticipantsFragment();
        conversationFragment = new ConversationFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return participantsFragment;
            case 1:
                return conversationFragment;
            default:
                return participantsFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Participants";
                break;
            case 1:
                title = "Conversation";
                break;
        }
        return title;
    }

    public void onRoomJoined(Context context){
        participantsFragment.onRoomJoined(context);
    }
}
