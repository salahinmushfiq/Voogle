package com.example.voogle.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.voogle.Fragments.BusFragment;
import com.example.voogle.Fragments.TrainFragement;

public class BusFragmentPagerAdapter extends FragmentPagerAdapter {
    public BusFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new BusFragment();
            case 1:
                return new TrainFragement();
                default:
                    return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }
}
