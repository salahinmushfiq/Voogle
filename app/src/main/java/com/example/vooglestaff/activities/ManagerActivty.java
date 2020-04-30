package com.example.vooglestaff.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.vooglestaff.R;
import com.example.vooglestaff.adapters.LicensePlateAdapter;
import com.example.vooglestaff.adapters.PhoneNoAdapter;
import com.example.vooglestaff.databinding.ActivityManagerBinding;
import com.example.vooglestaff.pojoClass.Manager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManagerActivty extends AppCompatActivity {

    ActivityManagerBinding activityManagerBinding;
    RecyclerView licensePlateRV, phoneNoRV;
    DatabaseReference databaseReference,managerListRef;
    String licensePlate,phoneNo;
    private FirebaseAuth mAuth;
    Manager manager ;
    int busIdCount,phoneNoCount;
    LicensePlateAdapter licensePlateAdapter;
    PhoneNoAdapter phoneNoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityManagerBinding= DataBindingUtil.setContentView(this,R.layout.activity_manager);
        databaseReference= FirebaseDatabase.getInstance().getReference("");
        managerListRef = databaseReference.child("root").child("ManagerList");
        mAuth=FirebaseAuth.getInstance();
        manager =new Manager();


    }

    public void licensePlateBtnOnClick(View view) {
        activityManagerBinding.phoneNoRV.setVisibility(View.GONE);
        activityManagerBinding.addPhoneNoACTV.setVisibility(View.GONE);
        activityManagerBinding.addPhoneNoBtn.setVisibility(View.GONE);
        activityManagerBinding.licensePlateRV.setVisibility(View.VISIBLE);
        activityManagerBinding.addLicensePlateBtn.setVisibility(View.VISIBLE);
        activityManagerBinding.addLicensePlateACTV.setVisibility(View.VISIBLE);
        managerListRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                                 @Override
                                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                     if (dataSnapshot.exists()) {
//
                                                             manager=dataSnapshot.getValue(Manager.class);
                                                             GenericTypeIndicator<ArrayList<String>>t=new GenericTypeIndicator<ArrayList<String>>() {
                                                             };
                                                             Toast.makeText(ManagerActivty.this, "Bus Id's : " + manager.getLicensePlate(), Toast.LENGTH_SHORT).show();

                                                             licensePlateAdapter=new LicensePlateAdapter(ManagerActivty.this, manager.getLicensePlate());
                                                             activityManagerBinding.licensePlateRV.setAdapter(licensePlateAdapter);
                                                             activityManagerBinding.licensePlateRV.setVisibility(View.VISIBLE);

                                                             try {
                                                                 for (String busId : manager.getLicensePlate()) {
                                                                     Toast.makeText(ManagerActivty.this, "Bus Id: " + busId, Toast.LENGTH_SHORT).show();
                                                                 }
                                                                 } catch (Exception e) {
                                                                     Toast.makeText(ManagerActivty.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                 }

                                                             activityManagerBinding.licensePlateRV.setLayoutManager(new LinearLayoutManager(ManagerActivty.this));

                                                     }
                                                 }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


       // licensePlateRV = activityManagerBinding.licensePlateRV;



}
//        licensePlateAdapter =(new ArrayAdapter(ManagerActivty.this,android.R.layout.simple_list_item_1, stopNames);   // it sends entire hotel list to the adapter
     //   licensePlateRV.setAdapter(licensePlateAdapter);


    public void phoneNumberBtnOnClick(View view) {
        activityManagerBinding.licensePlateRV.setVisibility(View.GONE);
        activityManagerBinding.addLicensePlateACTV.setVisibility(View.GONE);
        activityManagerBinding.addLicensePlateBtn.setVisibility(View.GONE);
        activityManagerBinding.phoneNoRV.setVisibility(View.VISIBLE);
        activityManagerBinding.addPhoneNoACTV.setVisibility(View.VISIBLE);
        activityManagerBinding.addPhoneNoBtn.setVisibility(View.VISIBLE);

        managerListRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
//
                    manager=dataSnapshot.getValue(Manager.class);

                    Toast.makeText(ManagerActivty.this, "Phone Numbers : " + manager.getPhoneNumbers(), Toast.LENGTH_SHORT).show();

                    if(!manager.getPhoneNumbers().isEmpty()) {
                        phoneNoAdapter = new PhoneNoAdapter(manager.getPhoneNumbers(), ManagerActivty.this);
                        activityManagerBinding.phoneNoRV.setAdapter(phoneNoAdapter);
                        activityManagerBinding.phoneNoRV.setVisibility(View.VISIBLE);

                        try {
                            for (String phoneNumbers : manager.getPhoneNumbers()) {
                                Toast.makeText(ManagerActivty.this, "Phone Numbers: " + phoneNumbers, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ManagerActivty.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        activityManagerBinding.phoneNoRV.setLayoutManager(new LinearLayoutManager(ManagerActivty.this));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    public void addLicensePlateBtnOnClick(View view) {
        licensePlate=activityManagerBinding.addLicensePlateACTV.getText().toString();
        manager.setManagerId(mAuth.getCurrentUser().getUid());
        managerListRef.child(manager.getManagerId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                        manager=dataSnapshot.getValue(Manager.class);
                        busIdCount=manager.getBusIdCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });



        databaseReference.child("root").child("ManagerList").child(manager.getManagerId()).child("licensePlate").child(String.valueOf(busIdCount)).setValue(licensePlate).addOnCompleteListener(new OnCompleteListener<Void>() {


            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ManagerActivty.this, "Bus Id Written", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ManagerActivty.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        busIdCount=busIdCount+1;
        databaseReference.child("root").child("ManagerList").child(manager.getManagerId()).child("busIdCount").setValue(busIdCount);
    }

    public void addPhoneNoBtnOnClick(View view) {
        phoneNo=activityManagerBinding.addPhoneNoACTV.getText().toString();

        manager.setManagerId(mAuth.getCurrentUser().getUid());


        managerListRef.child(manager.getManagerId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                        manager=dataSnapshot.getValue(Manager.class);
                        phoneNoCount=manager.getPhoneNoCount();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });



        databaseReference.child("root").child("ManagerList").child(manager.getManagerId()).child("phoneNumbers").child(String.valueOf(phoneNoCount)).setValue(phoneNo).addOnCompleteListener(new OnCompleteListener<Void>() {


            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ManagerActivty.this, "Phone No. Written", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ManagerActivty.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        phoneNoCount=phoneNoCount+1;
        databaseReference.child("root").child("ManagerList").child(manager.getManagerId()).child("phoneNoCount").setValue(phoneNoCount);
    }
}
