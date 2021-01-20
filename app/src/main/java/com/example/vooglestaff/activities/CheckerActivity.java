package com.example.vooglestaff.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.vooglestaff.R;
import com.example.vooglestaff.databinding.ActivityCheckerBinding;
import com.example.vooglestaff.pojoClass.Location;
import com.example.vooglestaff.pojoClass.Manager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckerActivity extends AppCompatActivity {


    ActivityCheckerBinding activityCheckerBinding;
    DatabaseReference rootRef;
    int busSeatCount;
    String groupId;
    ArrayAdapter<String>licensePlateAdapter;
    ArrayList<String>licensePlates;
    private Intent goToHomeActivity;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCheckerBinding=DataBindingUtil.setContentView(this,R.layout.activity_checker);
        rootRef= FirebaseDatabase.getInstance().getReference("root");
        activityCheckerBinding.busSeatET.getText().toString();
        groupId=getIntent().getStringExtra("groupId");
        goToHomeActivity=new Intent(this,MainActivity.class);
        licensePlates=new ArrayList<>();

       loadLicensePlates();





    }

    private void loadLicensePlates() {
        rootRef.child("ManagerList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot managerList) {

                for(DataSnapshot manager:managerList.getChildren())
                {
                    if(manager.child("groupId").getValue().toString().equals(groupId))
                    {
                        Manager currentManager=manager.getValue(Manager.class);

                        for (String currentLicensePlate: currentManager.getLicensePlate()) {
                            licensePlates.add(currentLicensePlate);
                        }

                    }
                }
                licensePlateAdapter=new ArrayAdapter(CheckerActivity.this, android.R.layout.simple_spinner_dropdown_item,licensePlates);
                activityCheckerBinding.busLicensePlatesSpinner.setAdapter(licensePlateAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void seatUpdateBtnOnClick(View view) {

        busSeatCount=Integer.parseInt(activityCheckerBinding.busSeatET.getText().toString());
        String selectedLicensePlate=activityCheckerBinding.busLicensePlatesSpinner.getSelectedItem().toString();
        boolean gotLocation=false;
        getLocationFromBD(selectedLicensePlate,gotLocation);

//        rootRef.child("locations").child(selectedLicensePlate).setValue(currentLocation);
        rootRef.child("locations/"+selectedLicensePlate+"/"+"availableSeats").setValue(busSeatCount).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CheckerActivity.this, "Seat Added", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getLocationFromBD(String selectedLicensePlate, boolean gotLocation) {
        rootRef.child("locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot busLocation) {

                currentLocation=busLocation.getValue(Location.class);
                currentLocation.setAvailableSeats(busSeatCount);
                Toast.makeText(CheckerActivity.this, "Selected "+selectedLicensePlate, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void signOutBtnOnClick(View view) {
//        String currentUsermail=mAuth.getCurrentUser().getEmail().toString();
//        if(currentUsermail!=null){
//            mAuth.signOut();
//            goToHomeActivity = new Intent(ManagerActivty.this,MainActivity.class );
            startActivity(goToHomeActivity);
//        }
    }
}