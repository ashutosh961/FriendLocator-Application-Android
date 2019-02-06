package com.example.ashutosh.mapapplication;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by Ashutosh on 23-11-2016.
 */


public class MyService extends Service implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *

     */

    private static final String FIREBASE_URL="https://mapapplication-141609.firebaseio.com/Locations";
   Location location;
    GoogleApiClient mGoogleApiClient;
    protected LocationManager locationmanager;
    protected LocationListener locationListener;
     String latitude, longitude;
     boolean gps_enabled, network_enabled;
     String mLastUpdateTime;
 //   boolean isNetworkEnabled = locationmanager.isProviderEnabled(locationmanager.NETWORK_PROVIDER);
   // boolean isGPSEnabled = locationmanager.isProviderEnabled(locationmanager.GPS_PROVIDER);

    String ServiceMobile;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Firebase.setAndroidContext(this);
        Log.d("Testing", "Service got created");
        Toast.makeText(this, "ServiceClass.onCreate()", Toast.LENGTH_LONG).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        //   super.onDestroy();
        Intent intent = new Intent(getBaseContext(), MyService.class);
        startService(intent);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Firebase FirebaseRef1=new Firebase(FIREBASE_URL);

        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        Firebase.setAndroidContext(this);
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences sharedPreferences2= getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String mail=sharedPreferences1.getString("mail", "");
        String UID2=sharedPreferences2.getString("UID", "");
        System.out.println("Service Mail:"+mail);
        System.out.println("Service UID:"+UID2);
        ServiceMobile=sharedPreferences2.getString("Mobile", "");
        String  FIREBASE_URL1="https://mapapplication-141609.firebaseio.com/Notifications";
        Firebase FirebaseRef=new Firebase(FIREBASE_URL1);
        //SharedPreferences BTrack1,YouTrack1;
        FirebaseRef.child(ServiceMobile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Inside firebase service");
                ArrayList<String> YouTrack = new ArrayList<String>();
                ArrayList<String> BTrack = new ArrayList<String>();
                Map keymap = new HashMap();

                for (DataSnapshot Postsnapshot : dataSnapshot.getChildren()) {
                    keymap.put("key", Postsnapshot.getKey());
                   // System.out.println("Keymap1 service Setupmap value:" + Postsnapshot.getKey());
                    //String unique1 = (String) keymap.get("key");
                  HashMap ServiceData=(HashMap)Postsnapshot.getValue();
                    String Mobile1=(String)ServiceData.get("Mobile");
                    BTrack.add(Mobile1);
                    SharedPreferences   BTrack1 = getSharedPreferences("BTrack1", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = BTrack1.edit();
                    Set<String> Hashset1=new HashSet<String>();
                    Hashset1.addAll(BTrack);
                    editor1.putStringSet("BTrack",Hashset1);
                    editor1.commit();
                    Map<String, ?> allPrefs1 = BTrack1.getAll(); //your sharedPreference
                    Set<String> set1 = allPrefs1.keySet();
                    for(String s : set1){
                        Log.d("String:", s + "<" + allPrefs1.get(s).getClass().getSimpleName() + "> =  "
                                + allPrefs1.get(s).toString());
                    }

                    System.out.println("BTrack Element:" + Mobile1);
                    System.out.println("People Currently  you are tracking:" + Mobile1);
                    if(ServiceData.containsKey("BeingTracked")){
                        System.out.println("Inside comparison");
                        boolean beingtracked=(boolean)ServiceData.get("BeingTracked");
                        if(beingtracked==true){
                            Map BeingTrack=ServiceData;
                            String Mobile=(String)ServiceData.get("Mobile");
                            YouTrack.add(Mobile);
                            SharedPreferences  YouTrack1 = getSharedPreferences("YouTrack1", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = YouTrack1.edit();
                            Set<String> Hashset2=new HashSet<String>();
                            Hashset2.addAll(YouTrack);
                            editor2.putStringSet("YouTrack", Hashset2);
                            editor2.commit();
                            Map<String, ?> allPrefs = YouTrack1.getAll(); //your sharedPreference
                            Set<String> set = allPrefs.keySet();
                            for(String s : set){
                                Log.d("String:", s + "<" + allPrefs.get(s).getClass().getSimpleName() + "> =  "
                                        + allPrefs.get(s).toString());
                            }

                            //System.out.println("People tracking you:\n"+BeingTrack);
                            System.out.println("YouTrack Elements "+Mobile);
                            System.out.println("People tracking you:"+Mobile);
                        }
                    }





              //      HashMap ServiceData1=(HashMap)Postsnapshot.getValue();
                //    if(ServiceData1.containsKey("Track1")){
                  //  boolean track = (boolean) ServiceData.get("Track1");
                    //if (track == true) {
                      // System.out.println(" people currently being tracked by you" + ServiceData);
                     //}
                    //if(ServiceData.get("BeingTracked")!=null)
                    //{  Map ServiceData2=ServiceData;
                     //  }}

                }}



            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds
        }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Toast.makeText(this, "ServiceClass.onStart()", Toast.LENGTH_LONG).show();
        Log.d("Testing", "Service got started");

    }

    @Override
    public void onLocationChanged(Location location) {
        Firebase FirebaseRef1=new Firebase(FIREBASE_URL);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
        Date date = new Date();
        mLastUpdateTime = dateFormat.format(date);
        Map mcoordinate=new HashMap<>();
        mcoordinate.put("latitude", location.getLatitude());
        mcoordinate.put("longitude", location.getLongitude());
        mcoordinate.put("DateTime", mLastUpdateTime);
        FirebaseRef1.child(ServiceMobile).updateChildren(mcoordinate);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      Toast.makeText(getApplicationContext(),"GPS or network connection has been changed.",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
       Toast.makeText(getApplicationContext(),"Internet is disabled",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(),"Internet Connection has been suspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Internet Connection has failed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    //public void run(){}
    }



