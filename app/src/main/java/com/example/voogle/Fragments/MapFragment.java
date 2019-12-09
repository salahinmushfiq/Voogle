package com.example.voogle.Fragments;



import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.voogle.R;

import com.example.voogle.databinding.FragmentMapBinding;




import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;

import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements  OnMapReadyCallback  {
    final int[] count = {0};
    FragmentMapBinding fragmentMapBinding;
    MapboxMap map;

    PermissionsManager permissionsManager;

    private LocationComponent locationComponent;

    Geocoder geocoder;
    private MapView mapView;

    private static final LatLng SYDNEY = new LatLng(-33.88,151.21);
    private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Mapbox.getInstance(getContext(), getString(R.string.access_token));


        fragmentMapBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_map, container, false);

        initMap(savedInstanceState);


        return fragmentMapBinding.getRoot();

    }

    private void initMap(Bundle savedInstanceState) {
        LatLng source=new LatLng(23,90),destination=new LatLng(23,90);
        mapView = fragmentMapBinding.mapView;

        mapView.onCreate(savedInstanceState);

        geocoder=new Geocoder(getContext());
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                map = mapboxMap;
                map.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
// Obtain the map from a MapFragment or MapView.
                        UiSettings uiSettings = mapboxMap.getUiSettings();
                        uiSettings.areAllGesturesEnabled();
                        uiSettings.setZoomGesturesEnabled(true);
                        uiSettings.setQuickZoomGesturesEnabled(true);
                        uiSettings.setCompassEnabled(false);

                        // Toast instructing user to tap on the map



                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(23.738999, 90.387202
                                )) // Sets the new camera position
                                .zoom(14) // Sets the zoom
                                .bearing(180) // Rotate the camera
                                .tilt(30) // Set the camera tilt
                                .build(); // Creates a CameraPosition from the builder

                        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
// Move the camera instantly to Sydney with a zoom of 15.



                        map.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                            @Override
                            public boolean onMapLongClick(@NonNull LatLng point) {

                                Point source, destination;

                                count[0]++;
                                if(count[0]<=2)
                                {

                                    try {
                                        List <Address>addresses =geocoder.getFromLocation(point.getLatitude(),point.getLongitude(),1);
                                        mapboxMap.addMarker(new MarkerOptions().position(point).snippet(addresses.get(0).getAddressLine(0)));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if(count[0]==1)
                                    {

                                        Toast.makeText(getContext(), "source", Toast.LENGTH_SHORT).show();
                                    }
                                    if(count[0]==2)
                                    {

                                        Toast.makeText(getContext(), "Destination", Toast.LENGTH_SHORT).show();

                                    }

                                }
                                else {
                                    Toast.makeText(getContext(), "Already source and destination added", Toast.LENGTH_SHORT).show();
                                }

//                                List <Address>addresses=null;
//                                Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
//
//                                Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
//                                        locationComponent.getLastKnownLocation().getLatitude());
//
//                                GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
//                                if (source != null) {
//                                    source.setGeoJson(Feature.fromGeometry(destinationPoint));  //adding marker
//                                    try {
//                                        addresses=geocoder.getFromLocation(destination.getLatitude(),destination.getLongitude(),1);
//
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Toast.makeText(getContext(), addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
//                                }
                                return true;
                            }
                        });
                    }

                });

            }


        });

    }

    @Override
    public void onStart(){
        super.onStart();
        mapView.onStart();
    }
    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        map = mapboxMap;

    }
}
