package com.example.voogle.Fragments;


import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.example.voogle.GlobalVariables;
import com.example.voogle.PojoClasses.*;
import com.example.voogle.R;
import com.example.voogle.databinding.FragmentFaresBinding;
import com.google.firebase.database.*;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfConversion;


/**
 * A simple {@link Fragment} subclass.
 */
public class FaresFragment extends Fragment {

    FragmentFaresBinding fragmentFaresBinding;
    Double pathaoBikeTotalCost, pathaoCarTotalCost, uberMotoTotalCost, uberXTotalCost;
    double distance, duration;
    double distanceInKm, durationInMinute;
    PathaoBikeFares pathaoBikeFairs = new PathaoBikeFares();
    Fares fares = new Fares();
    PathaoCarFares pathaoCarFares = new PathaoCarFares();
    UberMotoFares uberMotoFares = new UberMotoFares();
    UberxFares uberxFares = new UberxFares();
    DatabaseReference root = FirebaseDatabase.getInstance().getReference("root");
    StopNew startingStop,endingStop;
    private Location startingStopLocalLocation,endingStopLocalLocation;

    public FaresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentFaresBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fares, container, false);
        startingStop = new StopNew();
        endingStop = new StopNew();
        getRoute();


        return fragmentFaresBinding.getRoot();
    }

    private void getRoute() {

//                        distance = responseRoute.distance();
//                        duration = responseRoute.duration();

        startingStop.setName("Shishu Mela");
        startingStop.setLat(23.773018887636074);
        startingStop.setLng(90.36722380809236);



        endingStop.setName("Kolabagan");
        endingStop.setLat(23.747854936993697);
        endingStop.setLng(90.38027281299742);

        startingStopLocalLocation=new android.location.Location(startingStop.getName());
        startingStopLocalLocation.setLatitude(startingStop.getLat());
        startingStopLocalLocation.setLongitude(startingStop.getLng());

        endingStopLocalLocation=new Location(endingStop.getName());
        endingStopLocalLocation.setLatitude(endingStop.getLat());
        endingStopLocalLocation.setLongitude(endingStop.getLng());


        double distance= startingStopLocalLocation.distanceTo(endingStopLocalLocation)/10000;
                        distanceInKm = TurfConversion.convertLength(distance, TurfConstants.UNIT_METERS, TurfConstants.UNIT_KILOMETERS);
                        durationInMinute = duration / 60;
                        Log.d("check", String.valueOf(distanceInKm));
                        Log.d("check", String.valueOf(durationInMinute));

                        getFaresFromDB();
                }


    private void getFaresFromDB() {

        root.child("fares").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    fares = dataSnapshot.getValue(Fares.class);
                    GlobalVariables.fares = fares;
                    pathaoBikeTotalCost = (distanceInKm * Double.parseDouble(fares.getPathaobike().getCost_per_km())) + Double.parseDouble(fares.getPathaobike().getBase_fair());
                    Log.d("fares", pathaoBikeTotalCost.toString());

                    pathaoCarTotalCost = (distanceInKm * Double.parseDouble(fares.getPathaocar().getCost_per_km())) + Double.parseDouble(fares.getPathaocar().getBase_fair());
                    Log.d("fares", pathaoCarTotalCost.toString());

                    uberXTotalCost = Double.parseDouble(fares.getUberx().getBase_fair()) + (distanceInKm * Double.parseDouble(fares.getUberx().getCost_per_km())) + ((durationInMinute * Double.parseDouble(fares.getUberx().getCost_per_min())));
                    uberMotoTotalCost = Double.parseDouble(fares.getUberx().getBase_fair()) + (distanceInKm * Double.parseDouble(fares.getUbermoto().getCost_per_km())) + ((durationInMinute * Double.parseDouble(fares.getUbermoto().getCost_per_min())));
                    setFares(uberXTotalCost, uberMotoTotalCost, pathaoCarTotalCost, pathaoBikeTotalCost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setFares(Double uberXTotalCost, Double uberMotoTotalCost, Double pathaoCarTotalCost, Double pathaoBikeTotalCost) {
        fragmentFaresBinding.uberCarText.setText(Html.fromHtml(String.format("Car Fare: <b>%,.02f</b> <i>BDT</i>", uberXTotalCost)));
        fragmentFaresBinding.uberBikeText.setText(Html.fromHtml(String.format("Bike Fare: <b>%,.02f</b> <i>BDT</i>", uberMotoTotalCost)));
        fragmentFaresBinding.pathaoCarText.setText(Html.fromHtml(String.format("Car Fare: <b>%,.02f</b> <i>BDT</i>", pathaoCarTotalCost)));
        fragmentFaresBinding.pathaoBikeText.setText(Html.fromHtml(String.format("Bike Fare: <b>%,.02f</b> <i>BDT</i>", pathaoBikeTotalCost)));
    }

}
