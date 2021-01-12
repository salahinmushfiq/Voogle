package com.example.voogle.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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

import com.example.voogle.PojoClasses.StopsNew;
import com.example.voogle.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

public class TestMapsFragment extends Fragment {

    private GoogleMap mMap;
    private DatabaseReference stopRef,root,routeRef;
    private Double lat,lng;
    ArrayList <Integer>possibleRoutes;
    LocationManager locationManager;
    Location userLocation,busLocation;
    Calendar cal;
    Date currentLocalTime;
    SimpleDateFormat date ;
    StopsNew startingStop,endingStop;
    Location startingStopLocalLocation,currentStopLocalLocation,endingStopLocalLocation;

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
//            startingStop.setName("Shishu Mela");
//            startingStop.setLat(23.773449569552913);
//            startingStop.setLng(90.36714567869578);

            //shyamoli
//            startingStop.setName("Me");
//            startingStop.setLat(23.774804);
//            startingStop.setLng(90.365533);


//            enable for up
            startingStop.setName("Shishu Mela");
            startingStop.setLat(23.773018887636074);
            startingStop.setLng(90.36722380809236);



            endingStop.setName("Kolabagan");
            endingStop.setLat(23.747854936993697);
            endingStop.setLng(90.38027281299742);


            userLocation=new Location("dummyprovider");
            userLocation.setLatitude(startingStop.getLat());
            userLocation.setLongitude (startingStop.getLng());
            mMap.addMarker(new MarkerOptions().position( new LatLng(startingStop.getLat(), startingStop.getLng())).title("Me")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.man));


//            getRouteDataFromDB(possibleRoutes);
            getRouteDataFromDBNew(startingStop,endingStop);


           getBusLocationFromDB();


        }
    };


    private void getRouteDataFromDB(ArrayList<Integer> possibleRoutes) {


        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("routeNew");
        stopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot route : dataSnapshot.getChildren()) {


                        Log.d ("Route: ",route.getKey());
                        for (int routeNo:possibleRoutes) {
                            if(Integer.valueOf(route.getKey())==routeNo){
                                for (DataSnapshot stops:route.getChildren())
                                {

                                    Log.d ("Route: ","stop: "+stops.getValue().toString());
                                    Log.d ("Route: ","name: "+stops.child("name").getValue().toString());
                                    Log.d ("Route: ","lat: "+stops.child("lat").getValue().toString());
                                    Log.d ("Route: ","lng: "+stops.child("lng").getValue().toString());


                                    LatLng position = new LatLng(Double.valueOf(stops.child("lat").getValue().toString()), Double.valueOf(stops.child("lng").getValue().toString()));
                                    mMap.addMarker(new MarkerOptions().position(position).title("name: "+stops.child("name").getValue().toString()+"      Route No.:"+stops.child("route").getValue().toString())).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pointer));

                                }
                                Log.d("Route",route.getValue().toString());
                            }
                        }

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getRouteDataFromDBNew(StopsNew startingStop,StopsNew endingStop) {
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


//                        returnedSourceExistingRoutes=detectingDownOrUp(route,sourceExistingRoutes,startingStopLocalLocation);
//                        returnedSourceExistingRoutes.size();



                        checkedRoute= (detectingAvailableRoutes(route,sourceExistingRoutes,startingStop,endingStop));

                        //find common route for source and destination
                        if(checkedRoute){
                            Log.d("getRouteDataFromDBNew","Route found: true for: "+route.getKey());
                            int iteratorForStart=-1;
                            int iteratorForEnd=-1;
                            String direction=traverseRoute(route,iteratorForStart,iteratorForEnd,startingStop,endingStop);
                            Log.d("getRouteDataFromDBNew","Route Direction : "+direction);

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

    private String traverseRoute(DataSnapshot route, int iteratorForStart, int iteratorForEnd, StopsNew startingStop, StopsNew endingStop) {

        Location startingStopLocation = new Location(startingStop.getName());
        startingStopLocation.setLatitude(startingStop.getLat());
        startingStopLocation.setLongitude(startingStop.getLng());

        ArrayList<Double> sourceDownUpDistance=new ArrayList();
        for (DataSnapshot stops : route.getChildren()) {


            Log.d("traverseRoute","start "+iteratorForStart);
            Log.d("traverseRoute","end "+iteratorForEnd);


                if((this.startingStop.getName().equals(stops.child("name").getValue().toString())))  {

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
                    iteratorForEnd=Integer.valueOf(stops.getKey().toString());
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

    private boolean detectingAvailableRoutes(DataSnapshot route, Map<Integer, String> sourceExistingRoutes, StopsNew startingStop, StopsNew endingStop) {

        startingStopLocalLocation=new Location(startingStop.getName());
        startingStopLocalLocation.setLatitude(startingStop.getLat());
        startingStopLocalLocation.setLongitude(startingStop.getLng());

        endingStopLocalLocation=new Location(endingStop.getName());
        endingStopLocalLocation.setLatitude(endingStop.getLat());
        endingStopLocalLocation.setLongitude(endingStop.getLng());
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





    private void detectSourceAndDestinationCoexistingRoutes(Map<Integer, String> returnedSourceExistingRoutes) {
        routeRef = FirebaseDatabase.getInstance().getReference().child("root").child("routeNew");
        routeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot route : dataSnapshot.getChildren()) {
                        if(returnedSourceExistingRoutes.containsKey(Integer.valueOf(route.getKey())))
                        {

                            Log.d("Routes","detectedSource In Function(detectSourceAndDestinationCoexistingRoutes ): "+route.getKey());
                            Log.d("Routes","detectedSource In Function(detecting Up Down:  "+returnedSourceExistingRoutes.get(Integer.valueOf(route.getKey())));
                        }else{
                            Log.d("Routes","Not detectedSource In get key: "+route.getKey());

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Map<Integer, String> detectingDownOrUp(DataSnapshot route, Map<Integer, String> sourceExistingRoutes, Location startingStopLocalLocation) {
        double distanceInMetersDown=0;
        double distanceInMetersUp=0;
        int upCurrentRouteNo=-1;
        int downCurrentRouteNo=-1;
        for (DataSnapshot stops:route.getChildren())
        {

            Log.d("detectingDownOrUp","Stop: "+ stops.child("name").getValue().toString());
            if( ((Double.valueOf(stops.child("lat").getValue().toString()))==startingStop.getLat().doubleValue())  && (Double.valueOf(stops.child("lng").getValue().toString())==startingStop.getLng()) ){
                distanceInMetersUp=0;
                distanceInMetersDown=0;
                Log.d("detectingDownOrUp","Stop if everything matches: "+ stops.child("name").getValue().toString());
                if(Integer.valueOf(stops.child("up").getValue().toString())==1)
                {

                    currentStopLocalLocation=new Location(startingStop.getName());
                    currentStopLocalLocation.setLatitude(Double.valueOf(stops.child("lat").getValue().toString()));
                    currentStopLocalLocation.setLongitude(Double.valueOf(stops.child("lat").getValue().toString()));
                    distanceInMetersUp= (startingStopLocalLocation.distanceTo(currentStopLocalLocation))/10000;
                    Log.d("detectingDownOrUp", "stop: " + stops.getValue().toString());
//                    Log.d("detectingDownOrUp", "name: " + stops.child("name").getValue().toString());
                    Log.d("detectingDownOrUp", "Source Name: " + startingStopLocalLocation.toString());
                    Log.d("detectingDownOrUp", "Source Lat: " + startingStopLocalLocation.getLatitude());
                    Log.d("detectingDownOrUp", "Source Lng: " + startingStopLocalLocation.getLongitude());
                    Log.d("detectingDownOrUp", "route_no.: " + stops.child("route").getValue().toString());
                    Log.d("detectingDownOrUp", "lat: " + stops.child("lat").getValue().toString());
                    Log.d("detectingDownOrUp", "lng: " + stops.child("lng").getValue().toString());
                    Log.d("detectingDownOrUp", "distance from source up: " + distanceInMetersUp);
                    upCurrentRouteNo=Integer.valueOf(stops.child("route").getValue().toString());

                }
                if(Integer.valueOf(stops.child("down").getValue().toString())==1)
                {

                    currentStopLocalLocation=new Location(startingStop.getName());
                    currentStopLocalLocation.setLatitude(Double.valueOf(stops.child("lat").getValue().toString()));
                    currentStopLocalLocation.setLongitude(Double.valueOf(stops.child("lat").getValue().toString()));
                    distanceInMetersDown= this.startingStopLocalLocation.distanceTo(currentStopLocalLocation)/10000;
                    Log.d("detectingDownOrUp", "stop: " + stops.getValue().toString());
                    Log.d("detectingDownOrUp", "name: " + stops.child("name").getValue().toString());
                    Log.d("detectingDownOrUp", "Source Lat: " + startingStopLocalLocation.getLatitude());
                    Log.d("detectingDownOrUp", "Source Lat: " + startingStopLocalLocation.getLongitude());
                    Log.d("detectingDownOrUp", "route_no.: " + stops.child("route").getValue().toString());
                    Log.d("detectingDownOrUp", "lat: " + stops.child("lat").getValue().toString());
                    Log.d("detectingDownOrUp", "lng: " + stops.child("lng").getValue().toString());
                    Log.d("detectingDownOrUp", "distance from source down: " + distanceInMetersDown);
                    downCurrentRouteNo=Integer.valueOf(stops.child("route").getValue().toString());
                }


            }else{
                Log.d("detectingDownOrUp","Not Detected");
            }
//
            LatLng position = new LatLng(Double.valueOf(stops.child("lat").getValue().toString()), Double.valueOf(stops.child("lng").getValue().toString()));

            mMap.addMarker(new MarkerOptions().position(position).title("name: "+stops.child("name").getValue().toString()+"      Route No.:"+stops.child("route").getValue().toString()+" Up: "+stops.child("up").getValue()+" Down: "+stops.child("down").getValue())).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pointer));


        }
        Log.d("Route",route.getValue().toString());
        if((upCurrentRouteNo==downCurrentRouteNo)&&(upCurrentRouteNo!=-1))
        {
            Log.d("Routes: ", "Routes matched : " + String.valueOf(upCurrentRouteNo));
            if(distanceInMetersDown<distanceInMetersUp)
            {
                Log.d("Routes: ", "Route should be : Down" );
                sourceExistingRoutes.put(upCurrentRouteNo,"down");
            }
            if(distanceInMetersDown>distanceInMetersUp)
            {
                Log.d("Routes: ", "Route should be : Up" );
                sourceExistingRoutes.put(upCurrentRouteNo,"up");
            }
        }
        return sourceExistingRoutes;
    }

    private void getBusLocationFromDB() {

        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("locations");
        stopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot bus : dataSnapshot.getChildren()) {
//                        if (data.child("name").getValue().toString().trim().equals(source)) {
                        String licensePlate = bus.child("licensePlate").getValue().toString();
                        //     Toast.makeText(MainActivity.this,"Stop: "+stop, Toast.LENGTH_SHORT).show();
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
        startingStop = new StopsNew();
        endingStop = new StopsNew();
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


//        Toast.makeText(getContext(), String.valueOf(date), Toast.LENGTH_SHORT).show();
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