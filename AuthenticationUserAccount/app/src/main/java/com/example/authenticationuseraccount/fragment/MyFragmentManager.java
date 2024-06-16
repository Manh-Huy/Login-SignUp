package com.example.authenticationuseraccount.fragment;

import androidx.fragment.app.Fragment;

public class MyFragmentManager {
    private static MyFragmentManager instance;
    private FragmentHome fragmentHome;
    private FragmentCorner fragmentCorner;
    private FragmentLibrary fragmentLibrary;
    private FragmentProfile fragmentProfile;

    private MyFragmentManager() {
        fragmentHome = new FragmentHome();
        fragmentCorner = new FragmentCorner();
        fragmentLibrary = new FragmentLibrary();
        fragmentProfile = new FragmentProfile();
    }

    public static synchronized MyFragmentManager getInstance() {
        if (instance == null) {
            instance = new MyFragmentManager();
        }
        return instance;
    }

    public static void setInstance(MyFragmentManager instance) {
        MyFragmentManager.instance = instance;
    }

    public FragmentHome getFragmentHome() {
        return fragmentHome;
    }

    public void setFragmentHome(FragmentHome fragmentHome) {
        this.fragmentHome = fragmentHome;
    }

    public FragmentCorner getFragmentCorner() {
        return fragmentCorner;
    }

    public void setFragmentCorner(FragmentCorner fragmentCorner) {
        this.fragmentCorner = fragmentCorner;
    }

    public FragmentLibrary getFragmentLibrary() {
        return fragmentLibrary;
    }

    public void setFragmentLibrary(FragmentLibrary fragmentLibrary) {
        this.fragmentLibrary = fragmentLibrary;
    }

    public FragmentProfile getFragmentProfile() {
        return fragmentProfile;
    }

    public void setFragmentProfile(FragmentProfile fragmentProfile) {
        this.fragmentProfile = fragmentProfile;
    }
}
