package com.example.authenticationuseraccount.activity.panel;

import static com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout.COLLAPSED;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.WindowInsets;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.StateFragmentAdapter;
import com.example.authenticationuseraccount.fragment.FragmentCorner;
import com.example.authenticationuseraccount.fragment.FragmentHome;
import com.example.authenticationuseraccount.fragment.FragmentLibrary;
import com.example.authenticationuseraccount.fragment.FragmentProfile;
import com.realgear.multislidinguppanel.BasePanelView;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;
import com.realgear.readable_bottom_bar.ReadableBottomBar;

public class RootNavigationBarPanel extends BasePanelView {
    private ViewPager2 rootViewPager;
    private ReadableBottomBar rootNavigationBar;

    private FragmentHome fragmentHome;
    private FragmentCorner fragmentCorner;
    private FragmentLibrary fragmentLibrary;
    private FragmentProfile fragmentProfile;

    private Context mContext;
    public RootNavigationBarPanel(@NonNull Context context, MultiSlidingUpPanelLayout panelLayout) {
        super(context, panelLayout);
        mContext = context;
        getContext().setTheme(R.style.Theme_AuthenticationUserAccount);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_root_navigation_bar, this, true);
    }

    @Override
    public void onCreateView() {
        this.setPanelState(COLLAPSED);
        this.setSlideDirection(MultiSlidingUpPanelLayout.SLIDE_VERTICAL);
        this.setPeakHeight(getNavigationBarHeight());

        fragmentHome = new FragmentHome();
        fragmentCorner = new FragmentCorner();
        fragmentLibrary = new FragmentLibrary();
        fragmentProfile = new FragmentProfile();
    }

    @Override
    public void onBindView() {
        rootViewPager = getMultiSlidingUpPanel().findViewById(R.id.root_view_pager);
        rootNavigationBar = findViewById(R.id.root_navigation_bar);


        StateFragmentAdapter adapter = new StateFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        adapter.addFragment(fragmentHome);
        adapter.addFragment(fragmentCorner);
        adapter.addFragment(fragmentLibrary);
        adapter.addFragment(fragmentProfile);

        rootViewPager.setAdapter(adapter);
        rootNavigationBar.setupWithViewPager2(rootViewPager);

    }

    @Override
    public void onPanelStateChanged(int i) {

    }

    public void changeFragment(int index){
        rootViewPager.setCurrentItem(index);
        rootNavigationBar.setupWithViewPager2(rootViewPager);
    }

    public int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsets windowInsets = getRootWindowInsets();
            if (windowInsets != null) {
                return windowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom;
            }
        } else {
            int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return getResources().getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    public void onRoomCreate() {
        rootViewPager.setCurrentItem(0);
        rootNavigationBar.setupWithViewPager2(rootViewPager);
        rootViewPager.setCurrentItem(1);
        rootNavigationBar.setupWithViewPager2(rootViewPager);
    }

    public void onRoomJoined() {
        fragmentCorner.onRoomJoined(mContext);
    }
}

