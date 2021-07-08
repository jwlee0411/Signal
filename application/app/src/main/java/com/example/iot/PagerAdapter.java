package com.example.iot;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new MainFragment();
            case 1: return new Main2Fragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
