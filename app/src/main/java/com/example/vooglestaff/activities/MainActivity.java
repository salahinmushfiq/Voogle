package com.example.vooglestaff.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.vooglestaff.R;
import com.example.vooglestaff.databinding.ActivityMainBinding;
import com.example.vooglestaff.databinding.LoginPopupCheckerBinding;
import com.example.vooglestaff.databinding.LoginPopupDriverBinding;
import com.example.vooglestaff.databinding.LoginPopupManagerBinding;
import com.example.vooglestaff.pojoClass.DriverPhoneMac;
import com.example.vooglestaff.pojoClass.Manager;
import com.example.vooglestaff.utils.DeviceInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;

@SuppressLint("LogNotTimber")
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    AlertDialog.Builder alert;
    AlertDialog alertDialog, ad;
    AlertDialog.Builder reportBuild;
    LoginPopupManagerBinding loginPopupManagerBinding;
    LoginPopupDriverBinding loginPopupDriverBinding;
    LoginPopupCheckerBinding loginPopupCheckerBinding;
    View alertView;
    String email, password;
    int nullCheckFlag;
    String phoneNumber, groupId, licensePlate;
    JSONObject signInJson;
    private FirebaseAuth mAuth;
    DatabaseReference rootRef,databaseReference;
    Manager currentManager;
    ArrayList<Manager> managerList = new ArrayList<>();
    private static final String TAG = "MainActivity";
    DriverPhoneMac currentDriver;
    String MAC_ID,currentUsermail,currentUserId;
    Intent goToManagerActivity,goToDriverActivity,goToCheckerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        MAC_ID = DeviceInformation.getMAC((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE));
        mAuth = FirebaseAuth.getInstance();
        // loginPopupBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.login_popup_manager, null, false);
        goToDriverActivity=new Intent(this,DriverActivity.class);
        goToCheckerActivity=new Intent(this,CheckerActivity.class);
        rootRef=FirebaseDatabase.getInstance().getReference("root");


//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            Toast.makeText(this, mAuth.getCurrentUser().getEmail().toString(), Toast.LENGTH_SHORT).show();
//            currentUsermail=mAuth.getCurrentUser().getEmail().toString();
//            currentUserId=mAuth.getCurrentUser().getUid();
//            if(currentUsermail!=null){
//                rootRef.child("ManagerList").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for(DataSnapshot manager:dataSnapshot.getChildren())
//                        {
//                            if(manager.child("email").getValue().toString().toLowerCase().equals(currentUsermail.toString().toLowerCase()))
//                            {
//                                goToManagerActivity = new Intent(MainActivity.this, ManagerActivty.class);
//                                startActivity(goToManagerActivity);
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//
//
//                });
////            Toast.makeText(this, String.valueOf(rootRef.child("ManagerList").get.contains(currentUserId)), Toast.LENGTH_SHORT).show();
//
//            }
//        }


    }

    private void alertDialogManager() {
        alert = new AlertDialog.Builder(MainActivity.this);
        alertView = getLayoutInflater().inflate(R.layout.login_popup_manager, null);

        alertDialog = alert.create();

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(null);
        alert.setView(alertView);

        ad = alert.show();
        loginPopupManagerBinding = DataBindingUtil.bind(alertView);

        // alertDialog.show();

    }

    private void alertDialogDriver() {
        alert = new AlertDialog.Builder(MainActivity.this);
        alertView = getLayoutInflater().inflate(R.layout.login_popup_driver, null);

        alertDialog = alert.create();

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(null);
        alert.setView(alertView);

        ad = alert.show();
        loginPopupDriverBinding = DataBindingUtil.bind(alertView);

        // alertDialog.show();

    }

    public void logInAsManagerClick(View view) {
        Toast.makeText(this, "Log In", Toast.LENGTH_SHORT).show();
        email = loginPopupManagerBinding.emailET.getText().toString().trim();
        password = loginPopupManagerBinding.passwordET.getText().toString().trim();
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, password, Toast.LENGTH_SHORT).show();
        nullcheck(email, password);
        if (nullCheckFlag == 0) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener(task -> {
                Toast.makeText(MainActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                ad.dismiss();
                goToManagerActivity = new Intent(MainActivity.this, ManagerActivty.class);
                startActivity(goToManagerActivity);
            }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Fill Up the fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void nullcheck(String email, String password) {
        nullCheckFlag = 0;
        if (email.isEmpty()) {
            nullCheckFlag = 1;
        }
        if (password.isEmpty()) {
            nullCheckFlag = 1;
        }
    }

    public void signUpBtnClick(View view) {
        Intent goToSignUpManagerActivity = new Intent(this, SignUpManagerActivity.class);
        startActivity(goToSignUpManagerActivity);
    }

    public void managerBtnOnClick(View view) {
        alertDialogManager();
    }

    public void driverBtnOnClick(View view) {
        //FirebaseUser user=mAuth.getCurrentUser();
        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent goToDriverActivity = new Intent(MainActivity.this, DriverActivity.class);
            startActivity(goToDriverActivity);
        } else {*/
        // No user is signed in
        alertDialogDriver();
//        }

    }

    public void logInAsDriverClick(View view) {


        licensePlate = loginPopupDriverBinding.licensePlateET.getText().toString();
        phoneNumber = loginPopupDriverBinding.phoneNoET.getText().toString();
        groupId = loginPopupDriverBinding.managerIdET.getText().toString();

        databaseReference = FirebaseDatabase.getInstance().getReference("root").child("ManagerList");

        databaseReference.orderByChild("groupId").equalTo(groupId)/*.equalTo(phoneNumber, "phoneNumber")*/.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressWarnings("SuspiciousMethodCalls")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String driverLicenseNo;
                    Log.i(TAG, "onDataChange: " + snapshot.toString());
                    if (snapshot.hasChildren())
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            Log.i(TAG, "onDataChange Inside: " + dataSnapshot1.toString());
                            try {
                                currentManager = dataSnapshot1.getValue(Manager.class);
                                assert currentManager != null;
                                for (DriverPhoneMac dPM : currentManager.getDriverPhoneNumbers())
                                {
                                    if (dPM.getNumber().equals(phoneNumber)) {
                                        if (dPM.getMacId().equals(MAC_ID) || dPM.getMacId().equals("default")) {
                                            currentDriver = dPM;
                                            if (dPM.getMacId().equals("default")) {
                                                databaseReference.child(currentManager.getManagerId()).child("driverPhoneNumbers").child(currentDriver.getLocalId()).child("macId").setValue(MAC_ID);
//                                                FirebaseDatabase.getInstance().getReference("root").child("locations").child(licensePlate).child("licenseNo").setValue(dPM.getLicenseNo());

                                                goToDriverActivity.putExtra("driverLicenseNo", currentDriver.getLicenseNo());
                                                goToDriverActivity.putExtra("licensePlateNo", licensePlate);
                                                goToDriverActivity.putExtra("groupId", groupId);
                                                startActivity(goToDriverActivity);

                                            }

                                            if(currentDriver.getMacId().equals(MAC_ID)){
                                                goToDriverActivity.putExtra("driverLicenseNo", currentDriver.getLicenseNo());
                                                goToDriverActivity.putExtra("licensePlateNo", licensePlate);
                                                goToDriverActivity.putExtra("groupId", groupId);
                                                startActivity(goToDriverActivity);

                                            }


                                        } else {
                                            Toast.makeText(getApplicationContext(), "This Device needs to be registered, Contact administration", Toast.LENGTH_LONG).show();
                                        }
                                        return;
                                    }
                                }
                                Log.i(TAG, "onChildAdded: " + currentManager.getDriverPhoneNumbers().contains(new DriverPhoneMac(phoneNumber, true)));

                            } catch (Exception x) {
                                x.printStackTrace();
                            }
                        }
                        // managerList=dataSnapshot.getValue(Manager.class);
                    else {
                        currentManager = snapshot.getValue(Manager.class);
                        Log.i(TAG, "onChildAdded: " + currentManager.getDriverPhoneNumbers().contains("phone:" + phoneNumber));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        licensePlate=loginPopupDriverBinding.licensePlateET.getText().toString();
//
//        GlobalVariables.licensePlate=licensePlate;
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url="https://us-central1-imperial-tiger-237207.cloudfunctions.net/getCredentialsById?fbclid=IwAR3wL4XhJ3LTvxIzzfKmUhw09QppSf1SvHGC8V5c5rXsgazoFncYvS4zimw";
//        Response.Listener listenResponse=new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject hotelJSON = null;
//
//                    hotelJSON = new JSONObject(response).getJSONObject("result");
//
//                    email = hotelJSON.getString("email");
//                    password = hotelJSON.getString("password");
//                    Toast.makeText(MainActivity.this, "Email"+email, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this, "Password"+password, Toast.LENGTH_SHORT).show();
//
//                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            Toast.makeText(MainActivity.this, "Successfully Signed In", Toast.LENGTH_SHORT).show();
//                            Intent goToDriverActivity=new Intent(MainActivity.this,DriverActivity.class);
//                            startActivity(goToDriverActivity);
//                        }
//                    });
//
//
//
//
//
//                }
//                catch (Exception e)
//                {
//                    Toast.makeText(MainActivity.this, "Response Listener Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//
//
//        };
//        Response.ErrorListener listenError  = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error instanceof TimeoutError) {
//                    Log.i("errors", "onErrorResponse: Timeout");
//
//                }
//                error.printStackTrace();
//            }
//        };
//
//        try {
//             signInJson=new JSONObject().put("data",new JSONObject().put("phoneNumber",phoneNumber).put("groupId",groupId).put("licensePlate",licensePlate));
//
//        }catch (Exception e)
//        {
//            Toast.makeText(this,"Json Object creation Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//
//
//        final String mRequestBody = signInJson.toString();
//        Log.d("TAG","json" +signInJson.toString(4));
//
//
//        StringRequest request = new StringRequest(POST, url, listenResponse, listenError) {
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//
//            @Override
//            public byte[] getBody() {
//                return mRequestBody.getBytes(StandardCharsets.UTF_8);
//            }
//        };
//
//        queue.add(request);


    }

    public void logInAsCheckerClick(View view) {


        phoneNumber = loginPopupCheckerBinding.phoneNoET.getText().toString();
        groupId = loginPopupCheckerBinding.managerIdET.getText().toString();

        databaseReference = FirebaseDatabase.getInstance().getReference("root").child("ManagerList");
        databaseReference.orderByChild("groupId").equalTo(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressWarnings("SuspiciousMethodCalls")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String driverLicenseNo;
                    Log.i(TAG, "onDataChange: " + snapshot.toString());
                    if (snapshot.hasChildren())
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            Log.i(TAG, "onDataChange Inside: " + dataSnapshot1.toString());
                            try {
                                currentManager = dataSnapshot1.getValue(Manager.class);
                                assert currentManager != null;
                                for (String checkerPhoneNo : currentManager.getCheckerPhoneNumbers())
                                {
                                    if((checkerPhoneNo.equals(phoneNumber)))
                                    {
                                        goToCheckerActivity.putExtra("groupId",groupId);
                                        startActivity(goToCheckerActivity);
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "You are not registered to this bus group. Please Contact with the authority.", Toast.LENGTH_SHORT).show();
                                    }
//                                    if (dPM.getNumber().equals(phoneNumber)) {
//                                        if (dPM.getMacId().equals(MAC_ID) || dPM.getMacId().equals("default")) {
//                                            currentDriver = dPM;
//                                            if (dPM.getMacId().equals("default")) {
//                                                databaseReference.child(currentManager.getManagerId()).child("driverPhoneNumbers").child(currentDriver.getLocalId()).child("macId").setValue(MAC_ID);
////                                                FirebaseDatabase.getInstance().getReference("root").child("locations").child(licensePlate).child("licenseNo").setValue(dPM.getLicenseNo());
//
//                                                goToDriverActivity.putExtra("driverLicenseNo", currentDriver.getLicenseNo());
//                                                goToDriverActivity.putExtra("licensePlateNo", licensePlate);
//                                                goToDriverActivity.putExtra("groupId", groupId);
//                                                startActivity(goToDriverActivity);
//
//                                            }
//
//                                            if(currentDriver.getMacId().equals(MAC_ID)){
//                                                goToDriverActivity.putExtra("driverLicenseNo", currentDriver.getLicenseNo());
//                                                goToDriverActivity.putExtra("licensePlateNo", licensePlate);
//                                                goToDriverActivity.putExtra("groupId", groupId);
//                                                startActivity(goToDriverActivity);
//
//                                            }
//
//
//                                        } else {
//                                            Toast.makeText(getApplicationContext(), "This Device needs to be registered, Contact administration", Toast.LENGTH_LONG).show();
//                                        }
//                                        return;
//                                    }
                                }
                                Log.i(TAG, "onChildAdded: " + currentManager.getDriverPhoneNumbers().contains(new DriverPhoneMac(phoneNumber, true)));

                            } catch (Exception x) {
                                x.printStackTrace();
                            }
                        }
                        // managerList=dataSnapshot.getValue(Manager.class);
                    else {
                        currentManager = snapshot.getValue(Manager.class);
                        Log.i(TAG, "onChildAdded: " + currentManager.getDriverPhoneNumbers().contains("phone:" + phoneNumber));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        licensePlate=loginPopupDriverBinding.licensePlateET.getText().toString();

//        GlobalVariables.licensePlate=licensePlate;
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url="https://us-central1-imperial-tiger-237207.cloudfunctions.net/getCredentialsById?fbclid=IwAR3wL4XhJ3LTvxIzzfKmUhw09QppSf1SvHGC8V5c5rXsgazoFncYvS4zimw";
//        Response.Listener listenResponse=new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject hotelJSON = null;
//
//                    hotelJSON = new JSONObject(response).getJSONObject("result");
//
//                    email = hotelJSON.getString("email");
//                    password = hotelJSON.getString("password");
//                    Toast.makeText(MainActivity.this, "Email"+email, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this, "Password"+password, Toast.LENGTH_SHORT).show();
//
//                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            Toast.makeText(MainActivity.this, "Successfully Signed In", Toast.LENGTH_SHORT).show();
//                            Intent goToDriverActivity=new Intent(MainActivity.this,DriverActivity.class);
//                            startActivity(goToDriverActivity);
//                        }
//                    });
//
//
//
//
//
//                }
//                catch (Exception e)
//                {
//                    Toast.makeText(MainActivity.this, "Response Listener Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//
//            }


    }

    ;
//        Response.ErrorListener listenError  = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error instanceof TimeoutError) {
//                    Log.i("errors", "onErrorResponse: Timeout");
//
//                }
//                error.printStackTrace();
//            }
//        };
//
//        try {
//
//            signInJson=new JSONObject().put("data",new JSONObject().put("phoneNumber",phoneNumber).put("groupId",groupId).put("licensePlate",licensePlate));
//
//        }catch (Exception e)
//        {
//            Toast.makeText(this,"Json Object creation Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//
//
//        final String mRequestBody = signInJson.toString();
//        Log.d("TAG","json" +signInJson.toString(4));
//
//
//        StringRequest request = new StringRequest(POST, url, listenResponse, listenError) {
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//
//            @Override
//            public byte[] getBody() {
//                return mRequestBody.getBytes(StandardCharsets.UTF_8);
//            }
//        };
//
//        queue.add(request);


//    }

    public void checkerBtnOnClick(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent goCheckerActivity = new Intent(MainActivity.this, CheckerActivity.class);
            startActivity(goCheckerActivity);
        } else {
            // No user is signed in
            alertDialogChecker();
        }
    }

    private void alertDialogChecker() {
        alert = new AlertDialog.Builder(MainActivity.this);
        alertView = getLayoutInflater().inflate(R.layout.login_popup_checker, null);

        alertDialog = alert.create();

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(null);
        alert.setView(alertView);

        ad = alert.show();
        loginPopupCheckerBinding = DataBindingUtil.bind(alertView);

        // alertDialog.show();

    }
}
