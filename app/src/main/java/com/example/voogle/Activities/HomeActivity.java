package com.example.voogle.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.example.voogle.Adapters.BusFragmentPagerAdapter;
import com.example.voogle.Fragments.BusFragment;
import com.example.voogle.Fragments.MapFragment;
import com.example.voogle.Fragments.FairsFragment;
import com.example.voogle.GlobalVariables;
import com.example.voogle.PojoClasses.PathaoBikeFairs;
import com.example.voogle.PojoClasses.PathaoCarFairs;
import com.example.voogle.PojoClasses.UberMotoFairs;
import com.example.voogle.PojoClasses.UberxFairs;
import com.example.voogle.R;
import com.example.voogle.components.Custompager;
import com.example.voogle.databinding.ActivityHomeBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.*;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfConversion;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding activityHomeBinding;
    String source;
    String destination;
    double sourceLat;
    double sourceLng;
    double destinationLat;
    double destinationLng;
    String sourceS_no;
    String destinationS_no;
    Double pathaoBikeTotalCost, pathaoCarTotalCost, uberMotoTotalCost, uberXTotalCost;
    double distance, duration;
    double distanceInKm, durationInMinute;
    PathaoBikeFairs pathaoBikeFairs = new PathaoBikeFairs();
    PathaoCarFairs pathaoCarFairs = new PathaoCarFairs();
    UberMotoFairs uberMotoFairs = new UberMotoFairs();
    UberxFairs uberxFairs = new UberxFairs();
    DatabaseReference root = FirebaseDatabase.getInstance().getReference("root");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        MapFragment mapFragment = new MapFragment();
       // getRoute();


//        source = getIntent().getStringExtra("source");
//        destination = getIntent().getStringExtra("destination");
//        sourceLat = getIntent().getDoubleExtra("sourceLat", 23);
//        sourceS_no = getIntent().getStringExtra("sourceS_no");
//
//        destinationLat = getIntent().getDoubleExtra("destinationLat", 23);
//        destinationS_no = getIntent().getStringExtra("destinationS_no");
//        sourceLng = getIntent().getDoubleExtra("sourceLng", 90);
//
//
//        destinationLng = getIntent().getDoubleExtra("destinationLng", 90);
//        sourceS_no = getIntent().getStringExtra("sourceS_no");
//        Bundle bundle = new Bundle();
//        bundle.putString("source", source);
//        bundle.putString("destination", destination);
//        bundle.putDouble("sourceLat", sourceLat);
//        bundle.putDouble("destinationLat", destinationLat);
//        bundle.putDouble("sourceLng", sourceLng);
//        bundle.putDouble("destinationLng", destinationLng);

        // Toast.makeText(this, "Source" + source, Toast.LENGTH_SHORT).show();
        //  Toast.makeText(this, "Source Sl No.: "+sourceS_no, Toast.LENGTH_SHORT).show();
        //  Toast.makeText(this, "Source Route No.: "+ (sourceRoute.toString()), Toast.LENGTH_SHORT).show();

        //  Toast.makeText(this, "Source Lat: "+sourceLat, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Source Lng: "+sourceLng, Toast.LENGTH_SHORT).show();
        //  Toast.makeText(this, "Destination: " + destination, Toast.LENGTH_SHORT).show();
        //  Toast.makeText(this, "Destination Sl No.:: " + destinationS_no, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Destination Route No.: "+ Arrays.toString(destinationRoute.toArray()), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Destination Lat: "+destinationLat, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Destination Lng: "+destinationLng, Toast.LENGTH_SHORT).show();


//        mapFragment.setArguments(bundle);

        // TODO : change if one needs pre-configured fragments
        BusFragmentPagerAdapter busFragmentPagerAdapter = new BusFragmentPagerAdapter(getSupportFragmentManager(), new BusFragment(), new FairsFragment(), mapFragment);
        Custompager custompager = activityHomeBinding.vehicleTypeVP;
        custompager.setAdapter(busFragmentPagerAdapter);
        activityHomeBinding.vehicleTypeTL.setupWithViewPager(custompager);


//        activityHomeBinding.vehicleTypeVP.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(activityHomeBinding.vehicleTypeTL));
        activityHomeBinding.vehicleTypeTL.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(custompager));

//        activityHomeBinding.vehicleTypeVP.requestDisallowInterceptTouchEvent(true);

        //TabLayout.Tab x;
        Objects.requireNonNull(activityHomeBinding.vehicleTypeTL.getTabAt(0)).setCustomView(R.layout.sample_tab);

        Objects.requireNonNull(activityHomeBinding.vehicleTypeTL.getTabAt(1)).setCustomView(R.layout.sample_tab_but_fairs);
        Objects.requireNonNull(activityHomeBinding.vehicleTypeTL.getTabAt(2)).setCustomView(R.layout.sample_tab_but_bus);


    }

}