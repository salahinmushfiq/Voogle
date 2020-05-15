package com.example.voogle.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.voogle.Activities.HomeActivity;
import com.example.voogle.GlobalVariables;
import com.example.voogle.PojoClasses.Fairs;
import com.example.voogle.PojoClasses.PathaoBikeFairs;
import com.example.voogle.PojoClasses.PathaoCarFairs;
import com.example.voogle.PojoClasses.UberMotoFairs;
import com.example.voogle.PojoClasses.UberxFairs;
import com.example.voogle.R;
import com.example.voogle.databinding.FragmentFairsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class FairsFragment extends Fragment {

    FragmentFairsBinding fragmentFairsBinding;
    Double pathaoBikeTotalCost, pathaoCarTotalCost, uberMotoTotalCost, uberXTotalCost;
    double distance, duration;
    double distanceInKm, durationInMinute;
    PathaoBikeFairs pathaoBikeFairs = new PathaoBikeFairs();
    Fairs fairs = new Fairs();
    PathaoCarFairs pathaoCarFairs = new PathaoCarFairs();
    UberMotoFairs uberMotoFairs = new UberMotoFairs();
    UberxFairs uberxFairs = new UberxFairs();
    DatabaseReference root = FirebaseDatabase.getInstance().getReference("root");

    public FairsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentFairsBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_fairs, container, false);
        getRoute();


        return fragmentFairsBinding.getRoot();
    }
    private void getRoute() {

        NavigationRoute.Builder testE;
        NavigationRoute route = (testE = NavigationRoute.builder(getActivity())
                .accessToken(getString(R.string.access_token))
                .origin(Point.fromLngLat(GlobalVariables.sourceLng, GlobalVariables.sourceLat))
                .destination(Point.fromLngLat(GlobalVariables.destinationLng, GlobalVariables.destinationLat)))
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .build();
        route.getRoute(new Callback<DirectionsResponse>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(Call<DirectionsResponse> call, @NotNull Response<DirectionsResponse> response) {
                if (response.body() != null && !response.body().routes().isEmpty()) {
                    DirectionsRoute responseRoute = response.body().routes().get(0);
                    if (responseRoute != null) {
                        distance = responseRoute.distance();
                        duration = responseRoute.duration();
                        distanceInKm = TurfConversion.convertLength(distance, TurfConstants.UNIT_METERS, TurfConstants.UNIT_KILOMETERS);
                        durationInMinute = duration / 60;
                        Log.d("check", String.valueOf(distanceInKm));
                        Log.d("check", String.valueOf(durationInMinute));

                        getFairsFromDB();
                    }
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getFairsFromDB() {

        root.child("fairs").addListenerForSingleValueEvent(new ValueEventListener() {
                                                               @Override
                                                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                   if (dataSnapshot.exists()) {

                                                                       fairs = dataSnapshot.getValue(Fairs.class);
                                                                       GlobalVariables.fairs=fairs;
                                                                       pathaoBikeTotalCost = (distanceInKm * Double.valueOf(fairs.getPathaobike().getCost_per_km())) + Double.valueOf(fairs.getPathaobike().getBase_fair());
                                                                       Log.d("checks", pathaoBikeTotalCost.toString());

                                                                       pathaoCarTotalCost = (distanceInKm * Double.valueOf(fairs.getPathaocar().getCost_per_km())) + Double.valueOf(fairs.getPathaocar().getBase_fair());
                                                                       Log.d("checks", pathaoCarTotalCost.toString());

                                                                       uberXTotalCost=(distanceInKm*Double.valueOf(fairs.getUberx().getBase_fair()))+(distanceInKm * Double.valueOf(fairs.getUberx().getCost_per_km()))+((durationInMinute * Double.valueOf(fairs.getUberx().getCost_per_min())));
                                                                       uberMotoTotalCost=(distanceInKm*Double.valueOf(fairs.getUbermoto().getBase_fair()))+(distanceInKm * Double.valueOf(fairs.getUbermoto().getCost_per_km()))+((durationInMinute * Double.valueOf(fairs.getUbermoto().getCost_per_min())));

                                                                   }
                                                               }

                                                               @Override
                                                               public void onCancelled(@NonNull DatabaseError databaseError) {

                                                               }
                                                           });


    }

}
