package com.example.vooglestaff.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.vooglestaff.R;
import com.example.vooglestaff.databinding.ActivitySignUpManagerBinding;
import com.example.vooglestaff.databinding.ActivitySignUpManagerBinding;
import com.example.vooglestaff.pojoClass.Manager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpManagerActivity extends AppCompatActivity {

    ActivitySignUpManagerBinding activitySignUpManagerBinding;
    String busGroupName,email,password,confirmPassword,busGroupId;
    int nullcheckFlag,passwordCheckFlag,busIdCheckFlag;
    Manager manager;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    DatabaseReference databaseReference,managerListRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         activitySignUpManagerBinding =DataBindingUtil.setContentView(this,R.layout.activity_sign_up_manager);
         databaseReference= FirebaseDatabase.getInstance().getReference("");

    }

    private void passwordCheck() {
        passwordCheckFlag=0;
        if(!password.equals(confirmPassword))
        {
            passwordCheckFlag=1;
        }
        if(password.length()<6)
        {
            passwordCheckFlag=1;
        }
    }

    private void nullcheck() {
        nullcheckFlag=0;
        if(busGroupName.isEmpty())
        {
            nullcheckFlag=1;
            activitySignUpManagerBinding.busGroupNameET.setError("Empty Field");
        }
        if(email.isEmpty())
        {
            nullcheckFlag=1;
            activitySignUpManagerBinding.emailET.setError("Empty Field");
        }
        if(password.isEmpty())
        {
            nullcheckFlag=1;
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        }
        if(confirmPassword.isEmpty())
        {
            nullcheckFlag=1;
            Toast.makeText(this, "Please Confirm Password", Toast.LENGTH_SHORT).show();
        }
        if(busGroupId.isEmpty())
        {
            nullcheckFlag=1;
            activitySignUpManagerBinding.busGroupIdET.setError("Empty Field");
        }
    }

    public void signUpBtnClick(View view) {
        busGroupName = activitySignUpManagerBinding.busGroupNameET.getText().toString();
        email = activitySignUpManagerBinding.emailET.getText().toString();
        password = activitySignUpManagerBinding.passwordET.getText().toString();
        confirmPassword = activitySignUpManagerBinding.confirmPasswordET.getText().toString();
        busGroupId=activitySignUpManagerBinding.busGroupIdET.getText().toString();
        manager=new Manager();
        nullcheck();
        passwordCheck();
        busGroupIdCheck();
        if (nullcheckFlag == 0  && passwordCheckFlag == 0 && busIdCheckFlag==0) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        if (mAuth.getCurrentUser() != null) {
                            setUser();
                            addUser(manager);
                            //Intent goToListActivity = new Intent(SignUpActivity.this, ListActivity.class);
                          //  goToListActivity.putExtra("flag", "signedUp");
                          //  startActivity(goToListActivity);
                        } else {
                            Toast.makeText(SignUpManagerActivity.this, "User is Null", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            activitySignUpManagerBinding.emailLayout.setError("Registration Failed as this email already exists");
                        else if (task.getException() instanceof FirebaseAuthWeakPasswordException)
                            activitySignUpManagerBinding.passwordLayout.setError("Weak Password, use more than 6 letters");
                        else if (task.getException() instanceof FirebaseNetworkException)
                            Toast.makeText(SignUpManagerActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
//                    manager.setManagerId(mAuth.getCurrentUser().getUid());

                }

            });
        }

    }

    private void setUser() {
        manager.setManagerId(mAuth.getCurrentUser().getUid());
        manager.setEmail(email);
        manager.setGroupId(busGroupId);
        manager.setGroupName(busGroupName);
        manager.setPassword(password);
    }

    private void busGroupIdCheck() {
        busIdCheckFlag=0;
        managerListRef = databaseReference.child("root").child("ManagerList");
        managerListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.child("groupId").getValue().toString().trim().equals(busGroupId)) {
                            busIdCheckFlag=1;
                            activitySignUpManagerBinding.busGroupIdLayout.setError("Bus Group Id already exists");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addUser(Manager manager) {
        databaseReference.child("root").child("ManagerList").child(manager.getManagerId()).setValue(manager).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SignUpManagerActivity.this, "Signed Up Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpManagerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
