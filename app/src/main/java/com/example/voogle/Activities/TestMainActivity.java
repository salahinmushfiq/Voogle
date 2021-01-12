package com.example.voogle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.voogle.Adapters.BusFragmentPagerAdapter;
import com.example.voogle.Fragments.FaresFragment;
import com.example.voogle.Fragments.MapFragment;
import com.example.voogle.Fragments.TestMapsFragment;
import com.example.voogle.PojoClasses.StopNew;
import com.example.voogle.R;
import com.example.voogle.components.Custompager;
import com.example.voogle.databinding.ActivityTestMainBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class TestMainActivity extends AppCompatActivity {

    ActivityTestMainBinding activityTestMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTestMainBinding=DataBindingUtil.setContentView(this,R.layout.activity_test_main);

        // TODO : change if one needs pre-configured fragments
        MapFragment mapFragment = new MapFragment();
        TestMapsFragment testMapsFragment = new TestMapsFragment();

        BusFragmentPagerAdapter busFragmentPagerAdapter = new BusFragmentPagerAdapter(getSupportFragmentManager(), new FaresFragment(), testMapsFragment);
        Custompager custompager = activityTestMainBinding.vehicleTypeVP;
        custompager.setAdapter(busFragmentPagerAdapter);
        activityTestMainBinding.vehicleTypeTL.setupWithViewPager(custompager);


//        activityHomeBinding.vehicleTypeVP.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(activityHomeBinding.vehicleTypeTL));
        activityTestMainBinding.vehicleTypeTL.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(custompager));

//        activityHomeBinding.vehicleTypeVP.requestDisallowInterceptTouchEvent(true);
        StopNew stopNew =new StopNew();
        stopNew.setUp(1);
        //TabLayout.Tab x;
//        Objects.requireNonNull(activityTestMainBinding.vehicleTypeTL.getTabAt(0)).setCustomView(R.layout.sample_tab);

        Objects.requireNonNull(activityTestMainBinding.vehicleTypeTL.getTabAt(0)).setCustomView(R.layout.sample_tab_but_fares);
        Objects.requireNonNull(activityTestMainBinding.vehicleTypeTL.getTabAt(1)).setCustomView(R.layout.sample_tab_but_bus);

    }
}