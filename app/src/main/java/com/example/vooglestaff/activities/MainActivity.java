package com.example.vooglestaff.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vooglestaff.R;
import com.example.vooglestaff.constants.GlobalVariables;
import com.example.vooglestaff.databinding.ActivityMainBinding;
import com.example.vooglestaff.databinding.LoginPopupDriverBinding;
import com.example.vooglestaff.databinding.LoginPopupManagerBinding;
import com.example.vooglestaff.databinding.LoginPopupManagerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import static com.android.volley.Request.Method.POST;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    AlertDialog.Builder alert;
    AlertDialog alertDialog, ad;
    AlertDialog.Builder reportBuild;
    LoginPopupManagerBinding loginPopupManagerBinding;
    LoginPopupDriverBinding loginPopupDriverBinding;
    View alertView;
    String email, password;
    int nullCheckFlag;
    String phoneNumber,groupId,licensePlate;
    JSONObject signInJson;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding=DataBindingUtil.setContentView(this,R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        // loginPopupBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.login_popup_manager, null, false);
        //
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
        email=loginPopupManagerBinding.emailET.getText().toString().trim();
        password=loginPopupManagerBinding.passwordET.getText().toString().trim();
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, password, Toast.LENGTH_SHORT).show();
        nullcheck(email,password);
        if (nullCheckFlag==0) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @SuppressWarnings("NullableProblems")
                @Override
                public void onComplete(Task<AuthResult> task) {
                    Toast.makeText(MainActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                    ad.dismiss();
                    Intent goToManagerActivity=new Intent(MainActivity.this,ManagerActivty.class);
                    startActivity(goToManagerActivity);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(this, "Fill Up the fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void nullcheck(String email, String password) {
        nullCheckFlag=0;
        if(email.isEmpty())
        {
            nullCheckFlag=1;
        }
        if(password.isEmpty())
        {
            nullCheckFlag=1;
        }
    }

    public void signUpBtnClick(View view) {
        Intent goToSignUpManagerActivity=new Intent(this, SignUpManagerActivity.class);
        startActivity(goToSignUpManagerActivity);
    }

    public void managerBtnOnClick(View view) {
        alertDialogManager();
    }

    public void driverBtnOnClick(View view) {
        //FirebaseUser user=mAuth.getCurrentUser();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent goToDriverActivity=new Intent(MainActivity.this,DriverActivity.class);
            startActivity(goToDriverActivity);
        } else {
            // No user is signed in
            alertDialogDriver();
        }

    }

    public void logInAsDriverClick(View view) throws JSONException {


        phoneNumber=loginPopupDriverBinding.phoneNoET.getText().toString();
        groupId=loginPopupDriverBinding.managerIdET.getText().toString();
        licensePlate=loginPopupDriverBinding.licensePlateET.getText().toString();
        GlobalVariables.licensePlate=licensePlate;

        RequestQueue queue = Volley.newRequestQueue(this);
        String url="https://us-central1-imperial-tiger-237207.cloudfunctions.net/getCredentialsById?fbclid=IwAR3wL4XhJ3LTvxIzzfKmUhw09QppSf1SvHGC8V5c5rXsgazoFncYvS4zimw";
        Response.Listener listenResponse=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject hotelJSON = null;

                    hotelJSON = new JSONObject(response).getJSONObject("result");

                    email = hotelJSON.getString("email");
                    password = hotelJSON.getString("password");
                    Toast.makeText(MainActivity.this, "Email"+email, Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Password"+password, Toast.LENGTH_SHORT).show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(MainActivity.this, "Successfully Signed In", Toast.LENGTH_SHORT).show();
                            Intent goToDriverActivity=new Intent(MainActivity.this,DriverActivity.class);
                            startActivity(goToDriverActivity);
                        }
                    });





                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "Response Listener Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }



        };
        Response.ErrorListener listenError  = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.i("errors", "onErrorResponse: Timeout");

                }
                error.printStackTrace();
            }
        };

        try {
             signInJson=new JSONObject().put("data",new JSONObject().put("phoneNumber",phoneNumber).put("groupId",groupId).put("licensePlate",licensePlate));

        }catch (Exception e)
        {
            Toast.makeText(this,"Json Object creation Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }



        final String mRequestBody = signInJson.toString();
        Log.d("TAG","json" +signInJson.toString(4));


        StringRequest request = new StringRequest(POST, url, listenResponse, listenError) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return mRequestBody.getBytes(StandardCharsets.UTF_8);
            }
        };

        queue.add(request);


    }

}
