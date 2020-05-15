package com.example.voogle.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.voogle.Fragments.BusFragment;
import com.example.voogle.Fragments.MapFragment;
import com.example.voogle.Fragments.FaresFragment;

public class BusFragmentPagerAdapter extends FragmentPagerAdapter {

    private BusFragment busFragment;
    private FaresFragment faresFragment;
    private MapFragment mapFragment;


    /**
     * <p>Custom {@link androidx.viewpager.widget.ViewPager} Adapter<br>
     * Extends {@link FragmentPagerAdapter}<br>
     * Used in our project to switch main menu fragments</p>
     *
     * @param fm            The {@link FragmentManager} to be used
     * @param busFragment   The First Fragment to be used
     * @param faresFragment The Second Fragment to be used
     * @param mapFragment   The Third Fragment to be used
     */
    public BusFragmentPagerAdapter(FragmentManager fm, BusFragment busFragment, FaresFragment faresFragment, MapFragment mapFragment) {
        super(fm);
        this.busFragment = busFragment;
        this.faresFragment = faresFragment;
        this.mapFragment = mapFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // TODO: Was this a typo? there were two TrainFragments
                return busFragment;
            case 1:
                return faresFragment;
            case 2:
                return mapFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public BusFragment getBusFragment() {
        return busFragment;
    }

    public FaresFragment getFaresFragment() {
        return faresFragment;
    }

    public MapFragment getMapFragment() {
        return mapFragment;
    }
}
