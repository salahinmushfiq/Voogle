package com.example.voogle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.voogle.Adapters.BusFragmentPagerAdapter;
import com.example.voogle.R;
import com.example.voogle.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding activityHomeBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding= DataBindingUtil.setContentView(this,R.layout.activity_home);
        BusFragmentPagerAdapter busFragmentPagerAdapter=new BusFragmentPagerAdapter(getSupportFragmentManager());
        activityHomeBinding.vehicleTypeVP.setAdapter(busFragmentPagerAdapter);
        activityHomeBinding.vehicleTypeTL.setupWithViewPager(activityHomeBinding.vehicleTypeVP);

    }
}
