package com.example.voogle.Fragments;


import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.voogle.GlobalVariables;
import com.example.voogle.PojoClasses.Stops;
import com.example.voogle.R;
import com.example.voogle.databinding.FragmentMapBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.BASE_API_URL;
import static com.mapbox.core.constants.Constants.PRECISION_6;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    final int[] count = {0};
    FragmentMapBinding fragmentMapBinding;

    MapboxMap map;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference("root");
    DatabaseReference stopRef;
    PermissionsManager permissionsManager;
    SymbolManager symbolManager;
    LocationComponent locationComponent;
    LocationComponentActivationOptions locationComponentActivationOptions;
    double sourceLat;
    double sourceLng;
    double destinationLat;
    double destinationLng;
    Geocoder geocoder;
    private MapView mapView;
    ArrayList<Integer> sourceRoutes, destinationRoutes;
    ArrayList<Point> points;
    ArrayList<Stops> stopss;
    private static final LatLng SYDNEY = new LatLng(-33.88, 151.21);
    private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);
    Double lat, lng;
    public static final String TAG = "mapFrag";
    private ArrayList<Symbol> symbolArrayList = new ArrayList<>();
    ArrayList<Integer> commonRoutes = new ArrayList<>();
    LatLng sourceX, destinationX;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private String source = null, destination = null,name;
    ArrayList<String>routes=new ArrayList<>();

    private Bundle savedInstanceState;
    private static final String LINE_GEOJSON_SOURCE_ID = "LINE_GEOJSON_SOURCE_ID";
    private static final String CIRCLE_GEOJSON_SOURCE_ID = "CIRCLE_GEOJSON_SOURCE_ID";

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Write a message to the database
        sourceRoutes = new ArrayList<>();
        destinationRoutes = new ArrayList<>();
        points = new ArrayList<>();
        stopss=new ArrayList<>();

        this.savedInstanceState = savedInstanceState;

        Mapbox.getInstance(getActivity(), getString(R.string.access_token));


        fragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

        mapView = fragmentMapBinding.mapView;


        source = getArguments().getString("source");
        destination = getArguments().getString("destination");
        sourceLat = getArguments().getDouble("sourceLat");
        destinationLat = getArguments().getDouble("destinationLat");
        sourceLng = getArguments().getDouble("sourceLng");
        destinationLng = getArguments().getDouble("destinationLng");
        Log.i(TAG, "onCreateView: " + source + " and " + destination);
        Toast.makeText(getActivity(), source, Toast.LENGTH_SHORT).show();
        // Toast.makeText(getActivity(), sourceLat, Toast.LENGTH_SHORT).show();
        //  Toast.makeText(getActivity(), sourceLng, Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), destination, Toast.LENGTH_SHORT).show();
        //   Toast.makeText(getActivity(), destinationLat, Toast.LENGTH_SHORT).show();
        //    Toast.makeText(getActivity(), destinationLng, Toast.LENGTH_SHORT).show();

        getCommonRoutes();

        return fragmentMapBinding.getRoot();

    }

    private void initMap(Bundle savedInstanceState, ArrayList<Stops> stops) {
//        LatLng source = new LatLng(Double.valueOf(sourceLat), Double.valueOf(sourceLng)), destination = new LatLng(Double.valueOf(destinationLat), Double.valueOf(destinationLng));
        mapView = fragmentMapBinding.mapView;

        mapView.onCreate(savedInstanceState);

        geocoder = new Geocoder(getContext());

        fragmentMapBinding.navView.initialize(isRunning -> {

        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                fragmentMapBinding.navView.onMapReady(map);
                map.setStyle(new Style.Builder().fromUri(/*"mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"*/ /*"mapbox://styles/mapbox/light-v10"*/ "mapbox://styles/mapbox/navigation-preview-day-v4")
                        .withImage("X", getActivity().getDrawable(R.drawable.ic_location_on_black_24dp))
                        .withImage("Y", getActivity().getDrawable(R.drawable.ic_location_on_red_24dp))
                        .withImage("ROUTE1", getActivity().getDrawable(R.drawable.ic_directions_bus_yellow_24dp))
                        .withImage("ROUTE2", getActivity().getDrawable(R.drawable.ic_directions_bus_blue_24dp))
                        .withImage("ROUTE3", getActivity().getDrawable(R.drawable.ic_directions_bus_red_24dp))
                        .withImage("ROUTE4", getActivity().getDrawable(R.drawable.ic_directions_bus_violet_24dp))
                        .withImage("ROUTE5", getActivity().getDrawable(R.drawable.ic_directions_bus_white_24dp))
                        .withImage("ROUTE6", getActivity().getDrawable(R.drawable.ic_directions_bus_green_24dp))
                        .withImage("ROUTE7", getActivity().getDrawable(R.drawable.ic_directions_bus_orange_24dp)), style -> {

                    symbolManager = new SymbolManager(mapView, mapboxMap, style);


                    // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                    // Obtain the map from a MapFragment or MapView.
                    UiSettings uiSettings = mapboxMap.getUiSettings();
                    uiSettings.setZoomGesturesEnabled(true);
                    uiSettings.setQuickZoomGesturesEnabled(true);
                    uiSettings.setCompassEnabled(true);
                    //                        symbolManager.setIconAllowOverlap(true);
                    //                        symbolManager.setTextAllowOverlap(true);
                    //                        symbolManager.setIconIgnorePlacement(true);
                    //                        symbolManager.setTextIgnorePlacement(true);
                    // Toast instructing user to tap on the map


                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(sourceLat, sourceLng
                            )) // Sets the new camera position
                            .zoom(14) // Sets the zoom
                            .tilt(30) // Set the camera tilt
                            .build(); // Creates a CameraPosition from the builder
                    locationComponent = mapboxMap.getLocationComponent();

                    locationComponent.activateLocationComponent(locationComponentActivationOptions = LocationComponentActivationOptions.builder(getActivity(), style).build());


                    map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);


                    Symbol x = symbolManager.create(new SymbolOptions().withIconImage("X").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng(Double.valueOf(sourceLat), Double.valueOf(sourceLng))).withTextField("Source"));
                    Symbol y = symbolManager.create(new SymbolOptions().withIconImage("Y").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng(Double.valueOf(destinationLat), Double.valueOf(destinationLng))).withTextField("Destination"));
                    symbolArrayList.add(x);
                    symbolArrayList.add(y);



                    for (Stops stop : stops) {
                        //Toast.makeText(getActivity(), "Stopssss: "+stop.getName(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getActivity(), "Stopssss: "+stop.getRoutes(), Toast.LENGTH_SHORT).show();
                        int i;
                        for(i=0;i<stop.getRoutes().size();i++) {
                           // Toast.makeText(getActivity(), "Stopssss: " + route, Toast.LENGTH_SHORT).show();

//                            try {
//                               // Toast.makeText(getActivity(), "routessss"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
//                                if (String.valueOf(stop.getRoutes().get(i)).equals("1") ) {
//                                    Symbol Route1 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE1").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
//                                    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
//                                    symbolArrayList.add(Route1);
//                                    Toast.makeText(getActivity(), "routessss 1: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
//
//                                }
//                                if (String.valueOf(stop.getRoutes().get(i)).equals("2") ) {
//                                    Symbol Route2 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE2").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
//                                    symbolArrayList.add(Route2);
//                                    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), "routessss"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
//                                }
//                                if (String.valueOf(stop.getRoutes().get(i)).equals("3") ) {
//                                    Symbol Route3 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE3").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
//                                    symbolArrayList.add(Route3);
//                                    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
//                                }
//                                if (String.valueOf(stop.getRoutes().get(i)).equals("4") ) {
//                                    Symbol Route4 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE4").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
//                                    symbolArrayList.add(Route4);
//                                    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), "routessss4: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
//                                }if (String.valueOf(stop.getRoutes().get(i)).equals("5") ) {
//                                    Symbol Route5 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE5").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
//                                    symbolArrayList.add(Route5);
//                                    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
//                                }if (String.valueOf(stop.getRoutes().get(i)).equals("6") ) {
//                                    Symbol Route6 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE6").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
//                                    symbolArrayList.add(Route6);
//                                    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
//                                }if (String.valueOf(stop.getRoutes().get(i)).equals("7") ) {
//                                    Symbol Route7 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE7").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
//                                    symbolArrayList.add(Route7);
//                                    Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
//
//                                }
//                            }
//                            catch (Exception e)
//                            {
//                                Log.d(TAG,e.getMessage());
//                            }
                        }
                    }
                    for(int commonRoute:commonRoutes)
                    {
                        int i;
                        for(Stops stop:stops)
                        {
                            for(i=0;i<stop.getRoutes().size();i++)
                            {
                                if(String.valueOf(stop.getRoutes().get(i)).equals(String.valueOf(commonRoute))){
                                    try {
                                        // Toast.makeText(getActivity(), "routessss"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                        if (String.valueOf(stop.getRoutes().get(i)).equals("1") ) {
                                            Symbol Route1 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE1").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                            Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            symbolArrayList.add(Route1);
                                            Toast.makeText(getActivity(), "routessss 1: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                            getRoutes();
                                        }
                                        if (String.valueOf(stop.getRoutes().get(i)).equals("2") ) {
                                            Symbol Route2 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE2").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                            symbolArrayList.add(Route2);
                                            Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getActivity(), "routessss"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                            getRoutes();
                                        }
                                        if (String.valueOf(stop.getRoutes().get(i)).equals("3") ) {
                                            Symbol Route3 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE3").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                            symbolArrayList.add(Route3);
                                            Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            getRoutes();
                                        }
                                        if (String.valueOf(stop.getRoutes().get(i)).equals("4") ) {
                                            Symbol Route4 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE4").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                            symbolArrayList.add(Route4);
                                            Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getActivity(), "routessss4: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                            getRoutes();
                                        }if (String.valueOf(stop.getRoutes().get(i)).equals("5") ) {
                                            Symbol Route5 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE5").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                            symbolArrayList.add(Route5);
                                            Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            getRoutes();
                                        }if (String.valueOf(stop.getRoutes().get(i)).equals("6") ) {
                                            Symbol Route6 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE6").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                            symbolArrayList.add(Route6);
                                            Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            getRoutes();
                                        }if (String.valueOf(stop.getRoutes().get(i)).equals("7") ) {
                                            Symbol Route7 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE7").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                            symbolArrayList.add(Route7);
                                            Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            getRoutes();

                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        Log.d(TAG,e.getMessage());
                                    }
                                }
                            }
                        }
                    }
//                   for(int route:commonRoutes)
//                   {
//                       if(route==1)
//                       {
//                           getRoutes();
//                       }
//                       if(route==2)
//                       {
//                           getRoutes();
//                       }
//                       if(route==3)
//                       {
//                           getRoutes();
//                       }
//                       if(route==4)
//                       {
//                           getRoutes();
//                       }
//                       if(route==5)
//                       {
//                       getRoutes(); }
//                       if(route==6)
//                       {
//                           getRoutes(); }
//                       if(route==7)
//                       {
//                           getRoutes(); }
//                   }
                    for (Symbol symbol : symbolArrayList) {

                        symbolManager.update(symbol);
                    }
//                    MapboxDirections directionsBuilder = MapboxDirections.builder()
//                            .accessToken(getString(R.string.access_token))
//                            .origin(Point.fromLngLat(Double.valueOf(sourceLng), Double.valueOf(sourceLat)))
//                            .destination(Point.fromLngLat(Double.valueOf(destinationLng), Double.valueOf(destinationLat)))
//                            .profile(DirectionsCriteria.PROFILE_DRIVING)
//                            .accessToken(getString(R.string.access_token))
//                            .overview(DirectionsCriteria.OVERVIEW_FULL)
//                            .build();

                    //                        for (Point wayPoint :
                    //                                points) {
                    //                            directionsBuilder.addWaypoint(wayPoint);
                    //                        }

                            /*NavigationRoute.Builder builder = NavigationRoute.builder(getActivity()).accessToken(getString(R.string.access_token))
                                    .origin(Point.fromLngLat(Double.valueOf(sourceLng), Double.valueOf(sourceLat)))
                                    .destination(Point.fromLngLat(Double.valueOf(destinationLng), Double.valueOf(destinationLat)));
                            builder.profile(DirectionsCriteria.PROFILE_DRIVING).user("azkzero");
                            for (Point wayPoint :
                                    points) {
                                builder.addWaypoint(wayPoint);
                            }*/

                         getRoutes();

                            /*builder.build()
                                    .getRoute(*/
                    //                        directionsBuilder.enqueueCall(new Callback<DirectionsResponse>() {
                    //                            @Override public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                    //
                    //                                if (response.body() == null) {
                    //                                   // Log.e("No routes found, make sure you set the right user and access token.");
                    //                                    Toast.makeText(getActivity(), "Routes Not Found", Toast.LENGTH_SHORT).show();
                    //                                    return;
                    //                                } else if (response.body().routes().size() < 1) {
                    //                                   // Log.e("No routes found");
                    //                                    Toast.makeText(getActivity(), "No Routes Found", Toast.LENGTH_SHORT).show();
                    //                                    return;
                    //                                }
                    //
                    //// Retrieve the directions route from the API response
                    //                                currentRoute = response.body().routes().get(0);
                    //                                currentRoute.routeOptions();
                    //                               // currentRoute.duration().toString();
                    //
                    //
                    //                                Toast.makeText(getActivity(), "Current Route"+currentRoute, Toast.LENGTH_SHORT).show();
                    //                                Toast.makeText(getActivity(), currentRoute.duration().toString(), Toast.LENGTH_SHORT).show();
                    //                                Log.d(TAG, currentRoute.toString());
                    //                                drawNavigationPolylineRoute(response.body().routes().get(0), response);
                    //                               //  currentRoute.toBuilder().build();
                    //                            }
                    //
                    //                            @Override public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                    //
                    //                                Log.e(TAG, "Error: " + throwable.getMessage());
                    //
                    //                            }
                    //                        });

                    // Move the camera instantly to Sydney with a zoom of 15.

                });

            }


        });

    }

    private void getRoutes() {
        List<Point> wayPoints = points;
        for (Point xtx :
                wayPoints) {
            Log.i(TAG, "onStyleLoaded: " + xtx.longitude() + " " + xtx.latitude());
        }
        navigationMapRoute = new NavigationMapRoute(null, mapView, map);
        NavigationRoute.Builder testB;
        NavigationRoute test = (testB = NavigationRoute.builder(getActivity()))
                .accessToken(Mapbox.getAccessToken())
                .origin(wayPoints.get(0))
                .destination(wayPoints.get(wayPoints.size() - 1))
                .routeOptions(RouteOptions.builder().steps(true).coordinates(wayPoints).profile(DirectionsCriteria.PROFILE_DRIVING).baseUrl(BASE_API_URL).user(DirectionsCriteria.PROFILE_DEFAULT_USER).geometries(DirectionsCriteria.GEOMETRY_POLYLINE).accessToken(getString(R.string.access_token)).requestUuid("mapboxzero").build()).profile(DirectionsCriteria.PROFILE_DRIVING)
                .alternatives(true)
                .build();
        Log.i(TAG, "onStyleLoaded: " + test);

        //                        testB.routeOptions(RouteOptions.builder().coordinates(orderNumbers).baseUrl(BASE_API_URL).user(DirectionsCriteria.PROFILE_DEFAULT_USER).geometries(DirectionsCriteria.GEOMETRY_POLYLINE).accessToken(getString(R.string.access_token)).requestUuid("mapboxzero").build());

                            /*for (Point point:
                                 points) {
                                testB.addWaypoint(point);
                            }*/

        test.getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() != null) {
                    Log.i(TAG, "onResponse: " + response.body().routes().size());
                    for (DirectionsRoute x :
                            response.body().routes()) {
                        navigationMapRoute.addRoute(x);
                    }
                } else Log.i(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

            }
        });
    }

    private void drawNavigationPolylineRoute(DirectionsRoute directionsRoute, Response<DirectionsResponse> response) {
        if (map != null) {
            map.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
// Retrieve the sources from the map
                    GeoJsonSource circleLayerSource = style.getSourceAs(CIRCLE_GEOJSON_SOURCE_ID);
                    GeoJsonSource lineLayerSource = style.getSourceAs(LINE_GEOJSON_SOURCE_ID);
                    if (circleLayerSource != null && response.body() != null) {

                        List<Feature> featureList = new ArrayList<>();

// Use each step maneuver's location to create a Point Feature.
// The Feature is then added to the list.


// Update the CircleLayer's source with the Feature list.
                        circleLayerSource.setGeoJson(FeatureCollection.fromFeatures(featureList));

// Update the LineLayer's source with the Polyline route from the Directions API response.
                        if (lineLayerSource != null && currentRoute.geometry() != null) {
                            lineLayerSource.setGeoJson(Feature.fromGeometry(LineString.fromPolyline(
                                    currentRoute.geometry(), PRECISION_6)));
                            Log.d(TAG, currentRoute.toString());
                        }

// Ease the camera to fit to the Directions route.

                    }
                }
            });
        }
    }

    private void getCommonRoutes() {


        for (Integer sourceRoute : GlobalVariables.sourceRoutes) {
            sourceRoutes.add(sourceRoute);
            for (Integer destinationRoute : GlobalVariables.destinationRoutes) {
                if (sourceRoute.equals(destinationRoute)) {
                    commonRoutes.add(sourceRoute);
                }
            }
        }
        Toast.makeText(getActivity(), "Source Route Map:" + sourceRoutes.toString(), Toast.LENGTH_SHORT).show();
        for (Integer destinationRoute : GlobalVariables.destinationRoutes) {
            destinationRoutes.add(destinationRoute);

        }
        Toast.makeText(getActivity(), "Destinaion Route Map:" + destinationRoutes.toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Common Routes: " + commonRoutes.toString(), Toast.LENGTH_SHORT).show();

        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("stops");
        stopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot stops : dataSnapshot.getChildren()) {
                        for (DataSnapshot route : stops.child("route").getChildren()) {
                            int i=0;
                            for (int commonRoute : commonRoutes) {
                                if (commonRoute == Integer.valueOf(route.getValue().toString())) {
                                    GlobalVariables.stops.add(stops.getValue(Stops.class));
                                    // Toast.makeText(getActivity(), stops.getValue(Stops.class).toString(), Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getActivity(), stops.child("lat").getValue().toString(), Toast.LENGTH_SHORT).show();
                                    // Toast.makeText(getActivity(), stops.child("lng").getValue().toString(), Toast.LENGTH_SHORT).show();
                                    lat = stops.child("lat").getValue(Double.class);
                                    lng = stops.child("lng").getValue(Double.class);
                                    name = stops.child("name").getValue(String.class);
                                    routes = (ArrayList<String>) stops.child("route").getValue();
                                    //    Toast.makeText(getActivity(), stops.child("name").getValue().toString(), Toast.LENGTH_SHORT).show();
                                    //     Toast.makeText(getActivity(), lat.toString(), Toast.LENGTH_SHORT).show();
                                    //     Toast.makeText(getActivity(), lng.toString(), Toast.LENGTH_SHORT).show();
                                   // points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                                    points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                                    stopss.add(new Stops(name,lat,lng,"front",routes));
                                    points.get(i).latitude();
                                    i++;
                                    
//                                        Integer[] item = points.toArray(new String[points.size()]);
//                                        List<Integer>l2 = new ArrayList<>();
//                                        l2 =  Arrays.asList(item);
//                                    if(commonRoute==1)
//                                    {
//                                        initMap(savedInstanceState);
//
//                                    }
//                                    if(commonRoute==2)
//                                    {
//                                        initMap(savedInstanceState);
//                                    }
//                                    if(commonRoute==3)
//                                    {
//                                        initMap(savedInstanceState);
//                                    }
//                                    if(commonRoute==4)
//                                    {
//                                        initMap(savedInstanceState);
//                                    }
//                                    if(commonRoute==5)
//                                    {
//                                        initMap(savedInstanceState);
//                                    }
//                                    if(commonRoute==6)
//                                    {
//                                        initMap(savedInstanceState);
//                                    }
//                                    if(commonRoute==7)
//                                    {
//                                        initMap(savedInstanceState);
//                                    }
                                }
                            }
                        }
                    }
                    for (Symbol symbol : symbolArrayList) {

                        // symbol.setIconImage("XRed");
                        symbolManager.update(symbol);
                    }
                   initMap(savedInstanceState,stopss);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(getActivity(), "size: " + GlobalVariables.stops.size(), Toast.LENGTH_SHORT).show();
        for (Stops stops : GlobalVariables.stops) {
         //   Toast.makeText(getActivity(), "Stops" + stops.toString(), Toast.LENGTH_SHORT).show();
           // Toast.makeText(getActivity(), "Stop Name" + stops.getName().toString(), Toast.LENGTH_SHORT).show();
           // Toast.makeText(getActivity(), "Stop Lat" + String.valueOf(stops.getLat()), Toast.LENGTH_SHORT).show();
           // Toast.makeText(getActivity(), "Stop Lng" + String.valueOf(stops.getLng()), Toast.LENGTH_SHORT).show();
        }
    }

    private void getWayPoints(NavigationRoute.Builder builder) {
//         stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("stops");
//          stopRef.addValueEventListener(new ValueEventListener() {
//          @Override
//           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//              if (dataSnapshot.exists()) {
//               for (DataSnapshot data : dataSnapshot.getChildren()) {
//                 if(!data.child("s_no").getValue().toString().equals(source)||!data.child("s_no").getValue().toString().equals(destination)) {
//                        //    stop = data.child("name").getValue().toString();
//                         //   Toast.makeText(MainActivity.this,"Stop: "+stop, Toast.LENGTH_SHORT).show();
//                            lat = Double.valueOf(data.child("lat").getValue().toString());
//                            lng = Double.valueOf(data.child("lng").getValue().toString());
//                            Toast.makeText(getActivity(), String.valueOf(lat), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getActivity(), String.valueOf(lng), Toast.LENGTH_SHORT).show();
//
//                     }
//                    if(data.child("name").getValue().toString().equals(destination)) {
//                          //  stop = data.child("name").getValue().toString();
//                         //   Toast.makeText(MainActivity.this,"Stop: "+stop, Toast.LENGTH_SHORT).show();
//                            lat = Double.valueOf(data.child("lat").getValue().toString());
//                            lng = Double.valueOf(data.child("lng").getValue().toString());
//                            Toast.makeText(getActivity(), String.valueOf(lat), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getActivity(), String.valueOf(lng), Toast.LENGTH_SHORT).show();
//
//                    }
//
//
//            }
//
//           } else {
//               Toast.makeText(getActivity(), "Empty Database", Toast.LENGTH_SHORT).show();
//           }
//          }
//
//           @Override
//           public void onCancelled(@NonNull DatabaseError databaseError) {
//              Toast.makeText(getActivity(), databaseError.toString(), Toast.LENGTH_SHORT).show();
//            }
//         });
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