package com.example.ashutosh.mapapplication;

/**
 * Created by Ashutosh on 27-08-2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity  {

    private EditText inputEmail, inputPassword;
  //   private static final String FIREBASE_URL="https://mapapplication-141609.firebaseio.com/";
 //    private Firebase FireBaseRef;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private static final String FIREBASE_URL="https://mapapplication-141609.firebaseio.com/Locations";
    private static final String FIREBASE_URL1="https://mapapplication-141609.firebaseio.com/Users";
   public  SharedPreferences sharedPreferences1,sharedPreferences2;
    private Firebase FireBaseRef;
    private FirebaseAuth.AuthStateListener mauthlistener;
    private Button btnSignup, btnLogin, btnReset;
   public String email;
    public String UID,Mobile;
    public String UID1,Mobile1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences2 = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Firebase.setAndroidContext(this);

        FireBaseRef=new Firebase(FIREBASE_URL1);
        //Get Firebase auth instance
          auth=FirebaseAuth.getInstance();



        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MapsActivity.class));
            finish();
        }


        // set the view now
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        //FireBaseConnection
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                             //Sign in Fail
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {


                                   // FireBaseRef.child("9426766184").removeValue();
                                    sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    FireBaseRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                System.out.println("Inside Login Activity");
                                                Map keymap = new HashMap();
                                                keymap.put("key", dataSnapshot1.getKey());
                                                System.out.println("Keymap1 Setupmap value:" + dataSnapshot1.getKey());
                                                String unique1 = (String) keymap.get("key");
                                                FireBaseRef.child(unique1).addChildEventListener(new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(DataSnapshot dataSnapshot2, String s) {
                                                        Map ActualData = new HashMap();
                                                        ActualData = (Map) dataSnapshot2.getValue();
                                                        System.out.println("Actual Data4:" + ActualData);

                                                        if (ActualData.containsValue(email)) {
                                                            Map ActualData3 = ActualData;
                                                            UID = (String) ActualData3.get("UniqueID");
                                                            Mobile = (String) ActualData3.get("Mobile");
                                                            System.out.println("Unique ID is:" + UID);
                                                            System.out.println("Mobile is:" + Mobile);
                                                            UID1 = UID.toString();
                                                            Mobile1 = Mobile.toString();
                                                            sharedPreferences2 = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                                                            editor2.putString("UID", UID);
                                                            editor2.putString("Mobile", Mobile);
                                                            editor2.commit();
                                                        }

                                                    }

                                                    @Override
                                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                    }

                                                    @Override
                                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                                    }

                                                    @Override
                                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                    }

                                                    @Override
                                                    public void onCancelled(FirebaseError firebaseError) {
                                                        Toast.makeText(getApplicationContext(),"Couldnt fetch details!,Please Login again",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                             Toast.makeText(getApplicationContext(),"Couldnt fetch details!,Please Login again",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    System.out.println("Unique ID second time is:" + UID);
                                    SharedPreferences.Editor editor =sharedPreferences1.edit();
                                    editor.putString("mail", email);
                                    editor.commit();

                                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);

                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }
}