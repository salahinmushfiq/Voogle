package com.example.voogle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.voogle.Fragments.MapFragment;
import com.example.voogle.R;
import com.example.voogle.databinding.ActivityHomeBinding;
import com.example.voogle.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    ArrayList<String>stops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseReference root= FirebaseDatabase.getInstance().getReference().child("root");
        stops=new ArrayList<>();
        readPlacesFromDB(root);



        super.onCreate(savedInstanceState);
        activityMainBinding=DataBindingUtil.setContentView(this, R.layout.activity_main);


        ArrayAdapter placesAdapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1, stops);
        activityMainBinding.sourceACTV.setAdapter(placesAdapter);
        activityMainBinding.sourceACTV.setThreshold(2);
        activityMainBinding.destinationACTV.setAdapter(placesAdapter);
        activityMainBinding.destinationACTV.setThreshold(2);

        /**
         * TUI EKHONO EITA COMMENT OUT KOROS NAI *FACEPALM*
         * AMI KAM SHESHE EIDA ABAR ON RAIKHA JAMU
         */
        Intent goToHome=new Intent(MainActivity.this,HomeActivity.class);
        startActivity(goToHome);

    }

    private void readPlacesFromDB(DatabaseReference root) {

        root.child("places").child("stops").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String a = data.getValue(String.class);
                        stops.add(a);

                    }

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

    public void onClickOnGo(View view) {
        Intent goToHome=new Intent(MainActivity.this,HomeActivity.class);


        String source=activityMainBinding.sourceACTV.getText().toString();
        String destination=activityMainBinding.destinationACTV.getText().toString();
        goToHome.putExtra("source", source);
        goToHome.putExtra("destination", destination);
        startActivity(goToHome);
    }
}
