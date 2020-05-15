package com.example.voogle.Fragments;


import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.example.voogle.GlobalVariables;
import com.example.voogle.PojoClasses.*;
import com.example.voogle.R;
import com.example.voogle.databinding.FragmentFaresBinding;
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

    public FaresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentFaresBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fares, container, false);
        getRoute();


        return fragmentFaresBinding.getRoot();
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

                        getFaresFromDB();
                    }
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFaresFromDB() {

        root.child("fares").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    fares = dataSnapshot.getValue(Fares.class);
                    GlobalVariables.fares = fares;
                    pathaoBikeTotalCost = (distanceInKm * Double.parseDouble(fares.getPathaobike().getCost_per_km())) + Double.parseDouble(fares.getPathaobike().getBase_fair());
                    Log.d("checks", pathaoBikeTotalCost.toString());

                    pathaoCarTotalCost = (distanceInKm * Double.parseDouble(fares.getPathaocar().getCost_per_km())) + Double.parseDouble(fares.getPathaocar().getBase_fair());
                    Log.d("checks", pathaoCarTotalCost.toString());

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
