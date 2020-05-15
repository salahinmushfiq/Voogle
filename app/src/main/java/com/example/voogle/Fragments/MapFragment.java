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
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.voogle.Adapters.RouteButtonAdapter;
import com.example.voogle.Functions.MapClick;
import com.example.voogle.GlobalVariables;
import com.example.voogle.PojoClasses.Bus;
import com.example.voogle.PojoClasses.Location;
import com.example.voogle.PojoClasses.Stops;
import com.example.voogle.R;
import com.example.voogle.databinding.FragmentMapBinding;
import com.google.firebase.database.*;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.*;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, MapClick {
    final int[] count = {0};
    FragmentMapBinding fragmentMapBinding;
    ArrayList<GeoJsonSource> geoJsonSources = new ArrayList<>();
    ArrayList<Bus> busList = new ArrayList<>();
    MapboxMap map;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference("root");
    DatabaseReference stopRef, busRef;
    PermissionsManager permissionsManager;
    SymbolManager symbolManager;
    LatLngBounds bd;
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
    ArrayList<Long> routes;
    ArrayList<Stops> stopss;
    Bus currentBus;
    RouteButtonAdapter routeButtonAdapter;
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
    ArrayList<String> routeLayers = new ArrayList<>();
    ArrayList<Location> locations = new ArrayList<>();

    ArrayList<Integer> addLayerFlags = new ArrayList<>(80);

    int[] colors = new int[]{
            Color.parseColor("#0054A5"),
            Color.parseColor("#FF9400"),
            Color.parseColor("#e5e15e"),
            Color.parseColor("#00A650")
    };

    int[] stopColors = new int[]{
            Color.parseColor("#FF0000"),
            Color.parseColor("#00FF00"),
            Color.parseColor("#0000FF")
    };
    private Bundle savedInstanceState;
    private static final String LINE_GEOJSON_SOURCE_ID = "LINE_GEOJSON_SOURCE_ID";
    private static final String CIRCLE_GEOJSON_SOURCE_ID = "CIRCLE_GEOJSON_SOURCE_ID";
    ArrayList<LineLayer> routeLineLayers;
    ArrayList<SymbolLayer> stopLayers = new ArrayList<>();
    private boolean firstRun;
    private Location current_location;
    String routeNo;
    private int getCommonRoutesFlag;
    private long time1, time2;

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
        routeLineLayers = new ArrayList<>();
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


// Set the camera to the greatest possible zoom level that includes the
// bounds

        Log.i(TAG, "onCreateView: " + source + " and " + destination);
//        Toast.makeText(getActivity(), source, Toast.LENGTH_SHORT).show();

//        Toast.makeText(getActivity(), destination, Toast.LENGTH_SHORT).show();

        routeLineLayers.clear();
        routeLineLayers.add(new LineLayer("ROUTE1L", "ROUTE1"));
        routeLineLayers.add(new LineLayer("ROUTE2L", "ROUTE2"));
        routeLineLayers.add(new LineLayer("ROUTE3L", "ROUTE3"));
        routeLineLayers.add(new LineLayer("ROUTE4L", "ROUTE4"));
        routeLineLayers.add(new LineLayer("ROUTE5L", "ROUTE5"));
        routeLineLayers.add(new LineLayer("ROUTE6L", "ROUTE6"));
        routeLineLayers.add(new LineLayer("ROUTE7L", "ROUTE7"));
        routeLineLayers.add(new LineLayer("ROUTE8L", "ROUTE8"));
        routeLineLayers.add(new LineLayer("ROUTE9L", "ROUTE9"));
        routeLineLayers.add(new LineLayer("ROUTE10L", "ROUTE10"));
        routeLineLayers.add(new LineLayer("ROUTE11L", "ROUTE11"));
        routeLineLayers.add(new LineLayer("ROUTE12L", "ROUTE12"));
        routeLineLayers.add(new LineLayer("ROUTE13L", "ROUTE13"));
        routeLineLayers.add(new LineLayer("ROUTE14L", "ROUTE14"));
        routeLineLayers.add(new LineLayer("ROUTE15L", "ROUTE15"));
        routeLineLayers.add(new LineLayer("ROUTE16L", "ROUTE16"));
        routeLineLayers.add(new LineLayer("ROUTE17L", "ROUTE17"));
        routeLineLayers.add(new LineLayer("ROUTE18L", "ROUTE18"));
        routeLineLayers.add(new LineLayer("ROUTE19L", "ROUTE19"));
        routeLineLayers.add(new LineLayer("ROUTE20L", "ROUTE20"));
        routeLineLayers.add(new LineLayer("ROUTE21L", "ROUTE21"));
        routeLineLayers.add(new LineLayer("ROUTE22L", "ROUTE22"));
        routeLineLayers.add(new LineLayer("ROUTE23L", "ROUTE23"));
        routeLineLayers.add(new LineLayer("ROUTE24L", "ROUTE24"));
        routeLineLayers.add(new LineLayer("ROUTE25L", "ROUTE25"));
        routeLineLayers.add(new LineLayer("ROUTE26L", "ROUTE26"));
        routeLineLayers.add(new LineLayer("ROUTE27L", "ROUTE27"));
        routeLineLayers.add(new LineLayer("ROUTE28L", "ROUTE28"));
        routeLineLayers.add(new LineLayer("ROUTE29L", "ROUTE29"));
        routeLineLayers.add(new LineLayer("ROUTE30L", "ROUTE30"));
        routeLineLayers.add(new LineLayer("ROUTE31L", "ROUTE31"));
        routeLineLayers.add(new LineLayer("ROUTE32L", "ROUTE32"));
        routeLineLayers.add(new LineLayer("ROUTE33L", "ROUTE33"));
        routeLineLayers.add(new LineLayer("ROUTE34L", "ROUTE34"));
        routeLineLayers.add(new LineLayer("ROUTE35L", "ROUTE35"));
        routeLineLayers.add(new LineLayer("ROUTE36L", "ROUTE36"));
        routeLineLayers.add(new LineLayer("ROUTE37L", "ROUTE37"));
        routeLineLayers.add(new LineLayer("ROUTE38L", "ROUTE38"));
        routeLineLayers.add(new LineLayer("ROUTE39L", "ROUTE39"));
        routeLineLayers.add(new LineLayer("ROUTE40L", "ROUTE40"));
        routeLineLayers.add(new LineLayer("ROUTE41L", "ROUTE41"));
        routeLineLayers.add(new LineLayer("ROUTE42L", "ROUTE42"));
        routeLineLayers.add(new LineLayer("ROUTE43L", "ROUTE43"));
        routeLineLayers.add(new LineLayer("ROUTE44L", "ROUTE44"));
        routeLineLayers.add(new LineLayer("ROUTE45L", "ROUTE45"));
        routeLineLayers.add(new LineLayer("ROUTE46L", "ROUTE46"));
        routeLineLayers.add(new LineLayer("ROUTE47L", "ROUTE47"));
        routeLineLayers.add(new LineLayer("ROUTE48L", "ROUTE48"));
        routeLineLayers.add(new LineLayer("ROUTE49L", "ROUTE49"));
        routeLineLayers.add(new LineLayer("ROUTE50L", "ROUTE50"));
        routeLineLayers.add(new LineLayer("ROUTE51L", "ROUTE51"));
        routeLineLayers.add(new LineLayer("ROUTE52L", "ROUTE52"));
        routeLineLayers.add(new LineLayer("ROUTE53L", "ROUTE53"));
        routeLineLayers.add(new LineLayer("ROUTE54L", "ROUTE54"));
        routeLineLayers.add(new LineLayer("ROUTE55L", "ROUTE55"));
        routeLineLayers.add(new LineLayer("ROUTE56L", "ROUTE56"));
        routeLineLayers.add(new LineLayer("ROUTE57L", "ROUTE57"));
        routeLineLayers.add(new LineLayer("ROUTE58L", "ROUTE58"));
        routeLineLayers.add(new LineLayer("ROUTE59L", "ROUTE59"));
        routeLineLayers.add(new LineLayer("ROUTE60L", "ROUTE60"));
        routeLineLayers.add(new LineLayer("ROUTE61L", "ROUTE61"));
        routeLineLayers.add(new LineLayer("ROUTE62L", "ROUTE62"));
        routeLineLayers.add(new LineLayer("ROUTE63L", "ROUTE63"));
        routeLineLayers.add(new LineLayer("ROUTE64L", "ROUTE64"));
        routeLineLayers.add(new LineLayer("ROUTE65L", "ROUTE65"));
        routeLineLayers.add(new LineLayer("ROUTE66L", "ROUTE66"));
        routeLineLayers.add(new LineLayer("ROUTE67L", "ROUTE67"));
        routeLineLayers.add(new LineLayer("ROUTE68L", "ROUTE68"));
        routeLineLayers.add(new LineLayer("ROUTE69L", "ROUTE69"));
        routeLineLayers.add(new LineLayer("ROUTE70L", "ROUTE70"));
        routeLineLayers.add(new LineLayer("ROUTE71L", "ROUTE71"));
        routeLineLayers.add(new LineLayer("ROUTE72L", "ROUTE72"));
        routeLineLayers.add(new LineLayer("ROUTE73L", "ROUTE73"));
        routeLineLayers.add(new LineLayer("ROUTE74L", "ROUTE74"));
        routeLineLayers.add(new LineLayer("ROUTE75L", "ROUTE75"));
        routeLineLayers.add(new LineLayer("ROUTE76L", "ROUTE76"));
        routeLineLayers.add(new LineLayer("ROUTE77L", "ROUTE77"));
        routeLineLayers.add(new LineLayer("ROUTE78L", "ROUTE78"));
        routeLineLayers.add(new LineLayer("ROUTE79L", "ROUTE79"));
        routeLineLayers.add(new LineLayer("ROUTE80L", "ROUTE80"));

        time1 = System.currentTimeMillis();
        initMap(savedInstanceState);

        fragmentMapBinding.removeLayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLayers();
            }
        });
        getCommonRoutesFlag = 0;

//        readLocations();
        return fragmentMapBinding.getRoot();

    }

    private void getBusList() {
        currentBus = new Bus();
        busRef = FirebaseDatabase.getInstance().getReference().child("root").child("busList");
        busRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot bus : dataSnapshot.getChildren()) {
//                        for (DataSnapshot route : stops.child("route").getChildren()) {
//
                        currentBus = bus.getValue(Bus.class);
                        for (int route : commonRoutes) {
                            if (route == currentBus.getRoute_no()) {
                                busList.add(currentBus);
                            }
                        }
//                        }
                        // Log.d("busLoad",bus.getValue().toString());
                        //Toast.makeText(getActivity(), currentBus.getGroupName(), Toast.LENGTH_SHORT).show();
                    }
                    if (!busList.isEmpty()) {
                        routeButtonAdapter = new RouteButtonAdapter(getActivity(), busList, MapFragment.this);
                        fragmentMapBinding.routeBtnRV.setAdapter(routeButtonAdapter);
                        fragmentMapBinding.routeBtnRV.setLayoutManager(new LinearLayoutManager(getActivity()));
                        for (Bus bus : busList) {
                            Log.d("BusList", bus.getGroupName());
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initMap(Bundle savedInstanceState) {
//        LatLng source = new LatLng(Double.valueOf(sourceLat), Double.valueOf(sourceLng)), destination = new LatLng(Double.valueOf(destinationLat), Double.valueOf(destinationLng));
        mapView = fragmentMapBinding.mapView;

        Log.d("check", "initMap");
        mapView.onCreate(savedInstanceState);

        geocoder = new Geocoder(getContext());

        fragmentMapBinding.navView.initialize(isRunning -> {

        });

        mapView.getMapAsync(mapboxMap -> {
            map = mapboxMap;
            fragmentMapBinding.navView.onMapReady(map);
            map.setStyle(new Style.Builder().fromUri(/*"mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"*/ /*"mapbox://styles/mapbox/light-v10"*/ "mapbox://styles/mapbox/navigation-preview-day-v4")
//            map.setStyle(new Style.Builder().fromUri(/*"mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"*/ /*"mapbox://styles/mapbox/light-v10"*/ "mapbox://styles/mushfimaqverick/ck9woofn10nb91isalygf97sy")
                            .withImage("X", getActivity().getDrawable(R.drawable.ic_location_on_black_24dp))
                            .withImage("Y", getActivity().getDrawable(R.drawable.ic_location_on_red_24dp))
                            .withImage("LocationPointer", getActivity().getDrawable(R.drawable.ic_person_pin_circle_yellow_24dp))
                            .withImage("ROUTE1", getActivity().getDrawable(R.drawable.ic_directions_bus_green_24dp))
                            .withImage("ROUTE2", getActivity().getDrawable(R.drawable.ic_directions_bus_blue_24dp))
                            .withImage("ROUTE3", getActivity().getDrawable(R.drawable.ic_directions_bus_red_24dp))
                    , style -> {

                        LineLayer lineLayerX = new LineLayer("x", "x");
                        style.addLayer(lineLayerX);
                        symbolManager = new SymbolManager(mapView, mapboxMap, style, "x");
                        LineLayer lastLineLayer = new LineLayer("mapbox-android-symbol-layer-0", "x");
                        for (LineLayer lineLayer :
                                routeLineLayers) {
                            style.addLayerBelow(lineLayer, lastLineLayer.getId());
                            lastLineLayer = lineLayer;
                        }

                        readLocations();

                        UiSettings uiSettings = mapboxMap.getUiSettings();
                        uiSettings.setZoomGesturesEnabled(true);
                        uiSettings.setQuickZoomGesturesEnabled(true);
                        uiSettings.setCompassEnabled(true);

                        getCommonRoutes();
                        getBusList();
                    });

        });
    }

    private void addAStop(Stops stop) {

        String stopSource = generateStopString(stop, false);
        String stopLayer = generateStopString(stop, true);

        getActivity().runOnUiThread(() -> {
            map.getStyle(style -> {
                if (style.getSource(stopSource) != null && style.getLayer(stopLayer) != null) return;

                if (style.getSource(stopSource) == null)
                    style.addSource(
                            new GeoJsonSource(
                                    stopSource,
                                    Feature.fromGeometry(
                                            Point.fromLngLat(
                                                    stop.getLng(),
                                                    stop.getLat()))));

                SymbolLayer symbolLayer = new SymbolLayer(stopLayer, stopSource).withProperties(
                        PropertyFactory.iconColor(colors[new Random().nextInt(3)]),
                        PropertyFactory.iconImage("ROUTE1"),
                        PropertyFactory.textColor("#E2000F"),
                        PropertyFactory.textHaloColor("#000000"),
                        PropertyFactory.textHaloWidth(0.5f),
                        PropertyFactory.textSize(15f),
                        PropertyFactory.textOffset(new Float[]{0.0f, 3.0f}),
                        PropertyFactory.textField(stop.getName()));
                style.addLayer(symbolLayer);
                stopLayers.add(symbolLayer);
            });

        });
    }

    private String generateStopString(Stops stop, boolean layer) {
        StringBuilder returner = new StringBuilder("STOP");
        for (String id :
                stop.getRoutes()) {
            returner
                    .append("-")
                    .append(id)
                    .append(layer ? "L" : "S");
        }
        return returner.toString();
    }

    private void addStops(ArrayList<Stops> stops) {
        getActivity().runOnUiThread(() -> {
            Symbol source = symbolManager.create(new SymbolOptions().withIconImage("X").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng(sourceLat, sourceLng)).withTextField("Source").withSymbolSortKey(83f));
            Symbol destination = symbolManager.create(new SymbolOptions().withIconImage("Y").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng(destinationLat, destinationLng)).withTextField("Destination").withSymbolSortKey(82f));
            symbolArrayList.add(source);
            symbolArrayList.add(destination);

            for (Symbol symbol : symbolArrayList) {
                symbolManager.update(symbol);
            }
        });

        for (int commonRoute : commonRoutes) {
            for (Stops stop : stops) {
                Log.i(TAG, "addStops: " + stop.getRoutes());
                Log.i(TAG, "addStopsX: " + commonRoute);
                if (stop.getRoutes().contains(String.valueOf(commonRoute)))
                    addAStop(stop);
            }
        }
        getActivity().runOnUiThread(() -> map.getStyle(style -> {


//                            CameraPosition position = new CameraPosition.Builder()
//                                    .target(new LatLng(sourceLat, sourceLng
//                                    )) // Sets the new camera position
//                                    .zoom(14) // Sets the zoom
//                                    .tilt(30) // Set the camera tilt
//                                    .build(); // Creates a CameraPosition from the builder
            locationComponent = map.getLocationComponent();

            locationComponent.activateLocationComponent(locationComponentActivationOptions = LocationComponentActivationOptions.builder(getActivity(), style).build());


//                            map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);

            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(new LatLng(sourceLat, sourceLng)) // Northeast
                    .include(new LatLng(destinationLat, destinationLng)) // Southwest
                    .build();

            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);


            loadRoute(style);
            Log.d("check", "OnStyle loaded");
            Toast.makeText(getContext(),
                    String.format("Time Taken %d seconds", (System.currentTimeMillis() - time1) / 1000),
                    Toast.LENGTH_LONG).show();
            fragmentMapBinding.progressBar.setVisibility(View.GONE);
        }));
    }

    private void loadRoute(Style style) {
        GeoJsonSource geoJsonSource = style.getSourceAs("k");

        int i = 0;
        for (int commonRoute : commonRoutes) {
            //Toast.makeText(getActivity(), "Common Routessss: "+commonRoute, Toast.LENGTH_SHORT).show();
            Log.d("route", "Common Routessss: " + commonRoute);
//            GeoJsonSource geoJsonSource = new GeoJsonSource("ROUTE" + commonRoute);

            try {
                URI uri = new URI("asset://route" + commonRoute + ".geojson");
                Log.i(TAG, "onStyleLoaded: " + uri);
                if (style.getSource("ROUTE" + commonRoute) == null)
                    style.addSource(new GeoJsonSource("ROUTE" + commonRoute, uri));
                else {
                    Log.i(TAG, "loadRoute: Source existed");
                    if (style.removeSource("ROUTE" + commonRoute))
                        style.addSource(new GeoJsonSource("ROUTE" + commonRoute, uri));

                }
            } catch (NullPointerException | URISyntaxException e) {
                e.printStackTrace();
            }

//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
            /*if(i==0) {
                route.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#0054A5")));
            }
            if(i==1)
            {
                route.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#EE1D23")));
            }
            if(i==2)
            {
                route.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#66CBC5")));
            }
            if(i==3)
            {
                route.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#66CBC5")));
            }
            if(i==4)
            {
                route.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#66CBC5")));
            }
            if(i==5)
            {
                route.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#66CBC5")));
            }
            if(i==6)
            {
                route.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#66CBC5")));
            }*/
            i++;


        }
        for (Source source :
                style.getSources()) {
            Log.i(TAG, "loadRoute: " + source.getId());
        }


        setProperties(commonRoutes);
    }

    private void setProperties(ArrayList<Integer> indexes) {
        for (Integer x :
                indexes) {
            if (map.getStyle() != null) {
                if (map.getStyle().getLayer(routeLineLayers.get(x).getId()) != null)
                    map.getStyle().getLayer(routeLineLayers.get(x).getId()).setProperties(
                            PropertyFactory.visibility(Property.VISIBLE),
                            PropertyFactory.fillOutlineColor(Color.RED),
                            PropertyFactory.fillOpacity(0.7f),
                            PropertyFactory.lineWidth(6.23f),
                            PropertyFactory.lineColor(colors[new Random().nextInt(4)]));
                int colorIndex = stopColors[new Random().nextInt(3)];
                for (Stops stops : stopss) {
                    if (stops.getRoutes().contains(String.valueOf(x))
                            && map.getStyle().getLayer("STOPL" + x) != null)
                        map.getStyle()
                                .getLayer("STOPL" + x)
                                .setProperties(
                                        PropertyFactory.visibility(Property.VISIBLE),
                                        PropertyFactory.iconColor(colorIndex)
                                );
                }

            }
        }
    }

    private void setProperties(int index) {
        Log.i(TAG, "setProperties: " + index);
        if (map.getStyle() != null) {
            if (map.getStyle().getLayer(routeLineLayers.get(index).getId()) != null)
                map.getStyle().getLayer(routeLineLayers.get(index).getId()).setProperties(
                        PropertyFactory.visibility(Property.VISIBLE),
                        PropertyFactory.lineOpacity(0.7f),
                        PropertyFactory.lineWidth(6.33f),
                        PropertyFactory.lineColor(colors[new Random().nextInt(4)]));
            else Toast.makeText(getContext(), "null routelayer at index " + index, Toast.LENGTH_SHORT).show();

            int colorIndex1 = stopColors[new Random().nextInt(3)];
            for (SymbolLayer layer :
                    stopLayers) {
                Log.i(TAG, "setProperties: " + layer.getTextField().value.getFormattedSections()[0].getText() + " " + layer.getId());
                if (layer.getId().contains("-" + index + "L"))
                    if (map.getStyle().getLayer(layer.getId()) != null)
                        map.getStyle().getLayer(layer.getId()).setProperties(
                                PropertyFactory.visibility(Property.VISIBLE),
                                PropertyFactory.lineColor(colorIndex1));
            }
        } else Toast.makeText(getContext(), "null style at index " + index, Toast.LENGTH_SHORT).show();
    }

    private void readLocations() {


//        locationPointer = symbolManager.create(new SymbolOptions().withIconImage("LocationPointer").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000")
//                .withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng(Double.valueOf(23.778628), Double.valueOf(90.365235))).withTextField("License Plate"));
//        symbolManager.update(locationPointer);
        current_location = new Location();
        firstRun = true;
        root.child("locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot location : dataSnapshot.getChildren()) {
                    locations.add(location.getValue(Location.class));
                    current_location = location.getValue(Location.class);
//                    Toast.makeText(getActivity(), "Lat: " + current_location.getLat() + " Lng: " + current_location.getLng() +
//                            " License Plate: " + current_location.getLicensePlate(), Toast.LENGTH_SHORT).show();

//                    if(locationPointer==null) {
                    Log.d("map", "called");
                    if (firstRun) {
                        locationPointer = symbolManager.create(new SymbolOptions().withIconImage("LocationPointer").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000")
                                .withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng(Double.valueOf(current_location.getLat()), Double.valueOf(current_location.getLng()))).withTextField(current_location.getLicensePlate()));
                        symbolArrayList.add(locationPointer);
                    } else {
                        for (Symbol symbol : symbolArrayList) {
                            if (symbol.getTextField().equals(current_location.getLicensePlate())) {
                                symbol.setLatLng(new LatLng(current_location.getLat(), current_location.getLng()));
                                symbolManager.update(symbol);
                            }
                        }
                    }

                    // symbolManager.update(locationPointer);
//                    }

                    //locationPointer.setLatLng(new LatLng(current_location[0].getLat(), current_location[0].getLng()));
                    // symbolManager.update(locationPointer);


                }
                /*for(Symbol symbol:symbolArrayList)
                {
                    symbolManager.update(symbol);
                }*/

                firstRun = false;

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
        sourceRoutes.clear();
        commonRoutes.clear();
        for (Integer sourceRoute : GlobalVariables.sourceRoutes) {
            sourceRoutes.add(sourceRoute);
            for (Integer destinationRoute : GlobalVariables.destinationRoutes) {
                if (sourceRoute.equals(destinationRoute)) {

                    commonRoutes.add(sourceRoute);

                }
            }

        }

//        Toast.makeText(getActivity(), "Source Route Map:" + sourceRoutes.toString(), Toast.LENGTH_SHORT).show();
        destinationRoutes.addAll(GlobalVariables.destinationRoutes);
//        Toast.makeText(getActivity(), "Destination Route Map:" + destinationRoutes.toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(), "Common Routes: " + commonRoutes.toString(), Toast.LENGTH_SHORT).show();

        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("stops");
        stopRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    new Thread(() -> {
                        for (DataSnapshot stops : dataSnapshot.getChildren()) {
                            GlobalVariables.stops.add(stops.getValue(Stops.class));
                            for (DataSnapshot route : stops.child("route").getChildren()) {
                                int i = 0;
                                for (int commonRoute : commonRoutes) {

                                    if (commonRoute == Integer.parseInt(route.getValue().toString())) {
//                                    GlobalVariables.stops.add(stops.getValue(Stops.class));
                                        // Toast.makeText(getActivity(), stops.getValue(Stops.class).toString(), Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(getActivity(), stops.child("lat").getValue().toString(), Toast.LENGTH_SHORT).show();
                                        // Toast.makeText(getActivity(), stops.child("lng").getValue().toString(), Toast.LENGTH_SHORT).show();
                                        lat = stops.child("lat").getValue(Double.class);
                                        lng = stops.child("lng").getValue(Double.class);
                                        name = stops.child("name").getValue(String.class);
                                        routes = (ArrayList<Long>) stops.child("route").getValue();
                                        points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                                        stopss.add(new Stops(name, lat, lng, "front", routes));
                                        points.get(i).latitude();
                                        i++;
                                    }
                                }
                            }
                            Log.d("check", "Datasnapshot");
                        }
                    /*for (Symbol symbol : symbolArrayList) {
                        symbolManager.update(symbol)
                    }*/
                        getCommonRoutesFlag = 1;
                        addStops(stopss);
                    }).start();


                    //  initMap(savedInstanceState, stopss);
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

    private void hideLayers() {
        for (LineLayer layer :
                routeLineLayers) {
            if (map.getStyle() != null && map.getStyle().getLayer(layer.getId()) != null)
                map.getStyle().getLayer(layer.getId()).setProperties(PropertyFactory.visibility(Property.NONE));
        }
        for (SymbolLayer layer :
                stopLayers) {
            if (map.getStyle() != null && map.getStyle().getLayer(layer.getId()) != null)
                map.getStyle().getLayer(layer.getId()).setProperties(PropertyFactory.visibility(Property.NONE));
        }
    }

    @Override
    public void onClick(String s) {
        routeNo = s;
        Toast.makeText(getActivity(), "Route No.: " + s, Toast.LENGTH_SHORT).show();
        if (!commonRoutes.isEmpty()) {
            hideLayers();
            setProperties(Integer.parseInt(s));
        }
    }
}
