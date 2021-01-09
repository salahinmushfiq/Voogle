package com.example.voogle.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Location;
import android.location.LocationManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TestMapsFragment extends Fragment {

    private GoogleMap mMap;
    private DatabaseReference stopRef,root;
    private Double lat,lng;
    ArrayList <Integer>possibleRoutes;
    LocationManager locationManager;
    Location userLocation,busLocation;

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
            mMap.addMarker(new MarkerOptions().position(Shyamoli).title("Me")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.man));
            userLocation=new Location("dummyprovider");
            userLocation.setLatitude(Shyamoli.latitude);
            userLocation.setLongitude (Shyamoli.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Shyamoli));
            mMap.setTrafficEnabled(true);
            possibleRoutes.add(3);
            possibleRoutes.add(6);
            possibleRoutes.add(9);
            getRouteDataFromDB(possibleRoutes);
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

                        mMap.addMarker(new MarkerOptions().position(currentBusLocation).title(licensePlate)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
                        busLocation=new Location(licensePlate);
                        busLocation.setLatitude(lat);
                        busLocation.setLongitude (lng);
                        String estimatedArrivalTime=String.valueOf(busLocation.distanceTo(userLocation));
                        Log.d("Route","Estimated Distance: "+estimatedArrivalTime);


                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        possibleRoutes=new ArrayList<>();
        root= FirebaseDatabase.getInstance().getReference().child("root");


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