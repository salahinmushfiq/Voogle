package com.example.voogle.Fragments;


import android.annotation.SuppressLint;
import android.graphics.Color;
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
import com.example.voogle.PojoClasses.Stops;
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
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

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
    String sourceLat, sourceLng, destinationLat, destinationLng;
    Geocoder geocoder;
    private MapView mapView;
    ArrayList<Integer> sourceRoutes, destinationRoutes;
    private static final LatLng SYDNEY = new LatLng(-33.88, 151.21);
    private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);
    Double lat, lng;
    public static final String TAG = "mapFrag";
    private ArrayList<Symbol> symbolArrayList = new ArrayList<>();
    LatLng sourceX, destinationX;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private String source = null, destination = null;
    ArrayList<Point> points;
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

        this.savedInstanceState = savedInstanceState;

        Mapbox.getInstance(getContext(), getString(R.string.access_token));


        fragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

        mapView = fragmentMapBinding.mapView;


        source = getArguments().getString("source");
        destination = getArguments().getString("destination");
        sourceLat = getArguments().getString("sourceLat");
        destinationLat = getArguments().getString("destinationLat");
        sourceLng = getArguments().getString("sourceLng");
        destinationLng = getArguments().getString("destinationLng");
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

                        MapboxDirections directionsBuilder = MapboxDirections.builder()
                                .accessToken(getString(R.string.access_token))
                                .origin(Point.fromLngLat(Double.valueOf(sourceLng), Double.valueOf(sourceLat)))
                                .destination(Point.fromLngLat(Double.valueOf(destinationLng), Double.valueOf(destinationLat)))
                                .profile(DirectionsCriteria.PROFILE_DRIVING)
                                .accessToken(getString(R.string.access_token))
                                .overview(DirectionsCriteria.OVERVIEW_FULL)
                                .build();

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
                    //    List<Points> orderNumbers=points;
                        navigationMapRoute = new NavigationMapRoute(null, mapView, map);
                        NavigationRoute.builder(getActivity())
                                .accessToken(Mapbox.getAccessToken())
                                .origin(Point.fromLngLat(Double.valueOf(sourceLng), Double.valueOf(sourceLat)))
                                .destination(Point.fromLngLat(Double.valueOf(destinationLng), Double.valueOf(destinationLat)))
                                .alternatives(true)
                                .build()
                                .getRoute(new Callback<DirectionsResponse>() {
                                    @Override
                                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                                        currentRoute=response.body().routes().get(0);
                                       // currentRoute.routeOptions(RouteOptions.builder().coordinates(pointso).build());
                                        navigationMapRoute.addRoute(currentRoute);
                                    }

                                    @Override
                                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                                    }
                                });
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

                    }

                });

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
        ArrayList<Integer> commonRoutes = new ArrayList<>();

        for (Integer sourceRoute : GlobalVariables.sourceRoutes) {
            sourceRoutes.add(sourceRoute);
            for (Integer destinationRoute : GlobalVariables.destinationRoutes) {
                if (sourceRoute.equals(destinationRoute) ) {
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

                            for (int commonRoute : commonRoutes) {
                                if (commonRoute == Integer.valueOf(route.getValue().toString())) {
                                    GlobalVariables.stops.add(stops.getValue(Stops.class));
                                    // Toast.makeText(getActivity(), stops.getValue(Stops.class).toString(), Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getActivity(), stops.child("lat").getValue().toString(), Toast.LENGTH_SHORT).show();
                                    // Toast.makeText(getActivity(), stops.child("lng").getValue().toString(), Toast.LENGTH_SHORT).show();
                                    lat = stops.child("lat").getValue(Double.class);
                                    lng = stops.child("lng").getValue(Double.class);

                                //    Toast.makeText(getActivity(), stops.child("name").getValue().toString(), Toast.LENGTH_SHORT).show();
                               //     Toast.makeText(getActivity(), lat.toString(), Toast.LENGTH_SHORT).show();
                               //     Toast.makeText(getActivity(), lng.toString(), Toast.LENGTH_SHORT).show();
                                    points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
//                                        Integer[] item = points.toArray(new String[points.size()]);
//                                        List<Integer>l2 = new ArrayList<>();
//                                        l2 =  Arrays.asList(item);

                                }
                            }
                        }
                    }

                    initMap(savedInstanceState);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(getActivity(), "size: " + GlobalVariables.stops.size(), Toast.LENGTH_SHORT).show();
        for (Stops stops : GlobalVariables.stops) {
            Toast.makeText(getActivity(), "Stops" + stops.toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "Stop Name" + stops.getName().toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "Stop Lat" + String.valueOf(stops.getLat()), Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "Stop Lng" + String.valueOf(stops.getLng()), Toast.LENGTH_SHORT).show();
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
