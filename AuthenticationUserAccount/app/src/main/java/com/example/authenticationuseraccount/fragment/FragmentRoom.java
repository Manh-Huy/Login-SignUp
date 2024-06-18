package com.example.authenticationuseraccount.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private EditText idInput;
    private Button copyButton, outRoomButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idInput = view.findViewById(R.id.id_input);
        copyButton = view.findViewById(R.id.copy_button);
        outRoomButton = view.findViewById(R.id.out_room_button);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.view_pager);

        ViewPagerRoomAdapter viewPagerAdapter = new ViewPagerRoomAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        copyButton.setOnClickListener(v -> {
            String textToCopy = idInput.getText().toString();
            if (!textToCopy.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Nothing to copy", Toast.LENGTH_SHORT).show();
            }
        });

        outRoomButton.setOnClickListener(v -> {
            FragmentCorner fragmentCorner = new FragmentCorner();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragmentCorner);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }
}
