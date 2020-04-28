package com.example.vooglestaff.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.vooglestaff.R;
import com.example.vooglestaff.databinding.ActivityMainBinding;
import com.example.vooglestaff.databinding.LoginPopupDriverBinding;
import com.example.vooglestaff.databinding.LoginPopupManagerBinding;
import com.example.vooglestaff.databinding.LoginPopupManagerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding=DataBindingUtil.setContentView(this,R.layout.activity_main);
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
        alertDialogDriver();
    }

    public void logInAsDriverClick(View view) {
    }
}
