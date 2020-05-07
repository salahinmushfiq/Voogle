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
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
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
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.camera.NavigationCamera;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, MapClick {
    final int[] count = {0};
    FragmentMapBinding fragmentMapBinding;
    ArrayList<GeoJsonSource>geoJsonSources=new ArrayList<>();
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
    ArrayList<String>routes;
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
    int addLayer1Flag, addLayer2Flag, addLayer3Flag, addLayer4Flag, addLayer5Flag, addLayer6Flag, addLayer7Flag,
            addLayer8Flag, addLayer9Flag, addLayer10Flag, addLayer11Flag, addLayer12Flag, addLayer13Flag, addLayer14Flag,
            addLayer15Flag, addLayer16Flag, addLayer17Flag, addLayer18Flag, addLayer19Flag, addLayer20Flag, addLayer21Flag,
            addLayer22Flag, addLayer23Flag, addLayer24Flag, addLayer25Flag, addLayer26Flag, addLayer27Flag, addLayer28Flag,
            addLayer29Flag, addLayer30Flag, addLayer31Flag, addLayer32Flag, addLayer33Flag, addLayer34Flag, addLayer35Flag,
            addLayer36Flag, addLayer37Flag, addLayer38Flag, addLayer39Flag, addLayer40Flag, addLayer41Flag, addLayer42Flag,
            addLayer43Flag, addLayer44Flag, addLayer45Flag, addLayer46Flag, addLayer47Flag, addLayer48Flag, addLayer49Flag,
            addLayer50Flag, addLayer51Flag, addLayer52Flag, addLayer53Flag, addLayer54Flag, addLayer55Flag, addLayer56Flag,
            addLayer57Flag, addLayer58Flag, addLayer59Flag, addLayer60Flag, addLayer61Flag, addLayer62Flag, addLayer63Flag,
            addLayer64Flag, addLayer65Flag, addLayer66Flag, addLayer67Flag, addLayer68Flag, addLayer69Flag, addLayer70Flag,
            addLayer71Flag, addLayer72Flag, addLayer73Flag, addLayer74Flag, addLayer75Flag, addLayer76Flag, addLayer77Flag,
            addLayer78Flag, addLayer79Flag, addLayer80Flag;
    private Bundle savedInstanceState;
    private static final String LINE_GEOJSON_SOURCE_ID = "LINE_GEOJSON_SOURCE_ID";
    private static final String CIRCLE_GEOJSON_SOURCE_ID = "CIRCLE_GEOJSON_SOURCE_ID";
    LineLayer route,route1, route2, route3, route4, route5, route6, route7, route8, route9, route10, route11, route12, route13, route14, route15, route16, route17, route18, route19, route20, route21, route22, route23, route24, route25, route26, route27, route28, route29, route30, route31, route32, route33, route34, route35, route36, route37, route38, route39, route40, route41, route42, route43, route44, route45, route46, route47, route48, route49, route50, route51, route52, route53, route54, route55, route56, route57, route58, route59, route60, route61, route62, route63, route64, route65, route66, route67, route68, route69, route70, route71, route72, route73, route74, route75, route76, route77, route78, route79, route80;
    private boolean firstRun;
    private Location current_location;
    String routeNo;
    private int getCommonRoutesFlag;
    private long time1st;

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




// Set the camera to the greatest possible zoom level that includes the
// bounds

        Log.i(TAG, "onCreateView: " + source + " and " + destination);
        Toast.makeText(getActivity(), source, Toast.LENGTH_SHORT).show();

        Toast.makeText(getActivity(), destination, Toast.LENGTH_SHORT).show();


        initMap(savedInstanceState);

        fragmentMapBinding.removeLayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addLayer1Flag == 1) {
                    map.getStyle().removeLayer(route1);
                    addLayer1Flag = 0;
                }
                if (addLayer2Flag == 1) {
                    map.getStyle().removeLayer(route2);
                    addLayer2Flag = 0;
                }
                if (addLayer3Flag == 1) {
                    map.getStyle().removeLayer(route3);
                    addLayer3Flag = 0;
                }
                if (addLayer4Flag == 1) {
                    map.getStyle().removeLayer(route4);
                    addLayer4Flag = 0;
                }
                if (addLayer5Flag == 1) {
                    map.getStyle().removeLayer(route5);
                    addLayer5Flag = 0;
                }
                if (addLayer6Flag == 1) {
                    map.getStyle().removeLayer(route6);
                    addLayer6Flag = 0;
                }
                if (addLayer7Flag == 1) {
                    map.getStyle().removeLayer(route7);
                    addLayer7Flag = 0;
                }
                if (addLayer8Flag == 1) {
                    map.getStyle().removeLayer(route8);
                    addLayer8Flag = 0;
                }
                if (addLayer9Flag == 1) {
                    map.getStyle().removeLayer(route9);
                    addLayer9Flag = 0;
                }
                if (addLayer10Flag == 1) {
                    map.getStyle().removeLayer(route10);
                    addLayer10Flag = 0;
                }
                if (addLayer11Flag == 1) {
                    map.getStyle().removeLayer(route11);
                    addLayer11Flag = 0;
                }
                if (addLayer12Flag == 1) {
                    map.getStyle().removeLayer(route12);
                    addLayer12Flag = 0;
                }
                if (addLayer13Flag == 1) {
                    map.getStyle().removeLayer(route13);
                    addLayer13Flag = 0;
                }
                if (addLayer14Flag == 1) {
                    map.getStyle().removeLayer(route14);
                    addLayer14Flag = 0;
                }
                if (addLayer15Flag == 1) {
                    map.getStyle().removeLayer(route15);
                    addLayer15Flag = 0;
                }
                if (addLayer16Flag == 1) {
                    map.getStyle().removeLayer(route16);
                    addLayer16Flag = 0;
                }
                if (addLayer17Flag == 1) {
                    map.getStyle().removeLayer(route17);
                    addLayer17Flag = 0;
                }
                if (addLayer18Flag == 1) {
                    map.getStyle().removeLayer(route18);
                    addLayer18Flag = 0;
                }
                if (addLayer19Flag == 1) {
                    map.getStyle().removeLayer(route19);
                    addLayer19Flag = 0;
                }
                if (addLayer20Flag == 1) {
                    map.getStyle().removeLayer(route20);
                    addLayer20Flag = 0;
                }
                if (addLayer21Flag == 1) {
                    map.getStyle().removeLayer(route21);
                    addLayer21Flag = 0;
                }
                if (addLayer22Flag == 1) {
                    map.getStyle().removeLayer(route22);
                    addLayer22Flag = 0;
                }
                if (addLayer23Flag == 1) {
                    map.getStyle().removeLayer(route23);
                    addLayer23Flag = 0;
                }
                if (addLayer24Flag == 1) {
                    map.getStyle().removeLayer(route24);
                    addLayer24Flag = 0;
                }
                if (addLayer25Flag == 1) {
                    map.getStyle().removeLayer(route25);
                    addLayer25Flag = 0;
                }
                if (addLayer26Flag == 1) {
                    map.getStyle().removeLayer(route26);
                    addLayer26Flag = 0;
                }
                if (addLayer27Flag == 1) {
                    map.getStyle().removeLayer(route27);
                    addLayer27Flag = 0;
                }
                if (addLayer28Flag == 1) {
                    map.getStyle().removeLayer(route28);
                    addLayer28Flag = 0;
                }
                if (addLayer29Flag == 1) {
                    map.getStyle().removeLayer(route29);
                    addLayer29Flag = 0;
                }
                if (addLayer30Flag == 1) {
                    map.getStyle().removeLayer(route30);
                    addLayer30Flag = 0;
                }
                if (addLayer31Flag == 1) {
                    map.getStyle().removeLayer(route31);
                    addLayer31Flag = 0;
                }
                if (addLayer32Flag == 1) {
                    map.getStyle().removeLayer(route32);
                    addLayer32Flag = 0;
                }
                if (addLayer33Flag == 1) {
                    map.getStyle().removeLayer(route33);
                    addLayer33Flag = 0;
                }
                if (addLayer34Flag == 1) {
                    map.getStyle().removeLayer(route34);
                    addLayer34Flag = 0;
                }
                if (addLayer35Flag == 1) {
                    map.getStyle().removeLayer(route35);
                    addLayer35Flag = 0;
                }
                if (addLayer36Flag == 1) {
                    map.getStyle().removeLayer(route36);
                    addLayer36Flag = 0;
                }
                if (addLayer37Flag == 1) {
                    map.getStyle().removeLayer(route37);
                    addLayer37Flag = 0;
                }
                if (addLayer38Flag == 1) {
                    map.getStyle().removeLayer(route38);
                    addLayer38Flag = 0;
                }
                if (addLayer39Flag == 1) {
                    map.getStyle().removeLayer(route39);
                    addLayer39Flag = 0;
                }
                if (addLayer40Flag == 1) {
                    map.getStyle().removeLayer(route40);
                    addLayer40Flag = 0;
                }
                if (addLayer41Flag == 1) {
                    map.getStyle().removeLayer(route41);
                    addLayer41Flag = 0;
                }
                if (addLayer42Flag == 1) {
                    map.getStyle().removeLayer(route42);
                    addLayer42Flag = 0;
                }
                if (addLayer43Flag == 1) {
                    map.getStyle().removeLayer(route43);
                    addLayer43Flag = 0;
                }
                if (addLayer44Flag == 1) {
                    map.getStyle().removeLayer(route44);
                    addLayer44Flag = 0;
                }
                if (addLayer45Flag == 1) {
                    map.getStyle().removeLayer(route45);
                    addLayer45Flag = 0;
                }
                if (addLayer46Flag == 1) {
                    map.getStyle().removeLayer(route46);
                    addLayer46Flag = 0;
                }
                if (addLayer47Flag == 1) {
                    map.getStyle().removeLayer(route47);
                    addLayer47Flag = 0;
                }
                if (addLayer48Flag == 1) {
                    map.getStyle().removeLayer(route48);
                    addLayer48Flag = 0;
                }
                if (addLayer49Flag == 1) {
                    map.getStyle().removeLayer(route49);
                    addLayer49Flag = 0;
                }
                if (addLayer50Flag == 1) {
                    map.getStyle().removeLayer(route50);
                    addLayer50Flag = 0;
                }
                if (addLayer51Flag == 1) {
                    map.getStyle().removeLayer(route51);
                    addLayer51Flag = 0;
                }
                if (addLayer52Flag == 1) {
                    map.getStyle().removeLayer(route52);
                    addLayer52Flag = 0;
                }
                if (addLayer53Flag == 1) {
                    map.getStyle().removeLayer(route53);
                    addLayer53Flag = 0;
                }
                if (addLayer54Flag == 1) {
                    map.getStyle().removeLayer(route54);
                    addLayer54Flag = 0;
                }
                if (addLayer55Flag == 1) {
                    map.getStyle().removeLayer(route55);
                    addLayer55Flag = 0;
                }
                if (addLayer56Flag == 1) {
                    map.getStyle().removeLayer(route56);
                    addLayer56Flag = 0;
                }
                if (addLayer57Flag == 1) {
                    map.getStyle().removeLayer(route57);
                    addLayer57Flag = 0;
                }
                if (addLayer58Flag == 1) {
                    map.getStyle().removeLayer(route58);
                    addLayer58Flag = 0;
                }
                if (addLayer59Flag == 1) {
                    map.getStyle().removeLayer(route59);
                    addLayer9Flag = 0;
                }
                if (addLayer60Flag == 1) {
                    map.getStyle().removeLayer(route10);
                    addLayer10Flag = 0;
                }
                if (addLayer61Flag == 1) {
                    map.getStyle().removeLayer(route61);
                    addLayer61Flag = 0;
                }
                if (addLayer62Flag == 1) {
                    map.getStyle().removeLayer(route62);
                    addLayer62Flag = 0;
                }
                if (addLayer63Flag == 1) {
                    map.getStyle().removeLayer(route63);
                    addLayer63Flag = 0;
                }
                if (addLayer64Flag == 1) {
                    map.getStyle().removeLayer(route64);
                    addLayer64Flag = 0;
                }
                if (addLayer65Flag == 1) {
                    map.getStyle().removeLayer(route65);
                    addLayer65Flag = 0;
                }
                if (addLayer66Flag == 1) {
                    map.getStyle().removeLayer(route66);
                    addLayer66Flag = 0;
                }
                if (addLayer67Flag == 1) {
                    map.getStyle().removeLayer(route67);
                    addLayer67Flag = 0;
                }
                if (addLayer68Flag == 1) {
                    map.getStyle().removeLayer(route68);
                    addLayer68Flag = 0;
                }
                if (addLayer69Flag == 1) {
                    map.getStyle().removeLayer(route69);
                    addLayer69Flag = 0;
                }
                if (addLayer70Flag == 1) {
                    map.getStyle().removeLayer(route70);
                    addLayer70Flag = 0;
                }
            }
        });
        getCommonRoutesFlag = 0;

//        readLocations();
        return fragmentMapBinding.getRoot();

    }

    private void getBusList() {
        currentBus = new Bus();
        busRef = FirebaseDatabase.getInstance().getReference().child("root").child("busList");
        busRef.addValueEventListener(new ValueEventListener() {
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

        time1st = System.currentTimeMillis();

        Log.i(TAG, "initMap: " + time1st);

        Log.d("check", "initMap");
        mapView.onCreate(savedInstanceState);

        geocoder = new Geocoder(getContext());

        fragmentMapBinding.navView.initialize(isRunning -> {

        });

        mapView.getMapAsync(mapboxMap -> {
            map = mapboxMap;
            fragmentMapBinding.navView.onMapReady(map);
       //     map.setStyle(new Style.Builder().fromUri(/*"mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"*/ /*"mapbox://styles/mapbox/light-v10"*/ "mapbox://styles/mapbox/navigation-preview-day-v4")
            map.setStyle(new Style.Builder().fromUri(/*"mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"*/ /*"mapbox://styles/mapbox/light-v10"*/ "mapbox://styles/mushfimaqverick/ck9woofn10nb91isalygf97sy")
                    .withImage("X", getActivity().getDrawable(R.drawable.ic_location_on_black_24dp))
                    .withImage("Y", getActivity().getDrawable(R.drawable.ic_location_on_red_24dp))
                    .withImage("LocationPointer", getActivity().getDrawable(R.drawable.ic_person_pin_circle_yellow_24dp))
                    .withImage("ROUTE1", getActivity().getDrawable(R.drawable.ic_directions_bus_yellow_24dp))
                    .withImage("ROUTE2", getActivity().getDrawable(R.drawable.ic_directions_bus_blue_24dp))
                    .withImage("ROUTE3", getActivity().getDrawable(R.drawable.ic_directions_bus_red_24dp))
                , style -> {


                Log.i(TAG, "initMap onLoaded: " + (System.currentTimeMillis() - time1st));

                symbolManager = new SymbolManager(mapView, mapboxMap, style);

                readLocations();

                UiSettings uiSettings = mapboxMap.getUiSettings();
                uiSettings.setZoomGesturesEnabled(true);
                uiSettings.setQuickZoomGesturesEnabled(true);
                uiSettings.setCompassEnabled(true);


                getCommonRoutes();
                getBusList();



                addLayer1Flag = 0;
                addLayer2Flag = 0;
                addLayer3Flag = 0;
                addLayer4Flag = 0;
                addLayer5Flag = 0;
                addLayer6Flag = 0;
                addLayer7Flag = 0;
            });

        });
    }

    private void addStop(ArrayList<Stops> stops) {
        Symbol source = symbolManager.create(new SymbolOptions().withIconImage("X").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng(Double.valueOf(sourceLat), Double.valueOf(sourceLng))).withTextField("Source"));
        Symbol destination = symbolManager.create(new SymbolOptions().withIconImage("Y").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng(Double.valueOf(destinationLat), Double.valueOf(destinationLng))).withTextField("Destination"));
        symbolArrayList.add(source);
        symbolArrayList.add(destination);

        Log.i(TAG, "initMap onLoaded: " + (System.currentTimeMillis() - time1st));

        for (Symbol symbol : symbolArrayList) {

            symbolManager.update(symbol);
        }
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
                                //route1Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("2")) {
                                Symbol Route2 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE2").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                symbolArrayList.add(Route2);
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "routessss" + String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                // route2Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("3")) {
                                Symbol Route3 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE3").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));

                                symbolArrayList.add(Route3);
                                // Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //  route3Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
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
                                //   route4Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("5")) {
                                Symbol Route5 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE5").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route5);
                                //       Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //     Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route5Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("6")) {
                                Symbol Route6 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE6").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route6);
                                //    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //     route6Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("7")) {
                                Symbol Route7 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE7").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route7);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("8")) {
                                Symbol Route8 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE8").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route8);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("9")) {
                                Symbol Route9 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE9").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route9);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("10")) {
                                Symbol Route10 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE10").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route10);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("11")) {
                                Symbol Route11 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE11").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                symbolArrayList.add(Route11);
                                //   Toast.makeText(getActivity(), "routessss 1: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //route1Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("12")) {
                                Symbol Route12 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE12").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                symbolArrayList.add(Route12);
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "routessss" + String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                // route2Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("13")) {
                                Symbol Route13 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE13").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));

                                symbolArrayList.add(Route13);
                                // Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //  route3Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("14")) {
                                Symbol Route14 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE14").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F")
                                        .withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route14);
                                //      Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), "routessss4: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route4Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("15")) {
                                Symbol Route15 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE15").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route15);
                                //       Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //     Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route5Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("16")) {
                                Symbol Route16 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE16").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route16);
                                //    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //     route6Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("17")) {
                                Symbol Route17 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE17").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route17);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("18")) {
                                Symbol Route18 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE18").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route18);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("19")) {
                                Symbol Route19 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE19").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route19);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("20")) {
                                Symbol Route20 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE20").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route20);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("21")) {
                                Symbol Route21 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE21").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                symbolArrayList.add(Route21);
                                //   Toast.makeText(getActivity(), "routessss 1: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //route1Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("22")) {
                                Symbol Route22 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE22").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                symbolArrayList.add(Route22);
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "routessss" + String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                // route2Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("23")) {
                                Symbol Route23 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE23").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));

                                symbolArrayList.add(Route23);
                                // Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //  route3Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("24")) {
                                Symbol Route24 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE24").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F")
                                        .withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route24);
                                //      Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), "routessss4: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route4Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("25")) {
                                Symbol Route25 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE25").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route25);
                                //       Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //     Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route5Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("26")) {
                                Symbol Route26 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE26").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route26);
                                //    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //     route6Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("27")) {
                                Symbol Route27 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE27").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route27);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("28")) {
                                Symbol Route28 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE28").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route28);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("29")) {
                                Symbol Route29 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE29").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route29);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("30")) {
                                Symbol Route30 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE30").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route30);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("31")) {
                                Symbol Route31 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE31").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                symbolArrayList.add(Route31);
                                //   Toast.makeText(getActivity(), "routessss 1: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //route1Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("32")) {
                                Symbol Route32 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE32").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                symbolArrayList.add(Route32);
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "routessss" + String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                // route2Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("33")) {
                                Symbol Route33 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE33").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));

                                symbolArrayList.add(Route33);
                                // Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //  route3Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("34")) {
                                Symbol Route34 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE34").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F")
                                        .withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route34);
                                //      Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), "routessss4: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route4Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("35")) {
                                Symbol Route35 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE35").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route35);
                                //       Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //     Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route5Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("36")) {
                                Symbol Route36 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE36").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route36);
                                //    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //     route6Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("37")) {
                                Symbol Route37 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE37").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route37);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("38")) {
                                Symbol Route38 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE38").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route38);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("39")) {
                                Symbol Route39 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE39").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route39);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("40")) {
                                Symbol Route40 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE40").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route40);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("41")) {
                                Symbol Route41 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE41").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                symbolArrayList.add(Route41);
                                //   Toast.makeText(getActivity(), "routessss 1: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //route1Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("42")) {
                                Symbol Route42 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE42").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                symbolArrayList.add(Route42);
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "routessss" + String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                // route2Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("43")) {
                                Symbol Route43 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE43").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));

                                symbolArrayList.add(Route43);
                                // Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //  route3Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("44")) {
                                Symbol Route44 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE44").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F")
                                        .withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route44);
                                //      Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), "routessss4: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route4Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("45")) {
                                Symbol Route45 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE45").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route45);
                                //       Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //     Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route5Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("46")) {
                                Symbol Route46 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE46").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route46);
                                //    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //     route6Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("47")) {
                                Symbol Route47 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE47").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route47);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("48")) {
                                Symbol Route48 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE48").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route48);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("49")) {
                                Symbol Route49 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE49").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route49);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("50")) {
                                Symbol Route50 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE50").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route50);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("51")) {
                                Symbol Route51 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE51").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                symbolArrayList.add(Route51);
                                //   Toast.makeText(getActivity(), "routessss 1: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //route1Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("52")) {
                                Symbol Route52 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE52").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                symbolArrayList.add(Route52);
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "routessss" + String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                // route2Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("53")) {
                                Symbol Route53 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE53").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));

                                symbolArrayList.add(Route53);
                                // Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //  route3Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("54")) {
                                Symbol Route54 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE54").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F")
                                        .withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route54);
                                //      Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), "routessss4: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route4Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("55")) {
                                Symbol Route55 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE55").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route55);
                                //       Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //     Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route5Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("56")) {
                                Symbol Route56 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE56").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route56);
                                //    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //     route6Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("57")) {
                                Symbol Route57 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE57").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route57);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("58")) {
                                Symbol Route58 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE58").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route58);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("59")) {
                                Symbol Route59 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE59").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route59);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("60")) {
                                Symbol Route60 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE60").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route60);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("61")) {
                                Symbol Route61 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE61").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                symbolArrayList.add(Route61);
                                //   Toast.makeText(getActivity(), "routessss 1: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //route1Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("62")) {
                                Symbol Route62 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE62").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                symbolArrayList.add(Route62);
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "routessss" + String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                // route2Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("63")) {
                                Symbol Route63 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE63").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));

                                symbolArrayList.add(Route63);
                                // Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //  route3Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("64")) {
                                Symbol Route64 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE64").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F")
                                        .withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route64);
                                //      Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), "routessss4: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route4Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("65")) {
                                Symbol Route65 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE65").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route65);
                                //       Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //     Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route5Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("66")) {
                                Symbol Route66 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE66").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route66);
                                //    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //     route6Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("67")) {
                                Symbol Route67 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE67").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route67);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("68")) {
                                Symbol Route68 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE68").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route68);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("69")) {
                                Symbol Route69 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE69").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route69);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("70")) {
                                Symbol Route70 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE70").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route70);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("71")) {
                                Symbol Route71 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE71").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //    Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                symbolArrayList.add(Route71);
                                //   Toast.makeText(getActivity(), "routessss 1: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //route1Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("72")) {
                                Symbol Route72 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE72").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));
                                symbolArrayList.add(Route72);
                                //     Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "routessss" + String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                // route2Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("73")) {
                                Symbol Route73 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE73").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName().toString()));

                                symbolArrayList.add(Route73);
                                // Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //  route3Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("74")) {
                                Symbol Route74 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE74").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F")
                                        .withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route74);
                                //      Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                //      Toast.makeText(getActivity(), "routessss4: "+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route4Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("75")) {
                                Symbol Route75 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE75").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route75);
                                //       Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //     Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //   route5Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("76")) {
                                Symbol Route76 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE76").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route76);
                                //    Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //     route6Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));
                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("77")) {
                                Symbol Route77 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE77").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route77);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("78")) {
                                Symbol Route78 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE78").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route78);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("79")) {
                                Symbol Route79 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE79").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route79);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }
                            if (String.valueOf(stop.getRoutes().get(i)).equals("80")) {
                                Symbol Route80 = symbolManager.create(new SymbolOptions().withIconImage("ROUTE80").withIconHaloWidth(0.5f).withIconSize(1.2f).withIconHaloColor("#E2000F").withTextColor("#E2000F").withTextHaloColor("#000000").withTextHaloWidth(0.5f).withTextSize(15f).withTextOffset(new Float[]{0.0f, 3.0f}).withLatLng(new LatLng((stop.getLat()), stop.getLng())).withTextField(stop.getName()));
                                symbolArrayList.add(Route80);
                                //   Toast.makeText(getActivity(), "routessss 7 :"+String.valueOf(stop.getRoutes().get(i)), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), stop.getName(), Toast.LENGTH_SHORT).show();
                                //   Toast.makeText(getActivity(), (int) stop.getLat(), Toast.LENGTH_SHORT).show();
                                lat = stop.getLat();
                                lng = stop.getLng();
                                //       route7Points.add(Point.fromLngLat(Double.valueOf(lng), Double.valueOf(lat)));

                            }

                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void loadRoute(Style style) {
//        GeoJsonSource geoJsonSource1 = new GeoJsonSource("ROUTE1");
//        try {
//            URI uri = new URI("asset://route1.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource1 = new GeoJsonSource("ROUTE1", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource2 = new GeoJsonSource("ROUTE2");
//        try {
//            URI uri = new URI("asset://route2.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource2 = new GeoJsonSource("ROUTE2", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource3 = new GeoJsonSource("ROUTE3");
//        try {
//            URI uri = new URI("asset://route3.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource3 = new GeoJsonSource("ROUTE3", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource4 = new GeoJsonSource("ROUTE4");
//        try {
//            URI uri = new URI("asset://route4.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource4 = new GeoJsonSource("ROUTE4", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource5 = new GeoJsonSource("ROUTE5");
//        try {
//            URI uri = new URI("asset://route5.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource5 = new GeoJsonSource("ROUTE5", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource6 = new GeoJsonSource("ROUTE6");
//        try {
//            URI uri = new URI("asset://route6.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource6 = new GeoJsonSource("ROUTE6", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource7 = new GeoJsonSource("ROUTE7");
//        try {
//            URI uri = new URI("asset://route7.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource7 = new GeoJsonSource("ROUTE7", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource8 = new GeoJsonSource("ROUTE8");
//        try {
//            URI uri = new URI("asset://route8.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource8 = new GeoJsonSource("ROUTE8", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource9 = new GeoJsonSource("ROUTE9");
//        try {
//            URI uri = new URI("asset://route9.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource9 = new GeoJsonSource("ROUTE9", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource10 = new GeoJsonSource("ROUTE10");
//        try {
//            URI uri = new URI("asset://route10.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource10 = new GeoJsonSource("ROUTE10", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource11 = new GeoJsonSource("ROUTE11");
//        try {
//            URI uri = new URI("asset://route11.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource11 = new GeoJsonSource("ROUTE11", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource12 = new GeoJsonSource("ROUTE12");
//        try {
//            URI uri = new URI("asset://route12.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource12 = new GeoJsonSource("ROUTE12", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource13 = new GeoJsonSource("ROUTE13");
//        try {
//            URI uri = new URI("asset://route13.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource13 = new GeoJsonSource("ROUTE13", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource14 = new GeoJsonSource("ROUTE14");
//        try {
//            URI uri = new URI("asset://route14.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource14 = new GeoJsonSource("ROUTE14", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource15 = new GeoJsonSource("ROUTE15");
//        try {
//            URI uri = new URI("asset://route15.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource15 = new GeoJsonSource("ROUTE15", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource16 = new GeoJsonSource("ROUTE16");
//        try {
//            URI uri = new URI("asset://route16.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource16 = new GeoJsonSource("ROUTE16", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource17 = new GeoJsonSource("ROUTE17");
//        try {
//            URI uri = new URI("asset://route17.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource17 = new GeoJsonSource("ROUTE17", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource18 = new GeoJsonSource("ROUTE18");
//        try {
//            URI uri = new URI("asset://route18.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource18 = new GeoJsonSource("ROUTE18", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource19 = new GeoJsonSource("ROUTE19");
//        try {
//            URI uri = new URI("asset://route19.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource19 = new GeoJsonSource("ROUTE19", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource20 = new GeoJsonSource("ROUTE20");
//        try {
//            URI uri = new URI("asset://route20.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource20 = new GeoJsonSource("ROUTE20", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource21 = new GeoJsonSource("ROUTE21");
//        try {
//            URI uri = new URI("asset://route21.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource21 = new GeoJsonSource("ROUTE21", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource22 = new GeoJsonSource("ROUTE22");
//        try {
//            URI uri = new URI("asset://route22.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource22 = new GeoJsonSource("ROUTE22", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource23 = new GeoJsonSource("ROUTE23");
//        try {
//            URI uri = new URI("asset://route23.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource23 = new GeoJsonSource("ROUTE23", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource24 = new GeoJsonSource("ROUTE24");
//        try {
//            URI uri = new URI("asset://route24.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource24 = new GeoJsonSource("ROUTE24", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource25 = new GeoJsonSource("ROUTE25");
//        try {
//            URI uri = new URI("asset://route25.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource25 = new GeoJsonSource("ROUTE25", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource26 = new GeoJsonSource("ROUTE26");
//        try {
//            URI uri = new URI("asset://route26.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource26 = new GeoJsonSource("ROUTE26", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource27 = new GeoJsonSource("ROUTE27");
//        try {
//            URI uri = new URI("asset://route27.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource27 = new GeoJsonSource("ROUTE27", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource28 = new GeoJsonSource("ROUTE28");
//        try {
//            URI uri = new URI("asset://route28.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource28 = new GeoJsonSource("ROUTE28", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource29 = new GeoJsonSource("ROUTE29");
//        try {
//            URI uri = new URI("asset://route29.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource29 = new GeoJsonSource("ROUTE29", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource30 = new GeoJsonSource("ROUTE30");
//        try {
//            URI uri = new URI("asset://route30.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource30 = new GeoJsonSource("ROUTE30", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource31 = new GeoJsonSource("ROUTE31");
//        try {
//            URI uri = new URI("asset://route31.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource31 = new GeoJsonSource("ROUTE31", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource32 = new GeoJsonSource("ROUTE32");
//        try {
//            URI uri = new URI("asset://route32.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource32 = new GeoJsonSource("ROUTE32", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource33 = new GeoJsonSource("ROUTE33");
//        try {
//            URI uri = new URI("asset://route33.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource33 = new GeoJsonSource("ROUTE33", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource34 = new GeoJsonSource("ROUTE34");
//        try {
//            URI uri = new URI("asset://route34.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource34 = new GeoJsonSource("ROUTE34", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource35 = new GeoJsonSource("ROUTE35");
//        try {
//            URI uri = new URI("asset://route35.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource35 = new GeoJsonSource("ROUTE35", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource36 = new GeoJsonSource("ROUTE36");
//        try {
//            URI uri = new URI("asset://route36.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource36 = new GeoJsonSource("ROUTE36", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource37 = new GeoJsonSource("ROUTE37");
//        try {
//            URI uri = new URI("asset://route37.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource37 = new GeoJsonSource("ROUTE37", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource38 = new GeoJsonSource("ROUTE38");
//        try {
//            URI uri = new URI("asset://route38.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource38 = new GeoJsonSource("ROUTE38", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource39 = new GeoJsonSource("ROUTE39");
//        try {
//            URI uri = new URI("asset://route39.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource39 = new GeoJsonSource("ROUTE39", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource40 = new GeoJsonSource("ROUTE40");
//        try {
//            URI uri = new URI("asset://route40.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource40 = new GeoJsonSource("ROUTE40", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource41 = new GeoJsonSource("ROUTE41");
//        try {
//            URI uri = new URI("asset://route41.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource41 = new GeoJsonSource("ROUTE41", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource42 = new GeoJsonSource("ROUTE42");
//        try {
//            URI uri = new URI("asset://route42.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource42 = new GeoJsonSource("ROUTE42", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource43 = new GeoJsonSource("ROUTE43");
//        try {
//            URI uri = new URI("asset://route43.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource43 = new GeoJsonSource("ROUTE43", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource44 = new GeoJsonSource("ROUTE44");
//        try {
//            URI uri = new URI("asset://route44.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource44 = new GeoJsonSource("ROUTE44", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource45 = new GeoJsonSource("ROUTE45");
//        try {
//            URI uri = new URI("asset://route45.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource45 = new GeoJsonSource("ROUTE45", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource46 = new GeoJsonSource("ROUTE46");
//        try {
//            URI uri = new URI("asset://route46.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource46 = new GeoJsonSource("ROUTE46", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource47 = new GeoJsonSource("ROUTE47");
//        try {
//            URI uri = new URI("asset://route47.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource47 = new GeoJsonSource("ROUTE47", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource48 = new GeoJsonSource("ROUTE48");
//        try {
//            URI uri = new URI("asset://route48.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource48 = new GeoJsonSource("ROUTE48", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource49 = new GeoJsonSource("ROUTE49");
//        try {
//            URI uri = new URI("asset://route49.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource49 = new GeoJsonSource("ROUTE49", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource50 = new GeoJsonSource("ROUTE50");
//        try {
//            URI uri = new URI("asset://route50.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource50 = new GeoJsonSource("ROUTE50", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource51 = new GeoJsonSource("ROUTE51");
//        try {
//            URI uri = new URI("asset://route51.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource51 = new GeoJsonSource("ROUTE51", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource52 = new GeoJsonSource("ROUTE52");
//        try {
//            URI uri = new URI("asset://route52.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource52 = new GeoJsonSource("ROUTE52", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource53 = new GeoJsonSource("ROUTE53");
//        try {
//            URI uri = new URI("asset://route53.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource53 = new GeoJsonSource("ROUTE53", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource54 = new GeoJsonSource("ROUTE54");
//        try {
//            URI uri = new URI("asset://route54.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource54 = new GeoJsonSource("ROUTE54", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource55 = new GeoJsonSource("ROUTE55");
//        try {
//            URI uri = new URI("asset://route55.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource55 = new GeoJsonSource("ROUTE55", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource56 = new GeoJsonSource("ROUTE56");
//        try {
//            URI uri = new URI("asset://route56.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource56 = new GeoJsonSource("ROUTE56", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource57 = new GeoJsonSource("ROUTE57");
//        try {
//            URI uri = new URI("asset://route57.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource57 = new GeoJsonSource("ROUTE57", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource58 = new GeoJsonSource("ROUTE58");
//        try {
//            URI uri = new URI("asset://route58.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource58 = new GeoJsonSource("ROUTE58", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource59 = new GeoJsonSource("ROUTE59");
//        try {
//            URI uri = new URI("asset://route59.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource59 = new GeoJsonSource("ROUTE59", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource60 = new GeoJsonSource("ROUTE60");
//        try {
//            URI uri = new URI("asset://route60.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource60 = new GeoJsonSource("ROUTE60", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource61 = new GeoJsonSource("ROUTE61");
//        try {
//            URI uri = new URI("asset://route61.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource61 = new GeoJsonSource("ROUTE61", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource62 = new GeoJsonSource("ROUTE62");
//        try {
//            URI uri = new URI("asset://route62.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource62 = new GeoJsonSource("ROUTE62", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource63 = new GeoJsonSource("ROUTE63");
//        try {
//            URI uri = new URI("asset://route63.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource63 = new GeoJsonSource("ROUTE63", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource64 = new GeoJsonSource("ROUTE64");
//        try {
//            URI uri = new URI("asset://route64.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource64 = new GeoJsonSource("ROUTE64", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource65 = new GeoJsonSource("ROUTE65");
//        try {
//            URI uri = new URI("asset://route65.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource65 = new GeoJsonSource("ROUTE65", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource66 = new GeoJsonSource("ROUTE66");
//        try {
//            URI uri = new URI("asset://route66.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource66 = new GeoJsonSource("ROUTE66", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource67 = new GeoJsonSource("ROUTE67");
//        try {
//            URI uri = new URI("asset://route67.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource67 = new GeoJsonSource("ROUTE67", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource68 = new GeoJsonSource("ROUTE68");
//        try {
//            URI uri = new URI("asset://route68.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource68 = new GeoJsonSource("ROUTE68", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource69 = new GeoJsonSource("ROUTE69");
//        try {
//            URI uri = new URI("asset://route69.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource69 = new GeoJsonSource("ROUTE69", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource70 = new GeoJsonSource("ROUTE70");
//        try {
//            URI uri = new URI("asset://route70.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource70 = new GeoJsonSource("ROUTE70", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource71 = new GeoJsonSource("ROUTE71");
//        try {
//            URI uri = new URI("asset://route71.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource71 = new GeoJsonSource("ROUTE71", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource72 = new GeoJsonSource("ROUTE72");
//        try {
//            URI uri = new URI("asset://route72.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource72 = new GeoJsonSource("ROUTE72", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource73 = new GeoJsonSource("ROUTE73");
//        try {
//            URI uri = new URI("asset://route73.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource73 = new GeoJsonSource("ROUTE73", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource74 = new GeoJsonSource("ROUTE74");
//        try {
//            URI uri = new URI("asset://route74.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource74 = new GeoJsonSource("ROUTE74", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource75 = new GeoJsonSource("ROUTE75");
//        try {
//            URI uri = new URI("asset://route75.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource75 = new GeoJsonSource("ROUTE75", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource76 = new GeoJsonSource("ROUTE76");
//        try {
//            URI uri = new URI("asset://route76.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource76 = new GeoJsonSource("ROUTE76", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource77 = new GeoJsonSource("ROUTE77");
//        try {
//            URI uri = new URI("asset://route77.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource77 = new GeoJsonSource("ROUTE77", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource78 = new GeoJsonSource("ROUTE78");
//        try {
//            URI uri = new URI("asset://route78.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource78 = new GeoJsonSource("ROUTE78", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource79 = new GeoJsonSource("ROUTE79");
//        try {
//            URI uri = new URI("asset://route79.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource79 = new GeoJsonSource("ROUTE79", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        GeoJsonSource geoJsonSource80 = new GeoJsonSource("ROUTE80");
//        try {
//            URI uri = new URI("asset://route80.geojson");
//            Log.i(TAG, "onStyleLoaded: " + uri);
//            style.addSource(geoJsonSource80 = new GeoJsonSource("ROUTE80", uri));
//            Log.i(TAG, "onStyleLoaded: " + style.getSources());
//        } catch (NullPointerException | URISyntaxException e) {
//            e.printStackTrace();
//        }

        int i=0;
        for(int commonRoute:commonRoutes)
        {
            //Toast.makeText(getActivity(), "Common Routessss: "+commonRoute, Toast.LENGTH_SHORT).show();
            Log.d("route","Common Routessss: "+commonRoute);
            GeoJsonSource geoJsonSource = new GeoJsonSource("ROUTE"+commonRoute);

            try {
                URI uri = new URI("asset://route"+commonRoute+".geojson");
                Log.i(TAG, "onStyleLoaded: " + uri);
                style.addSource(geoJsonSource = new GeoJsonSource("ROUTE"+commonRoute, uri));
                Log.i(TAG, "onStyleLoaded: " + style.getSources());
            } catch (NullPointerException | URISyntaxException e) {
                e.printStackTrace();
            }

            geoJsonSources.add(geoJsonSource);
            route = new LineLayer("ROUTE"+commonRoute+"L", "ROUTE"+commonRoute);
            route.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5eafe5")));

            i++;


        }
        route1 = new LineLayer("ROUTE1L", "ROUTE1");
        route2 = new LineLayer("ROUTE2L", "ROUTE2");
        route3 = new LineLayer("ROUTE3L", "ROUTE3");
        route4 = new LineLayer("ROUTE4L", "ROUTE4");
        route5 = new LineLayer("ROUTE5L", "ROUTE5");
        route6 = new LineLayer("ROUTE6L", "ROUTE6");
        route7 = new LineLayer("ROUTE7L", "ROUTE7");
        route8 = new LineLayer("ROUTE8L", "ROUTE8");
        route9 = new LineLayer("ROUTE9L", "ROUTE9");
        route10 = new LineLayer("ROUTE10L", "ROUTE10");
        route11 = new LineLayer("ROUTE11L", "ROUTE11");
        route12 = new LineLayer("ROUTE12L", "ROUTE12");
        route13 = new LineLayer("ROUTE13L", "ROUTE13");
        route14 = new LineLayer("ROUTE14L", "ROUTE14");
        route15 = new LineLayer("ROUTE15L", "ROUTE15");
        route16 = new LineLayer("ROUTE16L", "ROUTE16");
        route17 = new LineLayer("ROUTE17L", "ROUTE17");
        route18 = new LineLayer("ROUTE18L", "ROUTE18");
        route19 = new LineLayer("ROUTE19L", "ROUTE19");
        route20 = new LineLayer("ROUTE20L", "ROUTE20");
        route21 = new LineLayer("ROUTE21L", "ROUTE21");
        route22 = new LineLayer("ROUTE22L", "ROUTE22");
        route23 = new LineLayer("ROUTE23L", "ROUTE23");
        route24 = new LineLayer("ROUTE24L", "ROUTE24");
        route25 = new LineLayer("ROUTE25L", "ROUTE25");
        route26 = new LineLayer("ROUTE26L", "ROUTE26");
        route27 = new LineLayer("ROUTE27L", "ROUTE27");
        route28 = new LineLayer("ROUTE28L", "ROUTE28");
        route29 = new LineLayer("ROUTE29L", "ROUTE29");
        route30 = new LineLayer("ROUTE30L", "ROUTE30");
        route31 = new LineLayer("ROUTE31L", "ROUTE31");
        route32 = new LineLayer("ROUTE32L", "ROUTE32");
        route33 = new LineLayer("ROUTE33L", "ROUTE33");
        route34 = new LineLayer("ROUTE34L", "ROUTE34");
        route35 = new LineLayer("ROUTE35L", "ROUTE35");
        route36 = new LineLayer("ROUTE36L", "ROUTE36");
        route37 = new LineLayer("ROUTE37L", "ROUTE37");
        route38 = new LineLayer("ROUTE38L", "ROUTE38");
        route39 = new LineLayer("ROUTE39L", "ROUTE39");
        route40 = new LineLayer("ROUTE40L", "ROUTE40");
        route41 = new LineLayer("ROUTE41L", "ROUTE41");
        route42 = new LineLayer("ROUTE42L", "ROUTE42");
        route43 = new LineLayer("ROUTE43L", "ROUTE43");
        route44 = new LineLayer("ROUTE44L", "ROUTE44");
        route45 = new LineLayer("ROUTE45L", "ROUTE45");
        route46 = new LineLayer("ROUTE46L", "ROUTE46");
        route47 = new LineLayer("ROUTE47L", "ROUTE47");
        route48 = new LineLayer("ROUTE48L", "ROUTE48");
        route49 = new LineLayer("ROUTE49L", "ROUTE49");
        route50 = new LineLayer("ROUTE50L", "ROUTE50");
        route51 = new LineLayer("ROUTE51L", "ROUTE51");
        route52 = new LineLayer("ROUTE52L", "ROUTE52");
        route53 = new LineLayer("ROUTE53L", "ROUTE53");
        route54 = new LineLayer("ROUTE54L", "ROUTE54");
        route55 = new LineLayer("ROUTE55L", "ROUTE55");
        route56 = new LineLayer("ROUTE56L", "ROUTE56");
        route57 = new LineLayer("ROUTE57L", "ROUTE57");
        route58 = new LineLayer("ROUTE58L", "ROUTE58");
        route59 = new LineLayer("ROUTE59L", "ROUTE59");
        route60 = new LineLayer("ROUTE60L", "ROUTE60");
        route61 = new LineLayer("ROUTE61L", "ROUTE61");
        route62 = new LineLayer("ROUTE62L", "ROUTE62");
        route63 = new LineLayer("ROUTE63L", "ROUTE63");
        route64 = new LineLayer("ROUTE64L", "ROUTE64");
        route65 = new LineLayer("ROUTE65L", "ROUTE65");
        route66 = new LineLayer("ROUTE66L", "ROUTE66");
        route67 = new LineLayer("ROUTE67L", "ROUTE67");
        route68 = new LineLayer("ROUTE68L", "ROUTE68");
        route69 = new LineLayer("ROUTE69L", "ROUTE69");
        route70 = new LineLayer("ROUTE70L", "ROUTE70");
        route71 = new LineLayer("ROUTE71L", "ROUTE71");
        route72 = new LineLayer("ROUTE72L", "ROUTE72");
        route73 = new LineLayer("ROUTE73L", "ROUTE73");
        route74 = new LineLayer("ROUTE74L", "ROUTE74");
        route75 = new LineLayer("ROUTE75L", "ROUTE75");
        route76 = new LineLayer("ROUTE76L", "ROUTE76");
        route77 = new LineLayer("ROUTE77L", "ROUTE77");
        route78 = new LineLayer("ROUTE78L", "ROUTE78");
        route79 = new LineLayer("ROUTE79L", "ROUTE79");
        route80 = new LineLayer("ROUTE80L", "ROUTE80");

        route1.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5eafe5")));
        route2.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route3.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5ee5b8")));
        route4.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route5.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route6.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route7.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route8.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route9.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5eafe5")));
        route10.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route11.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5ee5b8")));
        route12.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route13.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route14.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route15.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route16.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5eafe5")));
        route17.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route18.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5ee5b8")));
        route19.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route20.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route21.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route22.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));

        route23.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5ee5b8")));
        route24.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route25.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route26.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route27.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route28.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5eafe5")));
        route29.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route30.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5ee5b8")));
        route31.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route32.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route33.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route34.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route35.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5eafe5")));
        route36.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route37.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5ee5b8")));
        route38.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route39.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route40.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route41.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        /* red color*/
        route42.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route43.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route44.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route45.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5eafe5")));
        route46.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route47.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5ee5b8")));
        route48.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route49.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route50.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));

        route51.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route52.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route53.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route54.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route55.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5eafe5")));
        route56.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route57.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5ee5b8")));
        route58.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route59.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route60.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route61.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route62.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route63.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route64.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route65.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5eafe5")));
        route66.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route67.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5ee5b8")));
        route68.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route69.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route70.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route71.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route72.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route73.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route74.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route75.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5eafe5")));
        route76.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));
        route77.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#5ee5b8")));
        route78.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5e15e")));
        /* red color*/
        route79.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e55e5e")));
        route80.setProperties(PropertyFactory.fillOutlineColor(Color.RED), PropertyFactory.fillOpacity(0.5f), PropertyFactory.lineWidth(6.23f), PropertyFactory.lineColor(Color.parseColor("#e5a45e")));

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
                    Toast.makeText(getActivity(), "Lat: " + current_location.getLat() + " Lng: " + current_location.getLng() +
                            " License Plate: " + current_location.getLicensePlate(), Toast.LENGTH_SHORT).show();

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
                        Log.d("check", "Datasnapshot");
                    }
                    /*for (Symbol symbol : symbolArrayList) {
                        symbolManager.update(symbol)
                    }*/
                    getCommonRoutesFlag = 1;
                    addStop(stopss);
                    map.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {


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
                                    .include(new LatLng(sourceLat,sourceLng)) // Northeast
                                    .include(new LatLng(destinationLat,destinationLng)) // Southwest
                                    .build();

                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);


                            loadRoute(style);
                            Log.d("check", "OnStyle loaded");
                            fragmentMapBinding.progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
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

    @Override
    public void onClick(String s) {

        routeNo = s;
        Toast.makeText(getActivity(), "Route No.: " + s, Toast.LENGTH_SHORT).show();
        if (!commonRoutes.isEmpty()) {

            //routeNo = getArguments().getString("routeNo");
//                    for (int commonRoute : commonRoutes) {
//            addLayer1Flag = 0;
//            addLayer2Flag = 0;
//            addLayer3Flag = 0;
//            addLayer4Flag = 0;
//            addLayer5Flag = 0;
//            addLayer6Flag = 0;
//            addLayer7Flag = 0;
            if (addLayer1Flag == 0) {
                if (routeNo.equals("1")) {
                    Toast.makeText(getActivity(), "Route 1 called", Toast.LENGTH_SHORT).show();

                    map.getStyle().addLayer(route1);
                    addLayer1Flag = 1;
                }
            }

            if (addLayer2Flag == 0) {
                if (routeNo.equals("2")) {
                    Toast.makeText(getActivity(), "Route 2 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route2);
                    addLayer2Flag = 1;
                }
            }

            if (addLayer3Flag == 0) {
                if (routeNo.equals("3")) {
                    Toast.makeText(getActivity(), "Route 3 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route3);
                    addLayer3Flag = 1;
                }
            }
            if (addLayer4Flag == 0) {
                if (routeNo.equals("4")) {
                    Toast.makeText(getActivity(), "Route 4 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route4);
                    addLayer4Flag = 1;
                }
            }

            if (addLayer5Flag == 0) {
                if (routeNo.equals("5")) {
                    Toast.makeText(getActivity(), "Route 5 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route5);
                    addLayer5Flag = 1;
                }
            }
            if (addLayer6Flag == 0) {
                if (routeNo.equals("6")) {
                    Toast.makeText(getActivity(), "Route 6 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route6);
                    addLayer6Flag = 1;
                }
            }
            if (addLayer7Flag == 0) {
                if (routeNo.equals("7")) {
                    Toast.makeText(getActivity(), "Route 7 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route7);
                    addLayer7Flag = 1;
                }
            }
            if (addLayer8Flag == 0) {
                if (routeNo.equals("8")) {
                    Toast.makeText(getActivity(), "Route 8 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route8);
                    addLayer8Flag = 1;
                }
            }
            if (addLayer9Flag == 0) {
                if (routeNo.equals("9")) {
                    Toast.makeText(getActivity(), "Route 9 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route9);
                    addLayer9Flag = 1;
                }
            }
            if (addLayer10Flag == 0) {
                if (routeNo.equals("10")) {
                    Toast.makeText(getActivity(), "Route 10 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route10);
                    addLayer10Flag = 1;
                }
            }
            if (addLayer11Flag == 0) {
                if (routeNo.equals("11")) {
                    Toast.makeText(getActivity(), "Route 11 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route11);
                    addLayer11Flag = 1;
                }
            }

            if (addLayer12Flag == 0) {
                if (routeNo.equals("12")) {
                    Toast.makeText(getActivity(), "Route 12 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route12);
                    addLayer12Flag = 1;
                }
            }

            if (addLayer13Flag == 0) {
                if (routeNo.equals("13")) {
                    Toast.makeText(getActivity(), "Route 13 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route13);
                    addLayer13Flag = 1;
                }
            }
            if (addLayer14Flag == 0) {
                if (routeNo.equals("14")) {
                    Toast.makeText(getActivity(), "Route 14 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route14);
                    addLayer14Flag = 1;
                }
            }

            if (addLayer15Flag == 0) {
                if (routeNo.equals("15")) {
                    Toast.makeText(getActivity(), "Route 15 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route15);
                    addLayer15Flag = 1;
                }
            }
            if (addLayer17Flag == 0) {
                if (routeNo.equals("17")) {
                    Toast.makeText(getActivity(), "Route 17 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route17);
                    addLayer17Flag = 1;
                }
            }
            if (addLayer18Flag == 0) {
                if (routeNo.equals("18")) {
                    Toast.makeText(getActivity(), "Route 18 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route18);
                    addLayer18Flag = 1;
                }
            }
            if (addLayer19Flag == 0) {
                if (routeNo.equals("19")) {
                    Toast.makeText(getActivity(), "Route 19 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route19);
                    addLayer19Flag = 1;
                }
            }
            if (addLayer20Flag == 0) {
                if (routeNo.equals("20")) {
                    Toast.makeText(getActivity(), "Route 20 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route20);
                    addLayer20Flag = 1;
                }
            }
            if (addLayer21Flag == 0) {
                if (routeNo.equals("21")) {
                    Toast.makeText(getActivity(), "Route 21 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route21);
                    addLayer21Flag = 1;
                }
            }

            if (addLayer22Flag == 0) {
                if (routeNo.equals("22")) {
                    Toast.makeText(getActivity(), "Route 22 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route22);
                    addLayer22Flag = 1;
                }
            }

            if (addLayer23Flag == 0) {
                if (routeNo.equals("23")) {
                    Toast.makeText(getActivity(), "Route 23 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route23);
                    addLayer23Flag = 1;
                }
            }
            if (addLayer24Flag == 0) {
                if (routeNo.equals("24")) {
                    Toast.makeText(getActivity(), "Route 24 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route24);
                    addLayer24Flag = 1;
                }
            }

            if (addLayer25Flag == 0) {
                if (routeNo.equals("25")) {
                    Toast.makeText(getActivity(), "Route 25 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route25);
                    addLayer25Flag = 1;
                }
            }
            if (addLayer26Flag == 0) {
                if (routeNo.equals("26")) {
                    Toast.makeText(getActivity(), "Route 26 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route26);
                    addLayer26Flag = 1;
                }
            }
            if (addLayer27Flag == 0) {
                if (routeNo.equals("27")) {
                    Toast.makeText(getActivity(), "Route 27 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route27);
                    addLayer27Flag = 1;
                }
            }
            if (addLayer28Flag == 0) {
                if (routeNo.equals("28")) {
                    Toast.makeText(getActivity(), "Route 28 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route28);
                    addLayer28Flag = 1;
                }
            }
            if (addLayer29Flag == 0) {
                if (routeNo.equals("29")) {
                    Toast.makeText(getActivity(), "Route 29 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route9);
                    addLayer9Flag = 1;
                }
            }
            if (addLayer30Flag == 0) {
                if (routeNo.equals("30")) {
                    Toast.makeText(getActivity(), "Route 30 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route30);
                    addLayer30Flag = 1;
                }
            }
            if (addLayer31Flag == 0) {
                if (routeNo.equals("31")) {
                    Toast.makeText(getActivity(), "Route 31 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route31);
                    addLayer31Flag = 1;
                }
            }

            if (addLayer32Flag == 0) {
                if (routeNo.equals("32")) {
                    Toast.makeText(getActivity(), "Route 32 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route32);
                    addLayer32Flag = 1;
                }
            }

            if (addLayer33Flag == 0) {
                if (routeNo.equals("33")) {
                    Toast.makeText(getActivity(), "Route 33 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route33);
                    addLayer33Flag = 1;
                }
            }
            if (addLayer34Flag == 0) {
                if (routeNo.equals("34")) {
                    Toast.makeText(getActivity(), "Route 34 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route34);
                    addLayer34Flag = 1;
                }
            }

            if (addLayer35Flag == 0) {
                if (routeNo.equals("35")) {
                    Toast.makeText(getActivity(), "Route 35 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route35);
                    addLayer35Flag = 1;
                }
            }
            if (addLayer36Flag == 0) {
                if (routeNo.equals("36")) {
                    Toast.makeText(getActivity(), "Route 36 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route36);
                    addLayer36Flag = 1;
                }
            }
            if (addLayer37Flag == 0) {
                if (routeNo.equals("37")) {
                    Toast.makeText(getActivity(), "Route 37 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route37);
                    addLayer37Flag = 1;
                }
            }
            if (addLayer38Flag == 0) {
                if (routeNo.equals("38")) {
                    Toast.makeText(getActivity(), "Route 38 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route38);
                    addLayer38Flag = 1;
                }
            }
            if (addLayer39Flag == 0) {
                if (routeNo.equals("39")) {
                    Toast.makeText(getActivity(), "Route 39 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route39);
                    addLayer39Flag = 1;
                }
            }
            if (addLayer40Flag == 0) {
                if (routeNo.equals("40")) {
                    Toast.makeText(getActivity(), "Route 40 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route40);
                    addLayer40Flag = 1;
                }
            }
            if (addLayer41Flag == 0) {
                if (routeNo.equals("41")) {
                    Toast.makeText(getActivity(), "Route 41 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route41);
                    addLayer41Flag = 1;
                }
            }

            if (addLayer42Flag == 0) {
                if (routeNo.equals("42")) {
                    Toast.makeText(getActivity(), "Route 42 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route42);
                    addLayer42Flag = 1;
                }
            }

            if (addLayer43Flag == 0) {
                if (routeNo.equals("43")) {
                    Toast.makeText(getActivity(), "Route 43 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route43);
                    addLayer43Flag = 1;
                }
            }
            if (addLayer44Flag == 0) {
                if (routeNo.equals("44")) {
                    Toast.makeText(getActivity(), "Route 44 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route44);
                    addLayer44Flag = 1;
                }
            }

            if (addLayer45Flag == 0) {
                if (routeNo.equals("45")) {
                    Toast.makeText(getActivity(), "Route 45 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route45);
                    addLayer45Flag = 1;
                }
            }
            if (addLayer46Flag == 0) {
                if (routeNo.equals("46")) {
                    Toast.makeText(getActivity(), "Route 46 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route46);
                    addLayer46Flag = 1;
                }
            }
            if (addLayer47Flag == 0) {
                if (routeNo.equals("47")) {
                    Toast.makeText(getActivity(), "Route 47 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route47);
                    addLayer47Flag = 1;
                }
            }
            if (addLayer48Flag == 0) {
                if (routeNo.equals("48")) {
                    Toast.makeText(getActivity(), "Route 48 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route48);
                    addLayer48Flag = 1;
                }
            }
            if (addLayer49Flag == 0) {
                if (routeNo.equals("49")) {
                    Toast.makeText(getActivity(), "Route 49 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route49);
                    addLayer49Flag = 1;
                }
            }
            if (addLayer50Flag == 0) {
                if (routeNo.equals("50")) {
                    Toast.makeText(getActivity(), "Route 50 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route50);
                    addLayer50Flag = 1;
                }
            }
            if (addLayer51Flag == 0) {
                if (routeNo.equals("51")) {
                    Toast.makeText(getActivity(), "Route 51 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route51);
                    addLayer51Flag = 1;
                }
            }

            if (addLayer52Flag == 0) {
                if (routeNo.equals("52")) {
                    Toast.makeText(getActivity(), "Route 52 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route52);
                    addLayer52Flag = 1;
                }
            }

            if (addLayer53Flag == 0) {
                if (routeNo.equals("53")) {
                    Toast.makeText(getActivity(), "Route 53 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route53);
                    addLayer53Flag = 1;
                }
            }
            if (addLayer54Flag == 0) {
                if (routeNo.equals("54")) {
                    Toast.makeText(getActivity(), "Route 54 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route54);
                    addLayer54Flag = 1;
                }
            }

            if (addLayer55Flag == 0) {
                if (routeNo.equals("55")) {
                    Toast.makeText(getActivity(), "Route 55 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route55);
                    addLayer55Flag = 1;
                }
            }
            if (addLayer56Flag == 0) {
                if (routeNo.equals("56")) {
                    Toast.makeText(getActivity(), "Route 56 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route56);
                    addLayer56Flag = 1;
                }
            }
            if (addLayer57Flag == 0) {
                if (routeNo.equals("57")) {
                    Toast.makeText(getActivity(), "Route 57 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route57);
                    addLayer57Flag = 1;
                }
            }
            if (addLayer58Flag == 0) {
                if (routeNo.equals("58")) {
                    Toast.makeText(getActivity(), "Route 58 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route58);
                    addLayer58Flag = 1;
                }
            }
            if (addLayer59Flag == 0) {
                if (routeNo.equals("59")) {
                    Toast.makeText(getActivity(), "Route 59 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route59);
                    addLayer59Flag = 1;
                }
            }
            if (addLayer60Flag == 0) {
                if (routeNo.equals("60")) {
                    Toast.makeText(getActivity(), "Route 60 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route60);
                    addLayer60Flag = 1;
                }
            }

            if (addLayer61Flag == 0) {
                if (routeNo.equals("61")) {
                    Toast.makeText(getActivity(), "Route 61 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route61);
                    addLayer61Flag = 1;
                }
            }

            if (addLayer62Flag == 0) {
                if (routeNo.equals("62")) {
                    Toast.makeText(getActivity(), "Route 62 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route62);
                    addLayer62Flag = 1;
                }
            }

            if (addLayer63Flag == 0) {
                if (routeNo.equals("63")) {
                    Toast.makeText(getActivity(), "Route 63 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route63);
                    addLayer63Flag = 1;
                }
            }
            if (addLayer64Flag == 0) {
                if (routeNo.equals("64")) {
                    Toast.makeText(getActivity(), "Route 64 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route64);
                    addLayer64Flag = 1;
                }
            }

            if (addLayer65Flag == 0) {
                if (routeNo.equals("65")) {
                    Toast.makeText(getActivity(), "Route 65 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route65);
                    addLayer65Flag = 1;
                }
            }
            if (addLayer66Flag == 0) {
                if (routeNo.equals("66")) {
                    Toast.makeText(getActivity(), "Route 66 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route66);
                    addLayer66Flag = 1;
                }
            }
            if (addLayer67Flag == 0) {
                if (routeNo.equals("67")) {
                    Toast.makeText(getActivity(), "Route 67 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route67);
                    addLayer67Flag = 1;
                }
            }
            if (addLayer68Flag == 0) {
                if (routeNo.equals("68")) {
                    Toast.makeText(getActivity(), "Route 68 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route68);
                    addLayer68Flag = 1;
                }
            }
            if (addLayer69Flag == 0) {
                if (routeNo.equals("69")) {
                    Toast.makeText(getActivity(), "Route 69 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route69);
                    addLayer69Flag = 1;
                }
            }
            if (addLayer70Flag == 0) {
                if (routeNo.equals("70")) {
                    Toast.makeText(getActivity(), "Route 70 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route70);
                    addLayer70Flag = 1;
                }
            }
            if (addLayer71Flag == 0) {
                if (routeNo.equals("71")) {
                    Toast.makeText(getActivity(), "Route 71 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route71);
                    addLayer71Flag = 1;
                }
            }

            if (addLayer72Flag == 0) {
                if (routeNo.equals("72")) {
                    Toast.makeText(getActivity(), "Route 72 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route72);
                    addLayer72Flag = 1;
                }
            }

            if (addLayer73Flag == 0) {
                if (routeNo.equals("73")) {
                    Toast.makeText(getActivity(), "Route 73 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route73);
                    addLayer73Flag = 1;
                }
            }
            if (addLayer74Flag == 0) {
                if (routeNo.equals("74")) {
                    Toast.makeText(getActivity(), "Route 74 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route74);
                    addLayer74Flag = 1;
                }
            }

            if (addLayer75Flag == 0) {
                if (routeNo.equals("75")) {
                    Toast.makeText(getActivity(), "Route 75 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route75);
                    addLayer75Flag = 1;
                }
            }
            if (addLayer76Flag == 0) {
                if (routeNo.equals("76")) {
                    Toast.makeText(getActivity(), "Route 76 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route76);
                    addLayer76Flag = 1;
                }
            }
            if (addLayer77Flag == 0) {
                if (routeNo.equals("77")) {
                    Toast.makeText(getActivity(), "Route 77 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route77);
                    addLayer77Flag = 1;
                }
            }
            if (addLayer78Flag == 0) {
                if (routeNo.equals("78")) {
                    Toast.makeText(getActivity(), "Route 78 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route78);
                    addLayer78Flag = 1;
                }
            }
            if (addLayer79Flag == 0) {
                if (routeNo.equals("79")) {
                    Toast.makeText(getActivity(), "Route 79 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route79);
                    addLayer79Flag = 1;
                }
            }
            if (addLayer80Flag == 0) {
                if (routeNo.equals("80")) {
                    Toast.makeText(getActivity(), "Route 80 called", Toast.LENGTH_SHORT).show();
                    map.getStyle().addLayer(route80);
                    addLayer80Flag = 1;
                }
            }

//                    }

        }
    }
}
