package com.example.voogle.Fragments;

import android.graphics.Color;
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
import com.example.voogle.PojoClasses.Location;
import com.example.voogle.PojoClasses.Stops;
import com.example.voogle.R;
import com.example.voogle.databinding.FragmentMapBinding;
import com.google.firebase.database.*;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.*;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


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
    Symbol locationPointer;
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
    ArrayList<Point> route1Points = new ArrayList<>();
    ArrayList<Point> route2Points = new ArrayList<>();
    ArrayList<Point> route3Points = new ArrayList<>();
    ArrayList<Point> route4Points = new ArrayList<>();
    ArrayList<Point> route5Points = new ArrayList<>();
    ArrayList<Point> route6Points = new ArrayList<>();
    ArrayList<Point> route7Points = new ArrayList<>();
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
    private String source = null, destination = null, name;
    ArrayList<String> routes = new ArrayList<>();
    ArrayList<Location> locations = new ArrayList<>();
    int addLayer1Flag, addLayer2Flag, addLayer3Flag, addLayer4Flag, addLayer5Flag, addLayer6Flag, addLayer7Flag;
    private Bundle savedInstanceState;
    private static final String LINE_GEOJSON_SOURCE_ID = "LINE_GEOJSON_SOURCE_ID";
    private static final String CIRCLE_GEOJSON_SOURCE_ID = "CIRCLE_GEOJSON_SOURCE_ID";
    LineLayer route1, route2, route3, route4, route5, route7;

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
        stopss = new ArrayList<>();

        this.savedInstanceState = savedInstanceState;

        Mapbox.getInstance(getActivity(), getString(R.string.access_token));


        fragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

        mapView = fragmentMapBinding.mapView;


        source = GlobalVariables.sourceName;
        destination = GlobalVariables.destinationName;
        sourceLat = GlobalVariables.sourceLat;
        sourceLng = GlobalVariables.sourceLng;
        destinationLat = GlobalVariables.destinationLat;
        destinationLng = GlobalVariables.destinationLng;

        Log.i(TAG, "onCreateView: " + source + " and " + destination);
        Toast.makeText(getActivity(), source, Toast.LENGTH_SHORT).show();

        Toast.makeText(getActivity(), destination, Toast.LENGTH_SHORT).show();


        readLocations();
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
                        .withImage("LocationPointer", getActivity().getDrawable(R.drawable.ic_person_pin_circle_yellow_24dp))
                        .withImage("ROUTE1", getActivity().getDrawable(R.drawable.ic_directions_bus_yellow_24dp))
                        .withImage("ROUTE2", getActivity().getDrawable(R.drawable.ic_directions_bus_blue_24dp))
                        .withImage("ROUTE3", getActivity().getDrawable(R.drawable.ic_directions_bus_red_24dp))
                        .withImage("ROUTE4", getActivity().getDrawable(R.drawable.ic_directions_bus_violet_24dp))
                        .withImage("ROUTE5", getActivity().getDrawable(R.drawable.ic_directions_bus_white_24dp))
                        .withImage("ROUTE6", getActivity().getDrawable(R.drawable.ic_directions_bus_green_24dp))
                        .withImage("ROUTE7", getActivity().getDrawable(R.drawable.ic_directions_bus_orange_24dp)), style -> {


                    symbolManager = new SymbolManager(mapView, mapboxMap, style);


                    UiSettings uiSettings = mapboxMap.getUiSettings();
                    uiSettings.setZoomGesturesEnabled(true);
                    uiSettings.setQuickZoomGesturesEnabled(true);
                    uiSettings.setCompassEnabled(true);

                    GeoJsonSource geoJsonSource1 = new GeoJsonSource("ROUTE1");
                    try {
                        URI uri = new URI("asset://route1.geojson");
                        Log.i(TAG, "onStyleLoaded: " + uri);
                        style.addSource(geoJsonSource1 = new GeoJsonSource("ROUTE1", uri));
                        Log.i(TAG, "onStyleLoaded: " + style.getSources());
                    } catch (NullPointerException | URISyntaxException e) {
                        e.printStackTrace();
                    }

                    GeoJsonSource geoJsonSource2 = new GeoJsonSource("ROUTE2");
                    try {
                        URI uri = new URI("asset://route2.geojson");
                        Log.i(TAG, "onStyleLoaded: " + uri);
                        style.addSource(geoJsonSource2 = new GeoJsonSource("ROUTE2", uri));
                        Log.i(TAG, "onStyleLoaded: " + style.getSources());
                    } catch (NullPointerException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                    GeoJsonSource geoJsonSource3 = new GeoJsonSource("ROUTE3");
                    try {
                        URI uri = new URI("asset://route3.geojson");
                        Log.i(TAG, "onStyleLoaded: " + uri);
                        style.addSource(geoJsonSource3 = new GeoJsonSource("ROUTE3", uri));
                        Log.i(TAG, "onStyleLoaded: " + style.getSources());
                    } catch (NullPointerException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                    GeoJsonSource geoJsonSource4 = new GeoJsonSource("ROUTE4");
                    try {
                        URI uri = new URI("asset://route4.geojson");
                        Log.i(TAG, "onStyleLoaded: " + uri);
                        style.addSource(geoJsonSource4 = new GeoJsonSource("ROUTE4", uri));
                        Log.i(TAG, "onStyleLoaded: " + style.getSources());
                    } catch (NullPointerException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                    GeoJsonSource geoJsonSource5 = new GeoJsonSource("ROUTE5");
                    try {
                        URI uri = new URI("asset://route5.geojson");
                        Log.i(TAG, "onStyleLoaded: " + uri);
                        style.addSource(geoJsonSource5 = new GeoJsonSource("ROUTE5", uri));
                        Log.i(TAG, "onStyleLoaded: " + style.getSources());
                    } catch (NullPointerException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                    GeoJsonSource geoJsonSource7 = new GeoJsonSource("ROUTE7");
                    try {
                        URI uri = new URI("asset://route7.geojson");
                        Log.i(TAG, "onStyleLoaded: " + uri);
                        style.addSource(geoJsonSource7 = new GeoJsonSource("ROUTE7", uri));
                        Log.i(TAG, "onStyleLoaded: " + style.getSources());
                    } catch (NullPointerException | URISyntaxException e) {
                        e.printStackTrace();
                    }

                    route1 = new LineLayer("ROUTE1L", "ROUTE1");
                    route2 = new LineLayer("ROUTE2L", "ROUTE2");
                    route3 = new LineLayer("ROUTE3L", "ROUTE3");
                    route4 = new LineLayer("ROUTE4L", "ROUTE4");
                    route5 = new LineLayer("ROUTE5L", "ROUTE5");
                    route7 = new LineLayer("ROUTE7L", "ROUTE7");

                    route1.setProperties(PropertyFactory.fillColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5eafe5")));
                    route2.setProperties(PropertyFactory.fillColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
                    route3.setProperties(PropertyFactory.fillColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5ee5b8")));
                    route4.setProperties(PropertyFactory.fillColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
                    /* red color*/
                    route5.setProperties(PropertyFactory.fillColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
                    route7.setProperties(PropertyFactory.fillColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));

                    addLayer1Flag = 0;
                    addLayer2Flag = 0;
                    addLayer3Flag = 0;
                    addLayer4Flag = 0;
                    addLayer5Flag = 0;
                    addLayer6Flag = 0;
                    addLayer7Flag = 0;

                    if (!commonRoutes.isEmpty()) {
                        for (int commonRoute : commonRoutes) {

                            if (addLayer1Flag == 0) {
                                if (commonRoute == 1) {
                                    Toast.makeText(getActivity(), "Route 1 called", Toast.LENGTH_SHORT).show();
                                    style.addLayer(route1);
                                    addLayer1Flag = 1;
                                }
                            }

                            if (addLayer2Flag == 0) {
                                if (commonRoute == 2) {
                                    Toast.makeText(getActivity(), "Route 2 called", Toast.LENGTH_SHORT).show();
                                    style.addLayer(route2);
                                    addLayer2Flag = 1;
                                }
                            }

                            if (addLayer3Flag == 0) {
                                if (commonRoute == 3) {
                                    Toast.makeText(getActivity(), "Route 3 called", Toast.LENGTH_SHORT).show();
                                    style.addLayer(route3);
                                    addLayer3Flag = 1;
                                }
                            }
                            if (addLayer4Flag == 0) {
                                if (commonRoute == 4) {
                                    Toast.makeText(getActivity(), "Route 4 called", Toast.LENGTH_SHORT).show();
                                    style.addLayer(route4);
                                    addLayer4Flag = 1;
                                }
                            }

                            if (addLayer5Flag == 0) {
                                if (commonRoute == 5) {
                                    Toast.makeText(getActivity(), "Route 5 called", Toast.LENGTH_SHORT).show();
                                    style.addLayer(route5);
                                    addLayer5Flag = 1;
                                }
                            }
                            if (addLayer7Flag == 0) {
                                if (commonRoute == 7) {
                                    Toast.makeText(getActivity(), "Route 7 called", Toast.LENGTH_SHORT).show();
                                    style.addLayer(route7);
                                    addLayer7Flag = 1;
                                }
                            }

                        }

                    }

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


                    for (int commonRoute : commonRoutes) {
                        int i;
                        for (Stops stop : stops) {
                            for (i = 0; i < stop.getRoutes().size(); i++) {
                                if (String.valueOf(stop.getRoutes().get(i)).equals(String.valueOf(commonRoute))) {
                                    try {
                                        // Toast.makeText(getActivity(), "routessss"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                        if (String.valueOf(stop.getRoutes().get(i)).equals("1")) {
                                            Symbol Route1 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE1").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                            //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            //    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            symbolArrayList.add(Route1);
                                            //   Toast.makeText(getActivity(), "routessss 1: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                            lat = stop.getLat();
                                            lng = stop.getLng();
                                            route1Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                                        }
                                        if (String.valueOf(stop.getRoutes().get(i)).equals("2")) {
                                            Symbol Route2 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE2").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                            symbolArrayList.add(Route2);
                                            //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getActivity(), "routessss" + String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                            lat = stop.getLat();
                                            lng = stop.getLng();
                                            route2Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                                        }
                                        if (String.valueOf(stop.getRoutes().get(i)).equals("3")) {
                                            Symbol Route3 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE3").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));

                                            symbolArrayList.add(Route3);
                                            // Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            //  Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            lat = stop.getLat();
                                            lng = stop.getLng();
                                            route3Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                                        }
                                        if (String.valueOf(stop.getRoutes().get(i)).equals("4")) {
                                            Symbol Route4 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE4").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F")
                                                    .withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                            symbolArrayList.add(Route4);
                                            //      Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            //      Toast.makeText(getActivity(), "routessss4: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                            lat = stop.getLat();
                                            lng = stop.getLng();
                                            route4Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                                        }
                                        if (String.valueOf(stop.getRoutes().get(i)).equals("5")) {
                                            Symbol Route5 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE5").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                            symbolArrayList.add(Route5);
                                            //       Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            //     Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            lat = stop.getLat();
                                            lng = stop.getLng();
                                            route5Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                                        }
                                        if (String.valueOf(stop.getRoutes().get(i)).equals("6")) {
                                            Symbol Route6 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE6").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                            symbolArrayList.add(Route6);
                                            //    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            lat = stop.getLat();
                                            lng = stop.getLng();
                                            route6Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                                        }
                                        if (String.valueOf(stop.getRoutes().get(i)).equals("7")) {
                                            Symbol Route7 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE7").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                            symbolArrayList.add(Route7);
                                            //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                            //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                            //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                            lat = stop.getLat();
                                            lng = stop.getLng();
                                            route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.getMessage());
                                    }
                                }
                            }
                        }
                    }


                    for (Symbol symbol : symbolArrayList) {

                        symbolManager.update(symbol);
                    }

//

                });

            }


        });

    }

    private void readLocations() {


        /*locationPointer = symbolManager.create(new SymbolOptions().withIconImage("LocationPointer").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000")
                .withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng(Double.valueOf(23.65743), Double.valueOf(90.2332))).withTextField("License Plate"));*/
        final Location[] current_location = {new Location()};
        root.child("locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot location : dataSnapshot.getChildren()) {
                    locations.add(location.getValue(Location.class));
                    current_location[0] = location.getValue(Location.class);
                    Toast.makeText(getActivity(), "Lat: " + current_location[0].getLat() + " Lng: " + current_location[0].getLng() +
                            " License Plate: " + current_location[0].getLicensePlate(), Toast.LENGTH_SHORT).show();

                    locationPointer.setLatLng(new LatLng(current_location[0].getLat(), current_location[0].getLng()));
                    symbolManager.update(locationPointer);
                }


                symbolManager.addClickListener(new OnSymbolClickListener() {
                    @Override
                    public void onAnnotationClick(Symbol symbol) {
                        symbol.setIconAnchor(current_location[0].getLicensePlate());
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


//    private void getRoutes(ArrayList<Point> route1Points, ArrayList<Point> route2Points, ArrayList<Point> route3Points, ArrayList<Point> route4Points, ArrayList<Point> route5Points, ArrayList<Point> route6Points, ArrayList<Point> route7Points) {
//        List<Point> wayPoints = points;
//        List<Point> route1Point = route1Points;
//        List<Point> route2Point = route2Points;
//        List<Point> route3Point =  route3Points;
//        List<Point> route4Point = route4Points;
//        List<Point> route5Point = route5Points;
//        List<Point> route6Point = route6Points;
//        List<Point> route7Point = route7Points;
//
//
//
//            navigationMapRoute = new NavigationMapRoute(null, mapView, map);
//
////            if(!route1Point.isEmpty()) {
////                Toast.makeText(getActivity(), "Route 1 Called", Toast.LENGTH_SHORT).show();
////
////
////                NavigationRoute.Builder testC;
////                NavigationRoute test2= (testC = NavigationRoute.builder(getActivity()))
////                        .accessToken(Mapbox.getAccessToken())
////                        .origin(route1Point.get(0))
////                        .destination(route1Point.get(route1Point.size() - 1))
////                        .routeOptions(RouteOptions.builder().steps(true).coordinates(route1Point).profile(DirectionsCriteria.PROFILE_WALKING).baseUrl(BASE_API_URL).user(DirectionsCriteria.PROFILE_DEFAULT_USER).geometries(DirectionsCriteria.GEOMETRY_POLYLINE).accessToken(getString(R.string.access_token)).requestUuid("mapboxzero").build()).profile(DirectionsCriteria.PROFILE_DRIVING)
////                        .alternatives(false)
////                        .build();
////                Log.i(TAG, "onStyleLoaded: " + test2);
////
////                test2.getRoute(new Callback<DirectionsResponse>() {
////                    @Override
////                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
////                        if (response.body() != null) {
////                            Log.i(TAG, "onResponse: " + response.body().routes().size());
////                            for (DirectionsRoute x :
////                                    response.body().routes()) {
////                                navigationMapRoute.addRoute(x);
////                            }
////                        } else Log.i(TAG, "onResponse: " + response);
////                    }
////
////                    @Override
////                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
////
////                    }
////                });
////            }
////            if(!route2Point.isEmpty()) {
////            Toast.makeText(getActivity(), "Route 7 Called", Toast.LENGTH_SHORT).show();
////
////            NavigationRoute.Builder testF;
////            NavigationRoute test3= (testF= NavigationRoute.builder(getActivity()))
////                    .accessToken(Mapbox.getAccessToken())
////                    .origin(route2Point.get(0))
////                    .destination(route2Point.get(route2Point.size() - 1))
////                    .routeOptions(RouteOptions.builder().steps(true).coordinates(route2Point).profile(DirectionsCriteria.PROFILE_DRIVING).baseUrl(BASE_API_URL).user(DirectionsCriteria.PROFILE_DEFAULT_USER).geometries(DirectionsCriteria.GEOMETRY_POLYLINE).accessToken(getString(R.string.access_token)).requestUuid("mapboxzero").build()).profile(DirectionsCriteria.PROFILE_DRIVING)
////                    .alternatives(false).build();
////
////            Log.i(TAG, "onStyleLoaded: " + test3);
////
////            test3.getRoute(new Callback<DirectionsResponse>() {
////                @Override
////                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
////                    if (response.body() != null) {
////                        Log.i(TAG, "onResponse: " + response.body().routes().size());
////                        for (DirectionsRoute x :
////                                response.body().routes()) {
////                            navigationMapRoute.addRoute(x);
////                        }
////                    } else Log.i(TAG, "onResponse: " + response);
////                }
////
////                @Override
////                public void onFailure(Call<DirectionsResponse> call, Throwable t) {
////
////                }
////            });
////        }
////            if(!route3Point.isEmpty()) {
////            Toast.makeText(getActivity(), "Route 7 Called", Toast.LENGTH_SHORT).show();
////
////            NavigationRoute.Builder testE;
////            NavigationRoute test3= (testE= NavigationRoute.builder(getActivity()))
////                    .accessToken(Mapbox.getAccessToken())
////                    .origin(route3Point.get(0))
////                    .destination(route3Point.get(route3Point.size() - 1))
////                    .routeOptions(RouteOptions.builder().steps(true).coordinates(route3Point).profile(DirectionsCriteria.PROFILE_DRIVING).baseUrl(BASE_API_URL).user(DirectionsCriteria.PROFILE_DEFAULT_USER).geometries(DirectionsCriteria.GEOMETRY_POLYLINE).accessToken(getString(R.string.access_token)).requestUuid("mapboxzero").build()).profile(DirectionsCriteria.PROFILE_DRIVING)
////                    .alternatives(false)
////                    .build();
////            Log.i(TAG, "onStyleLoaded: " + test3);
////
////            test3.getRoute(new Callback<DirectionsResponse>() {
////                @Override
////                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
////                    if (response.body() != null) {
////                        Log.i(TAG, "onResponse: " + response.body().routes().size());
////                        for (DirectionsRoute x :
////                                response.body().routes()) {
////                            navigationMapRoute.addRoute(x);
////                        }
////                    } else Log.i(TAG, "onResponse: " + response);
////                }
////
////                @Override
////                public void onFailure(Call<DirectionsResponse> call, Throwable t) {
////
////                }
////            });
////        }
////            if(!route4Point.isEmpty()) {
////            Toast.makeText(getActivity(), "Route 7 Called", Toast.LENGTH_SHORT).show();
////
////            NavigationRoute.Builder testE;
////            NavigationRoute test3= (testE= NavigationRoute.builder(getActivity()))
////                    .accessToken(Mapbox.getAccessToken())
////                    .origin(route4Point.get(0))
////                    .destination(route4Point.get(route4Point.size() - 1))
////                    .routeOptions(RouteOptions.builder().steps(true).coordinates(route4Point).profile(DirectionsCriteria.PROFILE_DRIVING).baseUrl(BASE_API_URL).user(DirectionsCriteria.PROFILE_DEFAULT_USER).geometries(DirectionsCriteria.GEOMETRY_POLYLINE).accessToken(getString(R.string.access_token)).requestUuid("mapboxzero").build()).profile(DirectionsCriteria.PROFILE_DRIVING)
////                    .alternatives(false)
////                    .build();
////            Log.i(TAG, "onStyleLoaded: " + test3);
////
////            test3.getRoute(new Callback<DirectionsResponse>() {
////                @Override
////                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
////                    if (response.body() != null) {
////                        Log.i(TAG, "onResponse: " + response.body().routes().size());
////                        for (DirectionsRoute x :
////                                response.body().routes()) {
////                            navigationMapRoute.addRoute(x);
////                        }
////                    } else Log.i(TAG, "onResponse: " + response);
////                }
////
////                @Override
////                public void onFailure(Call<DirectionsResponse> call, Throwable t) {
////
////                }
////            });
////        }
////            if(!route5Point.isEmpty()) {
////            Toast.makeText(getActivity(), "Route 7 Called", Toast.LENGTH_SHORT).show();
////
////            NavigationRoute.Builder testE;
////            NavigationRoute test3= (testE= NavigationRoute.builder(getActivity()))
////                    .accessToken(Mapbox.getAccessToken())
////                    .origin(route5Point.get(0))
////                    .destination(route5Point.get(route5Point.size() - 1))
////                    .routeOptions(RouteOptions.builder().steps(true).coordinates(route5Point).profile(DirectionsCriteria.PROFILE_DRIVING).baseUrl(BASE_API_URL).user(DirectionsCriteria.PROFILE_DEFAULT_USER).geometries(DirectionsCriteria.GEOMETRY_POLYLINE).accessToken(getString(R.string.access_token)).requestUuid("mapboxzero").build()).profile(DirectionsCriteria.PROFILE_DRIVING)
////                    .alternatives(false)
////                    .build();
////            Log.i(TAG, "onStyleLoaded: " + test3);
////
////            test3.getRoute(new Callback<DirectionsResponse>() {
////                @Override
////                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
////                    if (response.body() != null) {
////                        Log.i(TAG, "onResponse: " + response.body().routes().size());
////                        for (DirectionsRoute x :
////                                response.body().routes()) {
////                            navigationMapRoute.addRoute(x);
////                        }
////                    } else Log.i(TAG, "onResponse: " + response);
////                }
////
////                @Override
////                public void onFailure(Call<DirectionsResponse> call, Throwable t) {
////
////                }
////            });
////        }
////            if(!route6Point.isEmpty()) {
////            Toast.makeText(getActivity(), "Route 7 Called", Toast.LENGTH_SHORT).show();
////
////            NavigationRoute.Builder testE;
////            NavigationRoute test3= (testE= NavigationRoute.builder(getActivity()))
////                    .accessToken(Mapbox.getAccessToken())
////                    .origin(route6Point.get(0))
////                    .destination(route6Point.get(route6Point.size() - 1))
////                    .routeOptions(RouteOptions.builder().steps(true).coordinates(route6Point).profile(DirectionsCriteria.PROFILE_DRIVING).baseUrl(BASE_API_URL).user(DirectionsCriteria.PROFILE_DEFAULT_USER).geometries(DirectionsCriteria.GEOMETRY_POLYLINE).accessToken(getString(R.string.access_token)).requestUuid("mapboxzero").build()).profile(DirectionsCriteria.PROFILE_DRIVING)
////                    .alternatives(false)
////                    .build();
////            Log.i(TAG, "onStyleLoaded: " + test3);
////
////            test3.getRoute(new Callback<DirectionsResponse>() {
////                @Override
////                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
////                    if (response.body() != null) {
////                        Log.i(TAG, "onResponse: " + response.body().routes().size());
////                        for (DirectionsRoute x :
////                                response.body().routes()) {
////                            navigationMapRoute.addRoute(x);
////                        }
////                    } else Log.i(TAG, "onResponse: " + response);
////                }
////
////                @Override
////                public void onFailure(Call<DirectionsResponse> call, Throwable t) {
////
////                }
////            });
////        }
////            if(!route7Point.isEmpty()) {
////            Toast.makeText(getActivity(), "Route 7 Called", Toast.LENGTH_SHORT).show();
////
////            NavigationRoute.Builder testD;
////            NavigationRoute test3= (testD = NavigationRoute.builder(getActivity()))
////                    .accessToken(Mapbox.getAccessToken())
////                    .origin(route7Point.get(0))
////                    .destination(route7Point.get(route7Point.size() - 1))
////                    .routeOptions(RouteOptions.builder().steps(true).coordinates(route7Point).profile(DirectionsCriteria.PROFILE_WALKING).baseUrl(BASE_API_URL).user(DirectionsCriteria.PROFILE_DEFAULT_USER).geometries(DirectionsCriteria.GEOMETRY_POLYLINE).accessToken(getString(R.string.access_token)).requestUuid("mapboxzero").build()).profile(DirectionsCriteria.PROFILE_DRIVING)
////                    .alternatives(false)
////                    .build();
////            Log.i(TAG, "onStyleLoaded: " + test3);
////
////            test3.getRoute(new Callback<DirectionsResponse>() {
////                @Override
////                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
////                    if (response.body() != null) {
////                        Log.i(TAG, "onResponse: " + response.body().routes().size());
////                        for (DirectionsRoute x :
////                                response.body().routes()) {
////                            navigationMapRoute.addRoute(x);
////                        }
////                    } else Log.i(TAG, "onResponse: " + response);
////                }
////
////                @Override
////                public void onFailure(Call<DirectionsResponse> call, Throwable t) {
////
////                }
////            });
////        }
////        map.easeCamera(newLatLngBounds(new LatLngBounds.Builder()
////                .include()
////                .include(route7Point.get(route7Point.size() - 1))
////                .build(), 75), 2000);
//
//        navigationMapRoute.updateRouteArrowVisibilityTo(true);
//
//
//
//
//        }


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
                            int i = 0;
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
                                    points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                                    stopss.add(new Stops(name, lat, lng, "front", routes));
                                    points.get(i).latitude();
                                    i++;

//
                                }
                            }
                        }
                    }
                    for (Symbol symbol : symbolArrayList) {

                        // symbol.setIconImage("XRed");
                        symbolManager.update(symbol);

                    }
                    initMap(savedInstanceState, stopss);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//            private void drawLines(@NonNull FeatureCollection featureCollection) {
//                Toast.makeText(getActivity(), "Draw Lines called", Toast.LENGTH_SHORT).show();
//                if (map != null) {
//                    map.getStyle(style -> {
//                        if (featureCollection.features() != null) {
//                            if (featureCollection.features().size() > 0) {
//                                style.addSource(new GeoJsonSource("line-source", featureCollection));
//
//// The layer properties for our line. This is where we make the line dotted, set the
//// color, etc.
//                                style.addLayer(new LineLayer("linelayer", "line-source")
//                                        .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
//                                                PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
//                                                PropertyFactory.lineOpacity(.7f),
//                                                PropertyFactory.lineWidth(7f),
//                                                PropertyFactory.lineColor(Color.parseColor("#3bb2d0"))));
//                            }
//                        }
//                    });
//                }
//                else{
//                    Toast.makeText(getActivity(), "No maps loaded", Toast.LENGTH_SHORT).show();
//                }
//            }
//            private static class LoadGeoJson extends AsyncTask<Void, Void, FeatureCollection> {
//
//                private WeakReference<MapFragment> weakReference;
//
//                LoadGeoJson(MapFragment activity) {
//                    this.weakReference = new WeakReference<>(activity);
//                    Toast.makeText(activity.getActivity(), "Load GeoJson Called", Toast.LENGTH_SHORT).show();
//                }
//
//                @SuppressLint("LongLogTag")
//                @Override
//                protected FeatureCollection doInBackground(Void... voids) {
//
//
//                    try {
//                        MapFragment activity = weakReference.get();
//                        if (activity != null) {
//                            InputStream inputStream = activity.getActivity().getAssets().open("example.geojson");
//                            Log.d("TAG","input Stream opened");
//                            return FeatureCollection.fromJson(convertStreamToString(inputStream));
//                        }
//                    } catch (Exception exception) {
//                       Log.d("Exception Loading GeoJSON:" , exception.toString());
//
//
//                    }
//                    return null;
//                }
//
//                static String convertStreamToString(InputStream is) {
//                    Scanner scanner = new Scanner(is).useDelimiter("\\A");
//                    Log.d("TAG","Convert String is successful");
//                    return scanner.hasNext() ? scanner.next() : "";
//                }
//
//                @Override
//                protected void onPostExecute(@Nullable FeatureCollection featureCollection) {
//                    super.onPostExecute(featureCollection);
//                    MapFragment activity = weakReference.get();
//                    if (activity != null && featureCollection != null) {
//                        activity.drawLines(featureCollection);
//                    }
//                }
//            }

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
