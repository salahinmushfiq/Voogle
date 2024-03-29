package com.example.voogle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.voogle.Fragments.MapFragment;
import com.example.voogle.GlobalVariables;
import com.example.voogle.PojoClasses.Stops;
import com.example.voogle.R;
import com.example.voogle.databinding.ActivityHomeBinding;
import com.example.voogle.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.style.expressions.Expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    DatabaseReference stopRef,root,stopNewRef;
    ActivityMainBinding activityMainBinding;
    ArrayList<String>stopNames;
    String stopName;
    String source,destination;
    boolean sourceNull,destinationNull;
    private ArrayAdapter stopsAdapter;
    private Double lat,lng;
    int s_no;

    ArrayList<Integer>sourceRoute;
    ArrayList<Integer>destinationRoute;
    GlobalVariables gv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        root= FirebaseDatabase.getInstance().getReference().child("root");
        stopNames=new ArrayList<>();
        sourceRoute=new ArrayList<>();
        destinationRoute=new ArrayList<>();
//        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("stops");
//        initACTV(stopRef);
        stopNewRef = FirebaseDatabase.getInstance().getReference().child("root").child("stopsNew");
        initACTVNew(stopNewRef);


        super.onCreate(savedInstanceState);
        activityMainBinding=DataBindingUtil.setContentView(this, R.layout.activity_main);


    }

//    private void initACTV(DatabaseReference stopRef) {
//        stopRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                        stopName= data.child("name").getValue().toString();
//                        stopNames.add(stopName);
//
//
//
//
//                    }
//                    ArrayAdapter placesAdapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1, stopNames);
//                    placesAdapter.notifyDataSetChanged();
//                    activityMainBinding.sourceACTV.setAdapter(placesAdapter);
//                    activityMainBinding.sourceACTV.setThreshold(2);
//                    activityMainBinding.destinationACTV.setAdapter(placesAdapter);
//                    activityMainBinding.destinationACTV.setThreshold(2);
//                } else {
//                    //   Toast.makeText(ListActivity.this, "Empty Database", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    private void initACTVNew(DatabaseReference stopRef) {
        stopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot stop : dataSnapshot.getChildren()) {
                        stopName= stop.child("name").getValue().toString();
                        stopNames.add(stopName);
                    }
                    ArrayAdapter placesAdapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1, stopNames);
                    placesAdapter.notifyDataSetChanged();
                    activityMainBinding.sourceACTV.setAdapter(placesAdapter);
                    activityMainBinding.sourceACTV.setThreshold(2);
                    activityMainBinding.destinationACTV.setAdapter(placesAdapter);
                    activityMainBinding.destinationACTV.setThreshold(2);
                } else {
                    //   Toast.makeText(ListActivity.this, "Empty Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onClickOnGo(View view) {
        Intent goToHome=new Intent(MainActivity.this,HomeActivity.class);
        //ArrayList<String>stopNames=new ArrayList<>();
        ArrayList<String>stops=new ArrayList<>();
        stops.add(stops.getClass().getName());

        source=activityMainBinding.sourceACTV.getText().toString();
        destination=activityMainBinding.destinationACTV.getText().toString();
        nullCheck();
        if(sourceNull==true && destinationNull==true)
        {
            Toast.makeText(this, "Please Insert Source and destination", Toast.LENGTH_SHORT).show();
        }
        else{
            getLatLngFromDB(source,destination,goToHome);
            getLatLngFromDBNew(source,destination,goToHome);
            Log.d("check","Source: "+source+" Destination: "+destination);
        }
       // Toast.makeText(this, destination, Toast.LENGTH_SHORT).show();



    }

    private void nullCheck() {
        if(source.isEmpty())
        {
            sourceNull=true;
        }
        if(source.isEmpty())
        {
            destinationNull=true;
        }

    }

    public void getLatLngFromDB(String source, String destination, Intent goToHome) {
        //Toast.makeText(this, source, Toast.LENGTH_SHORT).show();

        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("stops");
        stopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if(data.child("name").getValue().toString().trim().equals(source)) {
                            stopName = data.child("name").getValue().toString();
                      //     Toast.makeText(MainActivity.this,"Stop: "+stop, Toast.LENGTH_SHORT).show();
                            lat = Double.valueOf(data.child("lat").getValue().toString());
                            lng = Double.valueOf(data.child("lng").getValue().toString());
//                            s_no = Integer.parseInt(data.child("s_no").getValue().toString());
                            GlobalVariables.sourceLat=lat;
                            GlobalVariables.sourceLng=lng;
//                            GlobalVariables.sourceS_no=s_no;
//                            GlobalVariables.sourceS_no=s_no;

                            for (DataSnapshot routes : data.child("route").getChildren()) {

                                //Toast.makeText(MainActivity.this, "Source Route: "+routes.getValue().toString(), Toast.LENGTH_SHORT).show();
                                sourceRoute.add(Integer.parseInt(routes.getValue().toString().trim()));

                            }
//                            goToHome.putExtra("sourceLat", lat);
//                            goToHome.putExtra("sourceLng", lng);
//                            goToHome.putExtra("sourceS_no", String.valueOf(s_no));

                        }
                        if(data.child("name").getValue().toString().trim().equals(destination)) {
                            stopName = data.child("name").getValue().toString();
                            lat = Double.valueOf(data.child("lat").getValue().toString());
                            lng = Double.valueOf(data.child("lng").getValue().toString());
//                            s_no = Integer.parseInt(data.child("s_no").getValue().toString());
                            GlobalVariables.destinationLat=lat;
                            GlobalVariables.destinationLng=lng;
//                            GlobalVariables.destinationS_no=s_no;
//                            GlobalVariables.destinationS_no=s_no;
                            for (DataSnapshot routes : data.child("route").getChildren()) {
                               destinationRoute.add(Integer.valueOf(routes.getValue().toString()));
                            }


//                            goToHome.putExtra("destinationLat", lat);
//                            goToHome.putExtra("destinationLng", lng);
//                            goToHome.putExtra("destinationS_no", String.valueOf(s_no));

                        }
//                        stopNames.add(stopName);
//                        ArrayAdapter placesAdapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1, stopNames);
//                        placesAdapter.notifyDataSetChanged();
//                        activityMainBinding.sourceACTV.setAdapter(placesAdapter);
//                        activityMainBinding.sourceACTV.setThreshold(2);
//                        activityMainBinding.destinationACTV.setAdapter(placesAdapter);
//                        activityMainBinding.destinationACTV.setThreshold(2);


                    }

//                    goToHome.putExtra("source", source);
//                    goToHome.putExtra("destination", destination);

//                    Bundle source = new Bundle();
//                    source.putSerializable("ARRAYLISTSOURCE",(Serializable)sourceRoute);
//                    goToHome.putExtra("sourceRoute",source);
//                    Bundle destination = new Bundle();
//                    destination.putSerializable("ARRAYLISTDESTINATION",(Serializable)destinationRoute);
//                    goToHome.putExtra("destinationRoute",destination);
                    GlobalVariables.sourceRoutes=sourceRoute;
                    GlobalVariables.destinationRoutes=destinationRoute;
                    Log.d("check","Source"+source);
                    Log.d("check","Destination"+destination);
                    Log.d("check","Source Route: "+GlobalVariables.sourceRoutes.toString());
                    Log.d("check","Destination Route: "+GlobalVariables.sourceRoutes.toString());
             //       Toast.makeText(MainActivity.this, "Global Variable"+GlobalVariables.sourceRoutes.toString(), Toast.LENGTH_SHORT).show();

//                    startActivity(goToHome);
                } else {
                      Toast.makeText(MainActivity.this, "Empty Database", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    public void getLatLngFromDBNew(String source, String destination, Intent goToHome) {

        stopNewRef = FirebaseDatabase.getInstance().getReference().child("root").child("stopsNew");
        stopNewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if(data.child("name").getValue().toString().trim().equals(source)) {
                            stopName = data.child("name").getValue().toString();
                            lat = Double.valueOf(data.child("lat").getValue().toString());
                            lng = Double.valueOf(data.child("lng").getValue().toString());
                            GlobalVariables.sourceNewLat=lat;
                            GlobalVariables.sourceNewLng=lng;
                            GlobalVariables.sourceNewName=stopName;

                            for (DataSnapshot routes : data.child("route").getChildren()) {

                                sourceRoute.add(Integer.parseInt(routes.getValue().toString().trim()));

                            }


                        }
                        if(data.child("name").getValue().toString().trim().equals(destination)) {
                            stopName = data.child("name").getValue().toString();
                            lat = Double.valueOf(data.child("lat").getValue().toString());
                            lng = Double.valueOf(data.child("lng").getValue().toString());
//                            s_no = Integer.valueOf(data.child("s_no").getValue().toString());
                            GlobalVariables.destinationNewLat=lat;
                            GlobalVariables.destinationNewLng=lng;
                            GlobalVariables.destinationNewName=stopName;
                            for (DataSnapshot routes : data.child("route").getChildren()) {

                                destinationRoute.add(Integer.valueOf(routes.getValue().toString()));

                            }


                        }

                    }

                    startActivity(goToHome);
                } else {
                    Toast.makeText(MainActivity.this, "Empty Database", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }


}
