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
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.ViewPagerRoomAdapter;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.Message;
import com.example.authenticationuseraccount.utils.ChillCornerRoomManager;
import com.example.authenticationuseraccount.utils.SocketIoManager;
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
            //Update UI
            FragmentCorner fragmentCorner = new FragmentCorner();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragmentCorner);
            transaction.addToBackStack(null);
            transaction.commit();

            //Disconnect Room
            SocketIoManager.getInstance().disconnect();
            ChillCornerRoomManager.release();
        });

        return view;
    }


    public void onRoomJoined(Context context, String roomId) {
        if (viewPagerAdapter != null) {
            participantsFragment.onRoomJoined(context, roomId);
        } else {
            // Handle the case where the adapter is not initialized
            Toast.makeText(context, "Adapter not initialized", Toast.LENGTH_SHORT).show();
        }
    }

    public void onMessageReceived(Context context, Message message) {
        if (viewPagerAdapter != null) {
            conversationFragment.onMessageReceived(context, message);
        } else {
            // Handle the case where the adapter is not initialized
            Toast.makeText(context, "Adapter not initialized", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRoomCreate(Context context, String roomID) {
        LogUtils.ApplicationLogI("FragmentRoom | onRoomCreate");
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (viewPagerAdapter != null) {
                    participantsFragment.onRoomCreate(context, roomID);
                } else {
                    // Handle the case where the adapter is not initialized
                    LogUtils.ApplicationLogI("Fragment Room onRoomCreate: Adapter not initialized");
                    Toast.makeText(context, "Adapter not initialized", Toast.LENGTH_SHORT).show();
                }
            }
        }, 500); // 2000 milliseconds = 2 seconds
    }
}
