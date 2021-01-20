package com.example.vooglestaff.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.vooglestaff.R;
import com.example.vooglestaff.constants.GlobalVariables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import android.Manifest;
public class DriverActivity extends AppCompatActivity {
    LocationComponent locationComponent;
    LocationComponentActivationOptions locationComponentActivationOptions;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentLocation;
    private double lat, lng;
    DatabaseReference databaseReference;
    Context mContext;
    FirebaseAuth firebaseAuth;
    String driverLicenseNo,licensePlateNo,groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("");

        driverLicenseNo =null;
        licensePlateNo=null;
        groupId=null;

        driverLicenseNo=getIntent().getStringExtra("driverLicenseNo");
        licensePlateNo=getIntent().getStringExtra("licensePlateNo");
        groupId=getIntent().getStringExtra("groupId");





        if (ContextCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
            //Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            com.example.vooglestaff.pojoClass.Location locationPojoClass = new com.example.vooglestaff.pojoClass.Location();


            locationPojoClass.setLat(latitude);
            locationPojoClass.setLng(longitude);
            locationPojoClass.setLicensePlate(licensePlateNo);
            locationPojoClass.setLicenseNo(driverLicenseNo);
            locationPojoClass.setGroupId(Integer.parseInt(groupId));


            Toast.makeText(mContext, "License Plate: " + locationPojoClass.getLicensePlate(), Toast.LENGTH_SHORT).show();
            databaseReference.child("root").child("locations").child(licensePlateNo).setValue(locationPojoClass).addOnCompleteListener(task -> {
                if (task.isComplete()) {
                    Toast.makeText(mContext, "Location written", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void isLocationEnabled() {

        mContext = DriverActivity.this;



        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            AlertDialog alert = alertDialog.create();
            alert.show();
        } else {
            Toast.makeText(mContext, "Location Enabled!", Toast.LENGTH_SHORT).show();
//        }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(DriverActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    public void locationBtnOnClick(View view) {







        mContext = DriverActivity.this;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000,
                10, locationListenerGPS);
        isLocationEnabled();

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                com.example.vooglestaff.pojoClass.Location locationPojoClass=new com.example.vooglestaff.pojoClass.Location();

                lat=location.getLatitude();
                lng=location.getLongitude();
                locationPojoClass.setLat(lat);
                locationPojoClass.setLng(lng);
                locationPojoClass.setLicensePlate(licensePlateNo);
                locationPojoClass.setLicenseNo(driverLicenseNo);
                locationPojoClass.setGroupId(Integer.parseInt(groupId));
                Toast.makeText(DriverActivity.this, "Lat "+String.valueOf(lat), Toast.LENGTH_SHORT).show();
                Toast.makeText(DriverActivity.this, "", Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "License Plate: "+GlobalVariables.licensePlate, Toast.LENGTH_SHORT).show();
                Log.d("MAP","Lat "+String.valueOf(lat));
                Log.d("MAP","Lng "+String.valueOf(lng));

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
    }

    public void signOutBtnOnClick(View view) {
        firebaseAuth.signOut();

        Intent goToMainActivity=new Intent(DriverActivity.this,MainActivity.class);
        startActivity(goToMainActivity);

    }
}
