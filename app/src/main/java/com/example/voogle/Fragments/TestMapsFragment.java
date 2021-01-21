package com.example.voogle.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.graphics.Color;
import android.icu.util.Calendar;

import android.icu.util.TimeZone;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.voogle.Adapters.BusButtonAdapter;
import com.example.voogle.Adapters.BusButtonDetailsAdapter;
import com.example.voogle.Adapters.RouteButtonAdapter;
import com.example.voogle.Functions.MapClick;
import com.example.voogle.GlobalVariables;
import com.example.voogle.PojoClasses.Bus;
import com.example.voogle.PojoClasses.StopNew;
import com.example.voogle.R;
import com.example.voogle.databinding.FragmentTestMapsBinding;
import com.example.voogle.databinding.PopupBusDetailsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestMapsFragment extends Fragment implements MapClick{

    private GoogleMap mMap;
    private DatabaseReference stopRef,root,routeRef,currentRouteRef,busRef;
    private Double lat,lng;
    ArrayList<Long> possibleRoutes;
    LocationManager locationManager;
    MapView mapView;
    ArrayList<Bus> busList;
    Bus currentBus;
    Location userLocation,busLocation,startingStopLocalLocation,endingStopLocalLocation,endingLocation,startingLocation;
    Calendar cal;
    Date currentLocalTime;
    SimpleDateFormat date ;
    StopNew startingStop,endingStop;
    ArrayList <Location>locations;
    int iteratorForStart,iteratorForEnd;
    PolylineOptions lineOptions;
    Polyline polyline;
    ArrayList<Polyline>polylines;
    MarkerOptions markerOptions;
    static DecimalFormat df = new DecimalFormat("0.00");
    FragmentTestMapsBinding fragmentTestMapsBinding;
    private RouteButtonAdapter routeButtonAdapter;
    private BusButtonAdapter busButtonAdapter;
    BusButtonDetailsAdapter busButtonDetailsAdapter;
    AlertDialog.Builder alert;
    View alertView;
    AlertDialog alertDialog, ad;
    PopupBusDetailsBinding popupBusDetailsBinding;
    Marker markersss;
    private DatabaseReference locationRef;
// you can get seconds by adding  "...:ss" to it



    private void getRouteDataFromDBNew(StopNew startingStop, StopNew endingStop) {
        startingStopLocalLocation=new Location(startingStop.getName());
        startingStopLocalLocation.setLatitude(startingStop.getLat());
        startingStopLocalLocation.setLongitude(startingStop.getLng());

        endingStopLocalLocation=new Location(endingStop.getName());
        endingStopLocalLocation.setLatitude(endingStop.getLat());
        endingStopLocalLocation.setLongitude(endingStop.getLng());


        double distance= startingStopLocalLocation.distanceTo(endingStopLocalLocation)/10000;
//

        Log.d("Distance","Distance: "+distance);
        routeRef = FirebaseDatabase.getInstance().getReference().child("root").child("routeNew");
        routeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<Integer,String> sourceExistingRoutes = new HashMap<Integer,String>();
                    Map<Integer,String> returnedSourceExistingRoutes = new HashMap<Integer,String>();
                    boolean checkedRoute=false;
                    for (DataSnapshot route : dataSnapshot.getChildren()) {


                        checkedRoute= (detectingAvailableRoutes(route,sourceExistingRoutes,startingStop,endingStop));

                        //find common route for source and destination
                        if(checkedRoute){
                            Log.d("getRouteDataFromDBNew","Route found: true for: "+route.getKey());
                            iteratorForStart=-1;
                            iteratorForEnd=-1;
                            String direction=traverseRoute(route,iteratorForStart,iteratorForEnd,startingStop,endingStop);
//                            Log.d("getRouteDataFromDBNew","Route Direction : "+direction);
//                            Log.d("getRouteDataFromDBNew","Iterator Start : "+iteratorForStart);
//                            Log.d("getRouteDataFromDBNew","Iterator End : "+iteratorForEnd);

                            currentRouteRef = routeRef.child(route.getKey());
                            possibleRoutes.add(Long.valueOf(route.getKey()));
                            locations=new ArrayList<>();
                            if(direction.equals("up")) {

                                Log.d("getDistanceForUp", "Route No.:"+ route.getValue().toString());
                                getDistanceForUp(route);
                                mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                                    @Override
                                    public void onPolylineClick(Polyline polyline) {
                                        Toast.makeText(getContext(),"Route: "+ polyline.getTag().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
//                                possibleRoutes.add(Long.valueOf(route.getKey()));
                            }
                            if(direction.equals("down")) {
                                Log.d("getDistanceForDown", "Route No.:"+ route.getValue().toString());
                                getDistanceForDown(route);
                                mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                                    @Override
                                    public void onPolylineClick(Polyline polyline) {
                                        Toast.makeText(getContext(),"Route: "+ polyline.getTag().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
//                                possibleRoutes.add(Long.valueOf(route.getKey()));
                            }
                            if(!possibleRoutes.contains(Long.valueOf(route.getKey()))){

                                possibleRoutes.add(Long.valueOf(route.getKey()));

                            }

                        }
                        else{
                            Log.d("getRouteDataFromDBNew","Route found: false for: "+route.getKey());

                        }



                    }
//                    getBusList();
                    Log.d("getRouteDataFromDBNew","RouteList"+possibleRoutes.get(0).toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                        for (Long route : possibleRoutes) {
                            if (route == currentBus.getRoute_no()) {

                                busList.add(currentBus);
                            }
                        }
//                        }
                        // Log.d("busLoad",bus.getValue().toString());
                        //Toast.makeText(getActivity(), currentBus.getGroupName(), Toast.LENGTH_SHORT).show();
                    }
                    if (!busList.isEmpty()) {
//                        routeButtonAdapter = new RouteButtonAdapter(getActivity(), busList, (MapClick) TestMapsFragment.this);
//                        fragmentTestMapsBinding.busRV.setAdapter(routeButtonAdapter);
//                        fragmentTestMapsBinding.busRV.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

                        busButtonAdapter=new BusButtonAdapter(getActivity(), busList, (MapClick) TestMapsFragment.this);
                        busButtonDetailsAdapter=new BusButtonDetailsAdapter(getActivity(), busList, (MapClick) TestMapsFragment.this);


                        fragmentTestMapsBinding.busRV.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
                        fragmentTestMapsBinding.busRV.setAdapter(busButtonAdapter);

                        fragmentTestMapsBinding.busDetailsRV.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        fragmentTestMapsBinding.busDetailsRV.setAdapter(busButtonDetailsAdapter);
                        for (Bus bus : busList) {
                            Log.d("getBusList:", "Bus Name: "+bus.getGroupName());
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getDistanceForUp(DataSnapshot route) {
        Log.d("getDistanceForUp", "Route No.:"+ route.getValue().toString());


        currentRouteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot route) {
                if (route.exists()) {
                    locations.clear();
                    lineOptions = new PolylineOptions();

                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    Toast.makeText(getContext(), "Route No.:"+ route.getKey().toString(), Toast.LENGTH_SHORT).show();
                    for (DataSnapshot stops : route.getChildren()) {

                        Log.d("getDistanceForUp", "Stop No.:"+ stops.getValue().toString());
//                        Log.d("getDistanceForUp", "testssss: previously stated" + startingStop.getName());
                        if ((startingStop.getName().equals(stops.child("name").getValue().toString()))&&(Integer.valueOf(stops.child("up").getValue().toString())==1)&&(locations.size())==0){
                            Location location = new Location(startingStop.getName());
                            location.setLatitude(startingStop.getLat());
                            location.setLongitude(startingStop.getLng());
                            locations.add(location);
                            Log.d("getDistanceForUp", "Started getting location"+ stops.child("name").getValue().toString());
                        }
                        if ((Integer.valueOf(stops.child("up").getValue().toString())==0)&&((locations.size())!=0)) {
                            Location location = new Location(stops.child("name").getValue().toString());
                            location.setLatitude(Double.valueOf(stops.child("lat").getValue().toString()));
                            location.setLongitude(Double.valueOf(stops.child("lng").getValue().toString()));
                            locations.add(location);
                            Log.d("getDistanceForUp", "Iteration Running");
                            Log.d("getDistanceForUp", "Current iteration" + stops.child("name").getValue().toString());
                        }
                        if ((endingStop.getName().equals(stops.child("name").getValue().toString()))&&(Integer.valueOf(stops.child("up").getValue().toString())==1)) {
                            Log.d("getDistanceForUp", "Done iterating" + stops.child("name").getValue().toString());
                            int iterator = 0;
                            Log.d("getDistanceForUp", "Size: : " + String.valueOf(locations.size()));
                            double distance = 0;
//                            locations.indexOf();

                            for (Location location : locations) {


                                if(iterator>=locations.indexOf(location.getProvider())&&(iterator<locations.size()-1))
                                {

                                    polyline=mMap.addPolyline(lineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .color(color)
                                            .width(15));
                                   polyline.setClickable(true);
                                   polyline.setTag("Route No.: "+route.getKey());
                                   polylines.add(polyline);


                                    // draw the polyline for the route so far

                                    distance += locations.get(iterator).distanceTo(locations.get(iterator + 1));
//                                    Log.d("getDistanceForUp", "Name: : " + location.getProvider());
                                    Log.d("getDistanceForUp", "From: " + locations.get(iterator).getProvider()+" To: "+locations.get(iterator + 1).getProvider());
                                    Log.d("getDistanceForUp", "Distance: : " + (distance));
                                    iterator++;
                                }
                                if(iterator>=locations.indexOf(location.getProvider())&&(iterator==locations.size()-1)){
                                    polyline=mMap.addPolyline(lineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .color(color)
                                            .width(15));
                                    polyline.setClickable(true);
                                    polyline.setTag("Route No.: "+route.getKey().toString()+" Distance: "+df.format(distance/1000)+"km "+"Time: "+df.format((distance/1000)/GlobalVariables.averageSpeedOfDhaka)+"min");
                                    polylines.add(polyline);

                                }

                            }


                        }


                    }

                }


//                if (route.exists()) {
//
//
//
//                    Random rnd = new Random();
//                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//                    Toast.makeText(getContext(), "Route No.:"+ route.getKey(), Toast.LENGTH_SHORT).show();
//                    lineOptions = new PolylineOptions().width(15).color(color).geodesic(true);
//                    for (DataSnapshot stops : route.getChildren()) {
//
//                        Log.d("getDistanceForUp", "Stop No.:"+ stops.getValue().toString());
////                        Log.d("getDistanceForUp", "testssss: previously stated" + startingStop.getName());
//                        if ((startingStop.getName().equals(Objects.requireNonNull(stops.child("name").getValue()).toString()))&&(Integer.parseInt(stops.child("up").getValue().toString())==1)&&(locations.size())==0){
//                            Location location = new Location(startingStop.getName());
//                            location.setLatitude(startingStop.getLat());
//                            location.setLongitude(startingStop.getLng());
//                            locations.add(location);
//                            Log.d("getDistanceForUp", "Started getting location"+ stops.child("name").getValue().toString());
//                        }
//                        if ((Integer.parseInt(stops.child("up").getValue().toString())==0)&&((locations.size())!=0)) {
//                            Location location = new Location(stops.child("name").getValue().toString());
//                            location.setLatitude(Double.valueOf(stops.child("lat").getValue().toString()));
//                            location.setLongitude(Double.valueOf(stops.child("lng").getValue().toString()));
//                            locations.add(location);
//                            Log.d("getDistanceForUp", "Iteration Running");
//                            Log.d("getDistanceForUp", "Current iteration" + stops.child("name").getValue().toString());
//                        }
//                        if ((endingStop.getName().equals(stops.child("name").getValue().toString()))&&(Integer.parseInt(stops.child("up").getValue().toString())==1)) {
//                            Log.d("getDistanceForUp", "Done iterating" + stops.child("name").getValue().toString());
//                            int iterator = 0;
//                            Log.d("getDistanceForUp", "Size: : " + String.valueOf(locations.size()));
//                            double distance = 0;
////                            locations.indexOf();
////                            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
//
//                            for (Location location : locations) {
//
//
//
//                                if(iterator>=locations.indexOf(location.getProvider())&&(iterator<locations.size()-1))
//                                {
//
//                                   lineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
//
//
//
//                                    // draw the polyline for the route so far
//
//                                    distance += locations.get(iterator).distanceTo(locations.get(iterator + 1));
////                                    Log.d("getDistanceForUp", "Name: : " + location.getProvider());
//                                    Log.d("getDistanceForUp", "From: " + locations.get(iterator).getProvider()+" To: "+locations.get(iterator + 1).getProvider());
//                                    Log.d("getDistanceForUp", "Distance: : " + (distance));
//                                    iterator++;
//                                }if(iterator>=locations.indexOf(location.getProvider())&&(iterator>=locations.size()-1)){
////                                    lineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
//
//
//                                }
//
//                            }
//
//
//
//                        }
//
//
//                    }
//
//                    mMap.addPolyline(lineOptions);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDistanceForDown(DataSnapshot route) {
        Log.d("getDistanceForDown", "Route No.:"+ route.getValue().toString());


        currentRouteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot route) {
                if (route.exists()) {
                    locations.clear();
                    lineOptions = new PolylineOptions();

                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    Toast.makeText(getContext(), "Route No.:"+ route.getKey().toString(), Toast.LENGTH_SHORT).show();
                    for (DataSnapshot stops : route.getChildren()) {

                        Log.d("getDistanceForDown", "Stop No.:"+ stops.getValue().toString());
//                        Log.d("getDistanceForUp", "testssss: previously stated" + startingStop.getName());
                        if ((endingStop.getName().equals(stops.child("name").getValue().toString()))&&(Integer.valueOf(stops.child("down").getValue().toString())==1)&&(locations.size())==0){
                            Location location = new Location(startingStop.getName());
                            location.setLatitude(startingStop.getLat());
                            location.setLongitude(startingStop.getLng());
                            locations.add(location);
                            Log.d("getDistanceForDown", "Started getting location"+ stops.child("name").getValue().toString());
                        }
                        if ((Integer.valueOf(stops.child("down").getValue().toString())==0)&&((locations.size())!=0)) {
                            Location location = new Location(stops.child("name").getValue().toString());
                            location.setLatitude(Double.valueOf(stops.child("lat").getValue().toString()));
                            location.setLongitude(Double.valueOf(stops.child("lng").getValue().toString()));
                            locations.add(location);
                            Log.d("getDistanceForDown", "Iteration Running");
                            Log.d("getDistanceFoDown", "Current iteration" + stops.child("name").getValue().toString());

                        }
                        if ((startingStop.getName().equals(stops.child("name").getValue().toString()))&&(Integer.valueOf(stops.child("up").getValue().toString())==1)) {
                            Log.d("getDistanceForDown", "Done Iterating: " + stops.child("name").getValue().toString());
                            int iterator = 0;
                            Log.d("getDistanceForDown", "Size: : " + String.valueOf(locations.size()));
                            double distance = 0;
//                            locations.indexOf();

                            for (Location location : locations) {


                                if(iterator>=locations.indexOf(location.getProvider())&&(iterator<locations.size()-1))
                                {

                                    polyline=mMap.addPolyline(lineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .color(color)
                                            .width(15));
                                    polyline.setClickable(true);
                                    polyline.setTag("Route No.: "+route.getKey());
                                   polylines.add(polyline);


                                    // draw the polyline for the route so far

                                    distance += locations.get(iterator).distanceTo(locations.get(iterator + 1));
//                                    Log.d("getDistanceForUp", "Name: : " + location.getProvider());
                                    Log.d("getDistanceForDown", "From: " + locations.get(iterator).getProvider()+" To: "+locations.get(iterator + 1).getProvider());
                                    Log.d("getDistanceForDown", "Distance: : " + (distance));
                                    iterator++;
                                }if(iterator>=locations.indexOf(location.getProvider())&&(iterator==locations.size()-1)){
                                    polyline=mMap.addPolyline(lineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .color(color)
                                            .width(15));
                                    polyline.setClickable(true);
                                    polyline.setTag("Route No.: "+route.getKey().toString()+" Distance: "+df.format(distance/1000)+" km");

                                    polylines.add(polyline);

                                }

                            }


                        }


                    }

                }


//                if (route.exists()) {
//
//
//
//                    Random rnd = new Random();
//                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//                    Toast.makeText(getContext(), "Route No.:"+ route.getKey(), Toast.LENGTH_SHORT).show();
//                    lineOptions = new PolylineOptions().width(15).color(color).geodesic(true);
//                    for (DataSnapshot stops : route.getChildren()) {
//
//                        Log.d("getDistanceForUp", "Stop No.:"+ stops.getValue().toString());
////                        Log.d("getDistanceForUp", "testssss: previously stated" + startingStop.getName());
//                        if ((startingStop.getName().equals(Objects.requireNonNull(stops.child("name").getValue()).toString()))&&(Integer.parseInt(stops.child("up").getValue().toString())==1)&&(locations.size())==0){
//                            Location location = new Location(startingStop.getName());
//                            location.setLatitude(startingStop.getLat());
//                            location.setLongitude(startingStop.getLng());
//                            locations.add(location);
//                            Log.d("getDistanceForUp", "Started getting location"+ stops.child("name").getValue().toString());
//                        }
//                        if ((Integer.parseInt(stops.child("up").getValue().toString())==0)&&((locations.size())!=0)) {
//                            Location location = new Location(stops.child("name").getValue().toString());
//                            location.setLatitude(Double.valueOf(stops.child("lat").getValue().toString()));
//                            location.setLongitude(Double.valueOf(stops.child("lng").getValue().toString()));
//                            locations.add(location);
//                            Log.d("getDistanceForUp", "Iteration Running");
//                            Log.d("getDistanceForUp", "Current iteration" + stops.child("name").getValue().toString());
//                        }
//                        if ((endingStop.getName().equals(stops.child("name").getValue().toString()))&&(Integer.parseInt(stops.child("up").getValue().toString())==1)) {
//                            Log.d("getDistanceForUp", "Done iterating" + stops.child("name").getValue().toString());
//                            int iterator = 0;
//                            Log.d("getDistanceForUp", "Size: : " + String.valueOf(locations.size()));
//                            double distance = 0;
////                            locations.indexOf();
////                            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
//
//                            for (Location location : locations) {
//
//
//
//                                if(iterator>=locations.indexOf(location.getProvider())&&(iterator<locations.size()-1))
//                                {
//
//                                   lineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
//
//
//
//                                    // draw the polyline for the route so far
//
//                                    distance += locations.get(iterator).distanceTo(locations.get(iterator + 1));
////                                    Log.d("getDistanceForUp", "Name: : " + location.getProvider());
//                                    Log.d("getDistanceForUp", "From: " + locations.get(iterator).getProvider()+" To: "+locations.get(iterator + 1).getProvider());
//                                    Log.d("getDistanceForUp", "Distance: : " + (distance));
//                                    iterator++;
//                                }if(iterator>=locations.indexOf(location.getProvider())&&(iterator>=locations.size()-1)){
////                                    lineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
//
//
//                                }
//
//                            }
//
//
//
//                        }
//
//
//                    }
//
//                    mMap.addPolyline(lineOptions);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private String traverseRoute(DataSnapshot route, int iteratorForStart, int iteratorForEnd, StopNew startingStop, StopNew endingStop) {

        Location startingStopLocation = new Location(startingStop.getName());
        startingStopLocation.setLatitude(startingStop.getLat());
        startingStopLocation.setLongitude(startingStop.getLng());


        ArrayList<Double> sourceDownUpDistance=new ArrayList();
        for (DataSnapshot stops : route.getChildren()) {


            Log.d("traverseRoute","start "+iteratorForStart);
            Log.d("traverseRoute","end "+iteratorForEnd);


                if((startingStop.getName().equals(stops.child("name").getValue().toString())))  {

                    iteratorForStart=Integer.valueOf(stops.getKey().toString());

                        if(Integer.valueOf(stops.child("up").getValue().toString())==1){
                            Log.d("traverseRoute", "stop: " + stops.child("name").getValue().toString());
                            Log.d("traverseRoute", "Route: " + stops.child("route").getValue().toString());
                            Log.d("traverseRoute", "up: " + stops.child("up").getValue().toString());
                            Log.d("traverseRoute", "down: " + stops.child("down").getValue().toString());
                            Log.d("traverseRoute", "iterator Start: " + iteratorForStart);
                            startingStopLocalLocation = new Location(stops.child("name").getValue().toString());
                            startingStopLocalLocation.setLatitude(Double.valueOf(stops.child("lat").getValue().toString()));
                            startingStopLocalLocation.setLongitude(Double.valueOf(stops.child("lng").getValue().toString()));

                            double distanceStart = startingStopLocation.distanceTo(startingStopLocalLocation);
                            Log.d("traverseRoute", "Distance Start Up: " + distanceStart);
                            sourceDownUpDistance.add(1,distanceStart);
                        }
                        if(Integer.valueOf(stops.child("down").getValue().toString())==1){

                            Log.d("traverseRoute", "stop: " + stops.child("name").getValue().toString());
                            Log.d("traverseRoute", "Route: " + stops.child("route").getValue().toString());
                            Log.d("traverseRoute", "up: " + stops.child("up").getValue().toString());
                            Log.d("traverseRoute", "down: " + stops.child("down").getValue().toString());
                            Log.d("traverseRoute", "iterator Start: " + iteratorForStart);
                            startingStopLocalLocation = new Location(stops.child("name").getValue().toString());
                            startingStopLocalLocation.setLatitude(Double.valueOf(stops.child("lat").getValue().toString()));
                            startingStopLocalLocation.setLongitude(Double.valueOf(stops.child("lng").getValue().toString()));

                            double distanceStart = startingStopLocation.distanceTo(startingStopLocalLocation);
                            Log.d("traverseRoute", "Distance Start Down: " + distanceStart);
                            sourceDownUpDistance.add(0,distanceStart);
                        }


                }
                if((this.endingStop.getName().equals(stops.child("name").getValue().toString())) &&(iteratorForEnd==-1))
                {
                    iteratorForEnd=Integer.valueOf(stops.getKey());
                    if(iteratorForEnd!=-1){

                        Log.d("traverseRoute","iterator End: "+iteratorForEnd);
                        if(Integer.valueOf(stops.child("down").getValue().toString())==1) {
                            Log.d("traverseRoute","stop"+stops.child("name").getValue().toString());
                            Log.d("traverseRoute","Route"+stops.child("route").getValue().toString());
                            Log.d("traverseRoute", "up: " + stops.child("up").getValue().toString());
                            Log.d("traverseRoute", "down: " + stops.child("down").getValue().toString());
                            endingStopLocalLocation = new Location(endingStop.getName());
                            endingStopLocalLocation.setLatitude(endingStop.getLat());
                            endingStopLocalLocation.setLongitude(endingStop.getLng());
                            double distanceEnd = startingStopLocation.distanceTo(endingStopLocalLocation);
                            Log.d("traverseRoute", "Distance End Down: " + distanceEnd);
                        }
                        if(Integer.valueOf(stops.child("up").getValue().toString())==1) {
                            Log.d("traverseRoute","stop"+stops.child("name").getValue().toString());
                            Log.d("traverseRoute","Route"+stops.child("route").getValue().toString());
                            Log.d("traverseRoute", "up: " + stops.child("up").getValue().toString());
                            Log.d("traverseRoute", "down: " + stops.child("down").getValue().toString());
                            endingStopLocalLocation = new Location(endingStop.getName());
                            endingStopLocalLocation.setLatitude(endingStop.getLat());
                            endingStopLocalLocation.setLongitude(endingStop.getLng());
                            double distanceEnd = startingStopLocation.distanceTo(endingStopLocalLocation);
                            Log.d("traverseRoute", "Distance End Up: " + distanceEnd);
                        }
                    }

                }




        }
        double downDistance=sourceDownUpDistance.get(0);
        double upDistance=sourceDownUpDistance.get(1);
        String selectedDirection = null;
        if(upDistance<downDistance){
            Log.d("traverseRoute", "Selected: Up" );
            selectedDirection= "up";
        }
        if(downDistance<upDistance){
            Log.d("traverseRoute", "Selected: Down" );
            selectedDirection= "down";
        }


        return selectedDirection;
    }

    private boolean detectingAvailableRoutes(DataSnapshot route, Map<Integer, String> sourceExistingRoutes, StopNew startingStop, StopNew endingStop) {


        Log.d("detectingRoutes","Check source: "+startingStop.getName()+startingStop.getLat()+startingStop.getLng());
        Log.d("detectingRoutes","Check destination: "+endingStop.getName()+endingStop.getLat()+endingStop.getLng());
        boolean sourceFound=false;
        boolean destinationFound=false;
        for (DataSnapshot stops:route.getChildren()) {
            if((stops.child("name").getValue().toString().equals(startingStop.getName())))
            {
                sourceFound=true;
            }
            if((stops.child("name").getValue().toString().equals(endingStop.getName())))
            {
                destinationFound=true;
            }


        }
        if((destinationFound==true) && (sourceFound==true))
        {
            Log.d("detectedRoute","detectedRoute: "+route.getValue().toString());


            return true;

        }else{
            return  false;
        }

    }


    private void getBusLocationFromDB() {

        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("locations");
        stopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot bus : dataSnapshot.getChildren()) {

                        String licensePlate = bus.child("licensePlate").getValue().toString();
                        lat = Double.valueOf(bus.child("lat").getValue().toString());
                        lng = Double.valueOf(bus.child("lng").getValue().toString());
                        LatLng currentBusLocation = new LatLng(lat, lng);
                        MarkerOptions markerOptions= new MarkerOptions().position(currentBusLocation).title(licensePlate);

                        mMap.addMarker(markerOptions.zIndex(1)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_red));
                        busLocation=new Location(licensePlate);
                        busLocation.setLatitude(lat);
                        busLocation.setLongitude (lng);
                        double distanceInMeters=busLocation.distanceTo(userLocation);
//                        double speed=Double.valueOf(busLocation.getSpeed());

                        Log.d("Bus","License Plate: "+licensePlate);
                        Log.d("Bus","Estimated distanceInMeters: "+distanceInMeters);
//                        Log.d("Route","Estimated Speed: "+speed);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getBusLocationFromDB(String groupId) {





        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("locations");
        stopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {



                    for (DataSnapshot busLocationSnapshot : dataSnapshot.getChildren()) {

                        String licensePlate = busLocationSnapshot.child("licensePlate").getValue().toString();
                        lat = Double.valueOf(busLocationSnapshot.child("lat").getValue().toString());
                        lng = Double.valueOf(busLocationSnapshot.child("lng").getValue().toString());
                        LatLng currentBusLocation = new LatLng(lat, lng);
                        MarkerOptions markerOptions= new MarkerOptions().position(currentBusLocation).title(licensePlate);
//                        mMap.addMarker(markerOptions).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
//
                        busLocation=new Location(licensePlate);
                        busLocation.setLatitude(lat);
                        busLocation.setLongitude (lng);
                        int localGroupId =Integer.valueOf(busLocationSnapshot.child("groupId").getValue().toString());
                        int passedGroupId =Integer.valueOf(groupId);
                        double distanceInMeters=busLocation.distanceTo(userLocation);
                        double distanceTowardsDestination=busLocation.distanceTo(endingLocation);

//                        double speed=Double.valueOf(busLocation.getSpeed());

                        Log.d("Bus","License Plate: "+licensePlate);
                        Log.d("Bus","Estimated distanceInMeters: "+distanceInMeters);
                        int zIndex=0;
                        if((passedGroupId==0)&&(passedGroupId==localGroupId)){
                            licensePlate = busLocationSnapshot.child("licensePlate").getValue().toString();
                            lat = Double.valueOf(busLocationSnapshot.child("lat").getValue().toString());
                            lng = Double.valueOf(busLocationSnapshot.child("lng").getValue().toString());

                            busLocation=new Location(licensePlate);
                            busLocation.setLatitude(lat);
                            busLocation.setLongitude (lng);


                            distanceInMeters=busLocation.distanceTo(userLocation);
                            distanceTowardsDestination=busLocation.distanceTo(endingLocation);

                            double estimatedFare=Math.floor((distanceTowardsDestination/1000)*GlobalVariables.fareNormal);
                            if(estimatedFare<7)
                            {
                                estimatedFare=7;
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_purple));
                                marker.setTag(licensePlate);
                            }else{
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_purple));
                                marker.setTag(licensePlate);
                            }

                        }
                        if((passedGroupId==9)&&(passedGroupId==localGroupId)){
                            licensePlate = busLocationSnapshot.child("licensePlate").getValue().toString();
                            lat = Double.valueOf(busLocationSnapshot.child("lat").getValue().toString());
                            lng = Double.valueOf(busLocationSnapshot.child("lng").getValue().toString());
                            currentBusLocation = new LatLng(lat, lng);

                            busLocation=new Location(licensePlate);
                            busLocation.setLatitude(lat);
                            busLocation.setLongitude (lng);

                            distanceInMeters=busLocation.distanceTo(userLocation);
                            distanceTowardsDestination=busLocation.distanceTo(endingLocation);

                            double estimatedFare=Math.floor((distanceTowardsDestination/1000)*GlobalVariables.fareNormal);
                            if(estimatedFare<7)
                            {
                                estimatedFare=7;
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_sky_blue));
                                marker.setTag(licensePlate);
                            }else{
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex).title(String.valueOf(df.format(distanceInMeters/1000))+" km away "+df.format(estimatedTimeForArrival)+"min away"+busLocationSnapshot.child("availableSeats").getValue().toString()+" seats"+" "+df.format(estimatedFare)+" tk approx."+"Plate: "+busLocationSnapshot.child("licensePlate").getValue().toString() ));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_sky_blue));
                                marker.setTag(licensePlate);
                            }


                        }
                        if((passedGroupId==10)&&(passedGroupId==localGroupId)){
                            licensePlate = busLocationSnapshot.child("licensePlate").getValue().toString();
                            lat = Double.valueOf(busLocationSnapshot.child("lat").getValue().toString());
                            lng = Double.valueOf(busLocationSnapshot.child("lng").getValue().toString());
                            currentBusLocation = new LatLng(lat, lng);

                            busLocation=new Location(licensePlate);
                            busLocation.setLatitude(lat);
                            busLocation.setLongitude (lng);

                            distanceInMeters=busLocation.distanceTo(userLocation);
                            distanceTowardsDestination=busLocation.distanceTo(endingLocation);

                            double estimatedFare=Math.floor((distanceTowardsDestination/1000)*GlobalVariables.fareNormal);
                            if(estimatedFare<7)
                            {
                                estimatedFare=7;
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_orange));
                                marker.setTag(licensePlate);
                            }else{
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex).title(String.valueOf(df.format(distanceInMeters/1000))+" km away "+df.format(estimatedTimeForArrival)+"min away"+busLocationSnapshot.child("availableSeats").getValue().toString()+" seats"+" "+df.format(estimatedFare)+" tk approx."+"Plate: "+busLocationSnapshot.child("licensePlate").getValue().toString() ));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_orange));
                                marker.setTag(licensePlate);
                            }
                        }
                        if((passedGroupId==16)&&(passedGroupId==localGroupId)){
                            licensePlate = busLocationSnapshot.child("licensePlate").getValue().toString();
                            lat = Double.valueOf(busLocationSnapshot.child("lat").getValue().toString());
                            lng = Double.valueOf(busLocationSnapshot.child("lng").getValue().toString());
                            currentBusLocation = new LatLng(lat, lng);

                            busLocation=new Location(licensePlate);
                            busLocation.setLatitude(lat);
                            busLocation.setLongitude (lng);


                            distanceInMeters=busLocation.distanceTo(userLocation);
                            distanceTowardsDestination=busLocation.distanceTo(endingLocation);

                            double estimatedFare=Math.floor((distanceTowardsDestination/1000)*GlobalVariables.fareNormal);
                            if(estimatedFare<7)
                            {
                                estimatedFare=7;
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_green_2));
                                marker.setTag(licensePlate);
                            }else{
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex).title(String.valueOf(df.format(distanceInMeters/1000))+" km away "+df.format(estimatedTimeForArrival)+"min away"+busLocationSnapshot.child("availableSeats").getValue().toString()+" seats"+" "+df.format(estimatedFare)+" tk approx."+"Plate: "+busLocationSnapshot.child("licensePlate").getValue().toString() ));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_green_2));
                                marker.setTag(licensePlate);
                            }
                        }
                        if((passedGroupId==11)&&(passedGroupId==localGroupId)){
                            licensePlate = busLocationSnapshot.child("licensePlate").getValue().toString();
                            lat = Double.valueOf(busLocationSnapshot.child("lat").getValue().toString());
                            lng = Double.valueOf(busLocationSnapshot.child("lng").getValue().toString());
                            currentBusLocation = new LatLng(lat, lng);

                            busLocation=new Location(licensePlate);
                            busLocation.setLatitude(lat);
                            busLocation.setLongitude (lng);

                            distanceInMeters=busLocation.distanceTo(userLocation);
                            distanceTowardsDestination=busLocation.distanceTo(endingLocation);

                            double estimatedFare=Math.floor((distanceTowardsDestination/1000)*GlobalVariables.fareNormal);
                            if(estimatedFare<7)
                            {
                                estimatedFare=7;
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_brown));
                                marker.setTag(licensePlate);
                            }else{
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_brown));
                                marker.setTag(licensePlate);
                            }

                        }
                        if((passedGroupId==12)&&(passedGroupId==localGroupId)){
                            licensePlate = busLocationSnapshot.child("licensePlate").getValue().toString();
                            lat = Double.valueOf(busLocationSnapshot.child("lat").getValue().toString());
                            lng = Double.valueOf(busLocationSnapshot.child("lng").getValue().toString());
                            currentBusLocation = new LatLng(lat, lng);

                            busLocation=new Location(licensePlate);
                            busLocation.setLatitude(lat);
                            busLocation.setLongitude (lng);


                            distanceInMeters=busLocation.distanceTo(userLocation);
                            distanceTowardsDestination=busLocation.distanceTo(endingLocation);

                            double estimatedFare=Math.floor((distanceTowardsDestination/1000)*GlobalVariables.fareNormal);
                            if(estimatedFare<7)
                            {
                                estimatedFare=7;
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_yellow));
                                marker.setTag(licensePlate);
                            }else{
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_yellow));
                                marker.setTag(licensePlate);
                            }
                        }
                        if((passedGroupId==17)&&(passedGroupId==localGroupId)){
                            licensePlate = busLocationSnapshot.child("licensePlate").getValue().toString();
                            lat = Double.valueOf(busLocationSnapshot.child("lat").getValue().toString());
                            lng = Double.valueOf(busLocationSnapshot.child("lng").getValue().toString());
                            currentBusLocation = new LatLng(lat, lng);

                            busLocation=new Location(licensePlate);
                            busLocation.setLatitude(lat);
                            busLocation.setLongitude (lng);

                            distanceInMeters=busLocation.distanceTo(userLocation);
                            distanceTowardsDestination=busLocation.distanceTo(endingLocation);

                            double estimatedFare=Math.floor((distanceTowardsDestination/1000)*GlobalVariables.fareNormal);
                            if(estimatedFare<7)
                            {
                                estimatedFare=7;
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_navy_blue));
                                marker.setTag(licensePlate);
                            }else{
                                double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);
                                zIndex=2;

                                Marker marker=mMap.addMarker(markerOptions.zIndex(zIndex));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_navy_blue));
                                marker.setTag(licensePlate);
                            }

                        }

//                        Log.d("Route","Estimated Speed: "+speed);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        possibleRoutes=new ArrayList<Long>();
        root= FirebaseDatabase.getInstance().getReference().child("root");
        startingStop = new StopNew();
        endingStop = new StopNew();
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        currentLocalTime = cal.getTime();
        date =  new SimpleDateFormat("HH:mm:ss");

// you can get seconds by adding  "...:ss" to it
//        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        String localTime = date.format(currentLocalTime);


        Log.d("Route", "Current Time In Oncreate: "+ localTime);


        fragmentTestMapsBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_test_maps, container, false);


        mapView=fragmentTestMapsBinding.map;
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Add a marker in Sydney and move the camera
                LatLng Shyamoli = new LatLng(23.774804, 90.365533);
//            mMap.addMarker(new MarkerOptions().position(Shyamoli).title("Me")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.man));
//                userLocation=new Location("dummyprovider");
//                userLocation.setLatitude(Shyamoli.latitude);
//                userLocation.setLongitude (Shyamoli.longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(Shyamoli));
                mMap.setTrafficEnabled(true);
                mMap.setMinZoomPreference(12);

////            enable for down
//            endingStop.setName("Shishu Mela");
//            endingStop.setLat(23.77487089842745);
//            endingStop.setLng(90.36569494679532);

////            enable for up
//            startingStop.setName("Shishu Mela");
//            startingStop.setLat(23.773018887636074);
//            startingStop.setLng(90.36722380809236);

                //shyamoli
//            startingStop.setName("Me");
//            startingStop.setLat(23.774804);
//            startingStop.setLng(90.365533);


////////            enable for up
//            startingStop.setName("Shyamoli");
//            startingStop.setLat(23.774545853558333);
//            startingStop.setLng(90.3658781270351);

////            enable for down
//                endingStop.setName("Shyamoli");
//                endingStop.setLat(23.774905990568854);
//                endingStop.setLng(90.365667223853);

                //            enable for up
                startingStop.setName(GlobalVariables.sourceNewName);
                startingStop.setLat(GlobalVariables.sourceNewLat);
                startingStop.setLng(GlobalVariables.sourceNewLng);

////            enable for up
//            startingStop.setName("Mohammadpur");
//            startingStop.setLat(23.756961809167528);
//            startingStop.setLng(90.36157122318448);

////            enable for down
//            endingStop.setName("Mohammadpur");
//            endingStop.setLat(23.756998872575224);
//            endingStop.setLng(90.36161473701699);


                //enable for down
//            endingStop.setName("Kolabagan");
//            endingStop.setLat(23.747854936993697);
//            endingStop.setLng(90.38027281299742);

                //enable for up
//            endingStop.setName("Kolabagan");
//            endingStop.setLat(23.748978125411217);
//            endingStop.setLng(90.37943700869494);

//                //enable for up
//            endingStop.setName("Science Laboratory");
//            endingStop.setLat(23.73879827872002);
//            endingStop.setLng(90.38395013170603);
                //enable for up
                endingStop.setName(GlobalVariables.destinationNewName);
                endingStop.setLat(GlobalVariables.destinationNewLat);
                endingStop.setLng(GlobalVariables.destinationNewLng);
                busList = new ArrayList<>();
                //            //enable for down
//                startingStop.setName("Science Laboratory");
//                startingStop.setLat(23.73917813218871);
//                startingStop.setLng(90.38343505809199);


                userLocation=new Location("userLocation");
                userLocation.setLatitude(GlobalVariables.sourceNewLat);
                userLocation.setLongitude (GlobalVariables.sourceNewLng);
                mMap.addMarker(new MarkerOptions().position( new LatLng(GlobalVariables.sourceNewLat,GlobalVariables.sourceNewLng)).title("Me")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.human));

                endingLocation=new Location("destination");
                endingLocation.setLatitude(GlobalVariables.destinationNewLat);
                endingLocation.setLongitude (GlobalVariables.destinationNewLng);


                mMap.addMarker(new MarkerOptions().position( new LatLng(GlobalVariables.destinationNewLat,GlobalVariables.destinationNewLng)).title(GlobalVariables.destinationNewName)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.end));


                polylines=new ArrayList();
//          getRouteDataFromDB(possibleRoutes);
                getRouteDataFromDBNew(startingStop,endingStop);
//                getBusList();

                getBusLocationFromDB();
                getBusList();
                Log.d("onMapReady","BusList"+busList.toString());
                mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                    @Override
                    public void onPolylineClick(Polyline polyline) {
                        Toast.makeText(getContext(), polyline.getTag().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        if(marker.getTag()!=null)
                        {
                            alertDialogBusDetails(marker.getTag().toString());
                        }

                        return false;
                    }
                });


            }
        });

        return fragmentTestMapsBinding.getRoot();

    }


    @Override
    public void onClick(String routeNo) {

        Toast.makeText(getActivity(), "On Click Route No.: " + routeNo, Toast.LENGTH_SHORT).show();
//        if (!commonRoutes.isEmpty()) {
//            hideLayers();
//            setProperties(Integer.parseInt(s));
//        }
                if (!polylines.isEmpty()) {
                    Log.d("onClick","polyline size: "+polylines.size());
        //            hideLayers();
        //            setProperties(Integer.parseInt(s));
                    for (Polyline polyline: polylines) {
                        Log.d("onClick","polyline route No.: "+polyline.getTag());
                        if(polyline.getTag().toString().contains("Route No.: "+routeNo))
                        {

                            Log.d("onClick","if: polyline route No.: "+polyline.getTag());
                            polyline.setWidth(25);

                        }
                        if(!polyline.getTag().toString().contains("Route No.: "+routeNo))
                        {

                            Log.d("onClick","else: polyline route No.: "+polyline.getTag());
                            polyline.setWidth(0);



                        }

                    }
                    }
                else{
                    Log.d("onClick","polyline size: "+polylines.size());
                }



    }


    @Override
    public void onClick(String routeNo,String groupId) {

        Toast.makeText(getActivity(), "On Click Route No.: " + routeNo, Toast.LENGTH_SHORT).show();

        getBusLocationFromDB(groupId);
//        if (!commonRoutes.isEmpty()) {
//            hideLayers();
//            setProperties(Integer.parseInt(s));
//        }
        if (!polylines.isEmpty()) {
            Log.d("onClick","polyline size: "+polylines.size());
            //            hideLayers();
            //            setProperties(Integer.parseInt(s));
            for (Polyline polyline: polylines) {
                Log.d("onClick","polyline route No.: "+polyline.getTag());
                if(polyline.getTag().toString().contains("Route No.: "+routeNo))
                {

                    Log.d("onClick","if: polyline route No.: "+polyline.getTag());
                    polyline.setWidth(25);

                }
                if(!polyline.getTag().toString().contains("Route No.: "+routeNo))
                {

                    Log.d("onClick","else: polyline route No.: "+polyline.getTag());
                    polyline.setWidth(0);



                }

            }
        }
        else{
            Log.d("onClick","polyline size: "+polylines.size());
        }



    }

    private void alertDialogBusDetails(String passedLicensePlate) {



        alert = new AlertDialog.Builder(getActivity());
        alertView = getLayoutInflater().inflate(R.layout.popup_bus_details, null);

        alertDialog = alert.create();

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(null);

        popupBusDetailsBinding = DataBindingUtil.bind(alertView);

        locationRef = FirebaseDatabase.getInstance().getReference().child("root").child("locations");
        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot bus : dataSnapshot.getChildren()) {

                        String licensePlate = bus.child("licensePlate").getValue().toString();
                        if(passedLicensePlate.equals(licensePlate))
                        {
                            lat = Double.valueOf(bus.child("lat").getValue().toString());
                            lng = Double.valueOf(bus.child("lng").getValue().toString());

                            String licenseNo=bus.child("licenseNo").getValue().toString();
                            String availableSeats=bus.child("availableSeats").getValue().toString();
                            busLocation=new Location(licensePlate);
                            busLocation.setLatitude(lat);
                            busLocation.setLongitude (lng);


                            double distanceInMeters = busLocation.distanceTo(userLocation);
                            double distanceFromSourceToDestination=userLocation.distanceTo(endingLocation);
                            double distanceTowardsDestination=(busLocation.distanceTo(endingLocation))/1000;
                            double estimatedFare=Math.floor((distanceFromSourceToDestination/1000)*GlobalVariables.fareNormal);
                            double estimatedTimeForArrival=((distanceInMeters/1000)/GlobalVariables.averageSpeedOfDhaka);

                            popupBusDetailsBinding.licensePlateTV.setText("License Plate: "+licensePlate);
                            popupBusDetailsBinding.licenseNoTV.setText("License No.: "+licenseNo);
                            popupBusDetailsBinding.busDistanceToUserTV.setText("Bus Distance: "+df.format(distanceInMeters/1000)+" km away");
                            popupBusDetailsBinding.arrivalTimeTV.setText("Approx Arrival Time: "+df.format(estimatedTimeForArrival)+" minutes");
                            popupBusDetailsBinding.availableSeatsTV.setText("Available Seats: "+availableSeats);
                            popupBusDetailsBinding.busDistanceToDestinationTV.setText("Distance to Destination: "+df.format(distanceTowardsDestination)+" km");

                            if(estimatedFare<7){
                                popupBusDetailsBinding.expectedFare.setText("Approx fare: 7 tk");
                            }else{
                                popupBusDetailsBinding.expectedFare.setText("Approx fare: "+String.valueOf(estimatedFare)+" tk");
                            }


                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        popupBusDetailsBinding.arrivalTimeTV.setText("Works");
        alert.setView(alertView);

        ad = alert.show();


        // alertDialog.show();

    }



}