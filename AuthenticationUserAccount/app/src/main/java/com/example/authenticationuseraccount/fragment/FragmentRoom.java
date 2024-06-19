package com.example.authenticationuseraccount.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.ViewPagerRoomAdapter;
import com.google.android.material.tabs.TabLayout;

public class FragmentRoom extends Fragment {

    private View view;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Button outRoomButton;
    private ViewPagerRoomAdapter viewPagerAdapter;
    private ParticipantsFragment participantsFragment;
    private ConversationFragment conversationFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_room, container, false);

        // Initialize views
        outRoomButton = view.findViewById(R.id.out_room_button);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.view_pager);

        // Initialize the ViewPager adapter
        participantsFragment = new ParticipantsFragment();
        conversationFragment = new ConversationFragment();
        viewPagerAdapter = new ViewPagerRoomAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(participantsFragment, "Participants");
        viewPagerAdapter.addFragment(conversationFragment, "Conversation");
        mViewPager.setAdapter(viewPagerAdapter);

        // Setup the TabLayout with the ViewPager
        mTabLayout.setupWithViewPager(mViewPager);

        // Set click listener for out room button
        outRoomButton.setOnClickListener(v -> {
            FragmentCorner fragmentCorner = new FragmentCorner();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragmentCorner);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }


    public void onRoomJoined(Context context) {
        if (viewPagerAdapter != null) {
            participantsFragment.onRoomJoined(context);
        } else {
            // Handle the case where the adapter is not initialized
            Toast.makeText(context, "Adapter not initialized", Toast.LENGTH_SHORT).show();
        }
        /*new Handler(Looper.getMainLooper()).postDelayed(() -> {

        }, 2000); // 2000 milliseconds delay (2 seconds)*/
    }
}
