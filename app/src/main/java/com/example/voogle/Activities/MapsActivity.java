package com.example.voogle.Activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.voogle.GlobalVariables;
import com.example.voogle.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference stopRef,root;
    private Double lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        root= FirebaseDatabase.getInstance().getReference().child("root");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng Shyamoli = new LatLng(23.774804, 90.365533);
        mMap.addMarker(new MarkerOptions().position(Shyamoli).title("Marker in Shyamoli"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(Shyamoli));


        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("locations");
        stopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                        if (data.child("name").getValue().toString().trim().equals(source)) {
                            String licensePlate = data.child("licensePlate").getValue().toString();
                            //     Toast.makeText(MainActivity.this,"Stop: "+stop, Toast.LENGTH_SHORT).show();
                            lat = Double.valueOf(data.child("lat").getValue().toString());
                            lng = Double.valueOf(data.child("lng").getValue().toString());
                            LatLng currentLocation = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title(licensePlate));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}