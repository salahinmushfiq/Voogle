package com.example.voogle.Fragments;


import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.voogle.Activities.MainActivity;
import com.example.voogle.GlobalVariables;
import com.example.voogle.R;
import com.example.voogle.databinding.FragmentMapBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements  OnMapReadyCallback  {
    final int[] count = {0};
    FragmentMapBinding fragmentMapBinding;

    MapboxMap map;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference("root");
    PermissionsManager permissionsManager;
    SymbolManager symbolManager;
    LocationComponent locationComponent;
    LocationComponentActivationOptions locationComponentActivationOptions;
    String sourceLat,sourceLng,destinationLat,destinationLng;
    Geocoder geocoder;
    private MapView mapView;

    private static final LatLng SYDNEY = new LatLng(-33.88, 151.21);
    private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);

    public static final String TAG="mapFrag";
    private ArrayList<Symbol> symbolArrayList = new ArrayList<>();
    LatLng sourceX, destinationX;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private String source=null,destination=null;
    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Write a message to the database



        Mapbox.getInstance(getContext(), getString(R.string.access_token));


        fragmentMapBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_map, container, false);

        initMap(savedInstanceState);


        source=getArguments().getString("source");
        destination=getArguments().getString("destination");
        sourceLat=getArguments().getString("sourceLat");
        destinationLat=getArguments().getString("destinationLat");
        sourceLng=getArguments().getString("sourceLng");
        destinationLng=getArguments().getString("destinationLng");
        Log.i(TAG, "onCreateView: "+source+" and "+destination);
        Toast.makeText(getActivity(), source, Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), sourceLat, Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), sourceLng, Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), destination, Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), destinationLat, Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), destinationLng, Toast.LENGTH_SHORT).show();
        return fragmentMapBinding.getRoot();

    }

    private void initMap(Bundle savedInstanceState) {
//        LatLng source = new LatLng(Double.valueOf(sourceLat), Double.valueOf(sourceLng)), destination = new LatLng(Double.valueOf(destinationLat), Double.valueOf(destinationLng));
        mapView = fragmentMapBinding.mapView;

        mapView.onCreate(savedInstanceState);

        geocoder = new Geocoder(getContext());

        fragmentMapBinding.navView.initialize(new OnNavigationReadyCallback() {
            @Override
            public void onNavigationReady(boolean isRunning) {

            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                fragmentMapBinding.navView.onMapReady(map);
                map.setStyle(new Style.Builder().fromUri(/*"mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"*/ /*"mapbox://styles/mapbox/light-v10"*/ "mapbox://styles/mapbox/navigation-preview-day-v4")
                        .withImage("X", getActivity().getDrawable(R.drawable.ic_location_on_black_24dp)), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        symbolManager = new SymbolManager(mapView, mapboxMap, style);

                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
// Obtain the map from a MapFragment or MapView.
                        UiSettings uiSettings = mapboxMap.getUiSettings();
                        uiSettings.areAllGesturesEnabled();
                        uiSettings.setZoomGesturesEnabled(true);
                        uiSettings.setQuickZoomGesturesEnabled(true);
                        uiSettings.setCompassEnabled(false);
//                        symbolManager.setIconAllowOverlap(true);
//                        symbolManager.setTextAllowOverlap(true);
//                        symbolManager.setIconIgnorePlacement(true);
//                        symbolManager.setTextIgnorePlacement(true);
                        // Toast instructing user to tap on the map



                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(23.738999, 90.387202
                                )) // Sets the new camera position
                                .zoom(14) // Sets the zoom
                                .bearing(180) // Rotate the camera
                                .tilt(30) // Set the camera tilt
                                .build(); // Creates a CameraPosition from the builder
                        locationComponent = mapboxMap.getLocationComponent();

                        locationComponent.activateLocationComponent(locationComponentActivationOptions = LocationComponentActivationOptions.builder(getActivity(), style).build());


                        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);



                                Symbol x = symbolManager.create(new SymbolOptions().withIconImage("X").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng(Double.valueOf(sourceLat), Double.valueOf(sourceLng))).withTextField("Source"));
                                Symbol y = symbolManager.create(new SymbolOptions().withIconImage("X").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng(Double.valueOf(destinationLat), Double.valueOf(destinationLng))).withTextField("Destination"));
                                symbolArrayList.add(x);
                                symbolArrayList.add(y);

                        for (Symbol symbol : symbolArrayList) {

                           // symbol.setIconImage("XRed");
                            symbolManager.update(symbol);
                        }

                        NavigationRoute.Builder builder=NavigationRoute.builder(getActivity()).accessToken(getString(R.string.access_token))
                                .origin(Point.fromLngLat( Double.valueOf(sourceLng),Double.valueOf(sourceLat)))
                                .destination(Point.fromLngLat(Double.valueOf(destinationLng),Double.valueOf(destinationLat)));
                                 builder.addWaypoint(Point.fromLngLat( 90.373302,23.759788));
                            //     builder.addWaypoint(Point.fromLngLat( 90.388891,23.759243));
                                 getWayPoints(builder);
                        for (Integer route : GlobalVariables.sourceRoutes) {
                            //      destinationRoute.add(route);
                            Toast.makeText(getActivity(), "Source Root Map:"+route.toString(), Toast.LENGTH_SHORT).show();
                        }
                        for (Integer route : GlobalVariables.destinationRoutes) {
                            //      destinationRoute.add(route);
                            Toast.makeText(getActivity(), "Destinaion Root Map:"+route.toString(), Toast.LENGTH_SHORT).show();
                        }
                                 builder.build()
                                .getRoute(new Callback<DirectionsResponse>() {
                                    @SuppressLint("LogNotTimber")
                                    @Override
                                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                                        // You can get the generic HTTP info about the response
                                       // Log.d(TAG, "Response code: " + response.code());
                                       // Log.d(TAG, "Response code: " + response.body().routes());
                                        if (response.body() == null) {
                                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                            return;
                                        } else if (response.body().routes().size() < 1) {
                                            Log.e(TAG, "No routes found");
                                            return;
                                        }

                                        currentRoute = response.body().routes().get(0);
                                        try {
                                            final String chunked=new JSONObject(currentRoute.toJson()).toString(4);
                                            final int chunkSize = 2048;
                                            Log.i(TAG, "routed: ");
                                            for (int i = 0; i < chunked.length(); i += chunkSize) {
                                                Log.d(TAG, chunked.substring(i, Math.min(chunked.length(), i + chunkSize)));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        // Draw the route on the map

                                        if (navigationMapRoute != null) {
                                            navigationMapRoute.removeRoute();
                                        } else {
                                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                                        }
                                        navigationMapRoute.addRoute(currentRoute);
                                    }

                                    @Override
                                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                                        Log.e(TAG, "Error: " + throwable.getMessage());
                                    }
                                });

                        // Move the camera instantly to Sydney with a zoom of 15.

                    }

                });

            }


        });

    }

    private void getWayPoints(NavigationRoute.Builder builder) {
       // stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("stops");
      //  stopRef.addValueEventListener(new ValueEventListener() {
          //  @Override
         //   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          //      if (dataSnapshot.exists()) {
             //       for (DataSnapshot data : dataSnapshot.getChildren()) {
               //         if(!data.child("s_no").getValue().toString().equals(source)||!) {
//                            stop = data.child("name").getValue().toString();
//                            Toast.makeText(MainActivity.this,"Stop: "+stop, Toast.LENGTH_SHORT).show();
//                            lat = Double.valueOf(data.child("lat").getValue().toString());
//                            lng = Double.valueOf(data.child("lng").getValue().toString());
//                            Toast.makeText(MainActivity.this, String.valueOf(lat), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, String.valueOf(lng), Toast.LENGTH_SHORT).show();
//                            goToHome.putExtra("sourceLat", data.child("lat").getValue().toString());
//                            goToHome.putExtra("sourceLng", data.child("lng").getValue().toString());
                    //    }
                     //   if(data.child("name").getValue().toString().equals(destination)) {
//                            stop = data.child("name").getValue().toString();
//                            Toast.makeText(MainActivity.this,"Stop: "+stop, Toast.LENGTH_SHORT).show();
//                            lat = Double.valueOf(data.child("lat").getValue().toString());
//                            lng = Double.valueOf(data.child("lng").getValue().toString());
//                            Toast.makeText(MainActivity.this, String.valueOf(lat), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, String.valueOf(lng), Toast.LENGTH_SHORT).show();
//                            goToHome.putExtra("destinationLat", String.valueOf(lat));
//                            goToHome.putExtra("destinationLng", String.valueOf(lng));
                   //    }


                   // }
//                    goToHome.putExtra("source", source);
//                    goToHome.putExtra("destination", destination);
//                    startActivity(goToHome);
             //   } else {
             //       Toast.makeText(getActivity(), "Empty Database", Toast.LENGTH_SHORT).show();
             //   }
          //  }

         //   @Override
         //   public void onCancelled(@NonNull DatabaseError databaseError) {
          //      Toast.makeText(getActivity(), databaseError.toString(), Toast.LENGTH_SHORT).show();
        //    }
       // });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        map = mapboxMap;

    }
}
