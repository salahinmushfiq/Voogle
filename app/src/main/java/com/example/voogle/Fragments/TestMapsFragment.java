package com.example.voogle.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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

import com.example.voogle.PojoClasses.StopNew;
import com.example.voogle.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.Objects;
import java.util.Random;

public class TestMapsFragment extends Fragment {

    private GoogleMap mMap;
    private DatabaseReference stopRef,root,routeRef,currentRouteRef;
    private Double lat,lng;
    ArrayList <Integer>possibleRoutes;
    LocationManager locationManager;
    Location userLocation,busLocation,startingStopLocalLocation,endingStopLocalLocation;
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
    static DecimalFormat df = new DecimalFormat("0.000");
// you can get seconds by adding  "...:ss" to it

    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
            LatLng Shyamoli = new LatLng(23.774804, 90.365533);
//            mMap.addMarker(new MarkerOptions().position(Shyamoli).title("Me")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.man));
            userLocation=new Location("dummyprovider");
            userLocation.setLatitude(Shyamoli.latitude);
            userLocation.setLongitude (Shyamoli.longitude);
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
            endingStop.setName("Shyamoli");
            endingStop.setLat(23.774905990568854);
            endingStop.setLng(90.365667223853);


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

            //enable for up
//            endingStop.setName("Science Laboratory");
//            endingStop.setLat(23.73879827872002);
//            endingStop.setLng(90.38395013170603);

            //            //enable for down
            startingStop.setName("Science Laboratory");
            startingStop.setLat(23.73917813218871);
            startingStop.setLng(90.38343505809199);


            userLocation=new Location("dummyprovider");
            userLocation.setLatitude(startingStop.getLat());
            userLocation.setLongitude (startingStop.getLng());
            mMap.addMarker(new MarkerOptions().position( new LatLng(startingStop.getLat(), startingStop.getLng())).title("Me")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.man));

            polylines=new ArrayList();
//          getRouteDataFromDB(possibleRoutes);
            getRouteDataFromDBNew(startingStop,endingStop);


            getBusLocationFromDB();
            mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                @Override
                public void onPolylineClick(Polyline polyline) {
                    Toast.makeText(getContext(), polyline.getTag().toString(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    };



//    private void getRouteDataFromDB(ArrayList<Integer> possibleRoutes) {
//
//
//        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("routeNew");
//        stopRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot route : dataSnapshot.getChildren()) {
//
//
//                        Log.d ("Route: ",route.getKey());
//                        for (int routeNo:possibleRoutes) {
//                            if(Integer.valueOf(route.getKey())==routeNo){
//                                for (DataSnapshot stops:route.getChildren())
//                                {
//
//                                    Log.d ("Route: ","stop: "+stops.getValue().toString());
//                                    Log.d ("Route: ","name: "+stops.child("name").getValue().toString());
//                                    Log.d ("Route: ","lat: "+stops.child("lat").getValue().toString());
//                                    Log.d ("Route: ","lng: "+stops.child("lng").getValue().toString());
//
//
//                                    LatLng position = new LatLng(Double.valueOf(stops.child("lat").getValue().toString()), Double.valueOf(stops.child("lng").getValue().toString()));
//                                    mMap.addMarker(new MarkerOptions().position(position).title("name: "+stops.child("name").getValue().toString()+"      Route No.:"+stops.child("route").getValue().toString())).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pointer));
//
//                                }
//                                Log.d("Route",route.getValue().toString());
//                            }
//                        }
//
//                    }
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

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
                            Log.d("getRouteDataFromDBNew","Route Direction : "+direction);
                            Log.d("getRouteDataFromDBNew","Iterator Start : "+iteratorForStart);
                            Log.d("getRouteDataFromDBNew","Iterator End : "+iteratorForEnd);

                            currentRouteRef = routeRef.child(route.getKey());
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

                            }


                        }
                        else{
                            Log.d("getRouteDataFromDBNew","Route found: false for: "+route.getKey());

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
                                   polyline.setTag(route.getKey());
//                                   polylines.add(polyline);


                                    // draw the polyline for the route so far

                                    distance += locations.get(iterator).distanceTo(locations.get(iterator + 1));
//                                    Log.d("getDistanceForUp", "Name: : " + location.getProvider());
                                    Log.d("getDistanceForUp", "From: " + locations.get(iterator).getProvider()+" To: "+locations.get(iterator + 1).getProvider());
                                    Log.d("getDistanceForUp", "Distance: : " + (distance));
                                    iterator++;
                                }if(iterator>=locations.indexOf(location.getProvider())&&(iterator==locations.size()-1)){
                                    polyline=mMap.addPolyline(lineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .color(color)
                                            .width(15));
                                    polyline.setClickable(true);
                                    polyline.setTag(route.getKey().toString()+" Distance: "+df.format(distance/1000)+" km");
//                                    polylines.add(polyline);

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
                                    polyline.setTag(route.getKey());
//                                   polylines.add(polyline);


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
                                    polyline.setTag(route.getKey().toString()+" Distance: "+df.format(distance/1000)+" km");
//                                    polylines.add(polyline);

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

                        mMap.addMarker(markerOptions).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        possibleRoutes=new ArrayList<>();
        root= FirebaseDatabase.getInstance().getReference().child("root");
        startingStop = new StopNew();
        endingStop = new StopNew();
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        currentLocalTime = cal.getTime();
        date =  new SimpleDateFormat("HH:mm:ss");

// you can get seconds by adding  "...:ss" to it
//        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        String localTime = date.format(currentLocalTime);

        possibleRoutes.add(3);
        possibleRoutes.add(6);
        possibleRoutes.add(9);
        Log.d("Route", "Current Time In Oncreate: "+ localTime);


        return inflater.inflate(R.layout.fragment_test_maps, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}