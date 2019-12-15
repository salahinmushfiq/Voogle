package com.example.voogle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.voogle.Adapters.BusFragmentPagerAdapter;
import com.example.voogle.Fragments.BusFragment;
import com.example.voogle.Fragments.MapFragment;
import com.example.voogle.Fragments.TrainFragement;
import com.example.voogle.R;
import com.example.voogle.databinding.ActivityHomeBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding activityHomeBinding;
    String source ,destination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding= DataBindingUtil.setContentView(this,R.layout.activity_home);

        source= getIntent().getStringExtra("source");
        destination=getIntent().getStringExtra("destination");
        Bundle bundle=new Bundle();
        bundle.putString("source",source);
        bundle.putString("destination",destination);
        MapFragment mapFragment=new MapFragment();
        mapFragment.setArguments(bundle);

        // TODO : change if one needs pre-configured fragments
        BusFragmentPagerAdapter busFragmentPagerAdapter=new BusFragmentPagerAdapter(getSupportFragmentManager(), new BusFragment(), new TrainFragement(), mapFragment);
        activityHomeBinding.vehicleTypeVP.setAdapter(busFragmentPagerAdapter);
        activityHomeBinding.vehicleTypeTL.setupWithViewPager(activityHomeBinding.vehicleTypeVP);


        activityHomeBinding.vehicleTypeVP.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(activityHomeBinding.vehicleTypeTL));
        activityHomeBinding.vehicleTypeTL.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(activityHomeBinding.vehicleTypeVP));

        activityHomeBinding.vehicleTypeVP.requestDisallowInterceptTouchEvent(true);

        //TabLayout.Tab x;
        Objects.requireNonNull(activityHomeBinding.vehicleTypeTL.getTabAt(0)).setCustomView(R.layout.sample_tab);

        Objects.requireNonNull(activityHomeBinding.vehicleTypeTL.getTabAt(1)).setCustomView(R.layout.sample_tab_but_train);
        Objects.requireNonNull(activityHomeBinding.vehicleTypeTL.getTabAt(2)).setCustomView(R.layout.sample_tab_but_bus);
    }
}
