package com.example.voogle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.voogle.Fragments.MapFragment;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseReference stopRef,root;
    ActivityMainBinding activityMainBinding;
    ArrayList<String>stops;
    String stop;
    private ArrayAdapter stopsAdapter;
    private Double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        root= FirebaseDatabase.getInstance().getReference().child("root");
        stops=new ArrayList<>();


        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("stops");
        stopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        stop= data.child("name").getValue().toString();
                        stops.add(stop);
                        ArrayAdapter placesAdapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1, stops);
                        placesAdapter.notifyDataSetChanged();
                        activityMainBinding.sourceACTV.setAdapter(placesAdapter);
                        activityMainBinding.sourceACTV.setThreshold(2);
                        activityMainBinding.destinationACTV.setAdapter(placesAdapter);
                        activityMainBinding.destinationACTV.setThreshold(2);


                    }
                } else {
                    //   Toast.makeText(ListActivity.this, "Empty Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        super.onCreate(savedInstanceState);
        activityMainBinding=DataBindingUtil.setContentView(this, R.layout.activity_main);




        /**
         * TUI EKHONO EITA COMMENT OUT KOROS NAI *FACEPALM*
         * AMI KAM SHESHE EIDA ABAR ON RAIKHA JAMU
         */
        Intent goToHome=new Intent(MainActivity.this,HomeActivity.class);
        startActivity(goToHome);

    }
    public void onClickOnGo(View view) {
        Intent goToHome=new Intent(MainActivity.this,HomeActivity.class);
        //ArrayList<String>stopNames=new ArrayList<>();
        ArrayList<String>stops=new ArrayList<>();
        stops.add(stops.getClass().getName());

        String source=activityMainBinding.sourceACTV.getText().toString();
        String destination=activityMainBinding.destinationACTV.getText().toString();
       // Toast.makeText(this, destination, Toast.LENGTH_SHORT).show();
        getLatLngFromDB(source,destination,goToHome);


    }

    public void getLatLngFromDB(String source, String destination, Intent goToHome) {
        //Toast.makeText(this, source, Toast.LENGTH_SHORT).show();
        stopRef = FirebaseDatabase.getInstance().getReference().child("root").child("stops");
        stopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if(data.child("name").getValue().toString().equals(source)) {
                            stop = data.child("name").getValue().toString();
                           Toast.makeText(MainActivity.this,"Stop: "+stop, Toast.LENGTH_SHORT).show();
                            lat = Double.valueOf(data.child("lat").getValue().toString());
                            lng = Double.valueOf(data.child("lng").getValue().toString());
                            Toast.makeText(MainActivity.this, String.valueOf(lat), Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, String.valueOf(lng), Toast.LENGTH_SHORT).show();
                            goToHome.putExtra("sourceLat", data.child("lat").getValue().toString());
                            goToHome.putExtra("sourceLng", data.child("lng").getValue().toString());
                        }
                        if(data.child("name").getValue().toString().equals(destination)) {
                            stop = data.child("name").getValue().toString();
                            Toast.makeText(MainActivity.this,"Stop: "+stop, Toast.LENGTH_SHORT).show();
                            lat = Double.valueOf(data.child("lat").getValue().toString());
                            lng = Double.valueOf(data.child("lng").getValue().toString());
                            Toast.makeText(MainActivity.this, String.valueOf(lat), Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, String.valueOf(lng), Toast.LENGTH_SHORT).show();
                            goToHome.putExtra("destinationLat", String.valueOf(lat));
                            goToHome.putExtra("destinationLng", String.valueOf(lng));
                        }
                        stops.add(stop);
                        ArrayAdapter placesAdapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1, stops);
                        placesAdapter.notifyDataSetChanged();
                        activityMainBinding.sourceACTV.setAdapter(placesAdapter);
                        activityMainBinding.sourceACTV.setThreshold(2);
                        activityMainBinding.destinationACTV.setAdapter(placesAdapter);
                        activityMainBinding.destinationACTV.setThreshold(2);


                    }
                    goToHome.putExtra("source", source);
                    goToHome.putExtra("destination", destination);
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
