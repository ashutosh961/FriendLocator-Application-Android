package com.example.ashutosh.mapapplication;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,LoaderManager.LoaderCallbacks<Cursor>,ActivityCompat.OnRequestPermissionsResultCallback, LocationListener, OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    GoogleApiClient mGoogleApiClient;
    private Location location;
    Intent intent=null;
    private String MailId="";
    private static final String FIREBASE_URL="https://mapapplication-141609.firebaseio.com/Locations";
    private static final String FIREBASE_URL1="https://mapapplication-141609.firebaseio.com/Users";
    private static final String FIREBASE_URL2="https://mapapplication-141609.firebaseio.com/Notifications";
    private Firebase FireBaseRef;
    private Firebase FireBaseRef1;
    private Firebase FireBaseRef2;
    private Uri imageuri;
    SupportMapFragment mapfragment;
    DrawerLayout drawerLayout;
     Toolbar toolbar;
    Context  context=this;
    String mLastUpdateTime;
    private FirebaseAuth auth;
    Map keymap;
     boolean  isGpsEnabled=  false;
    boolean  isNetworkEnabled=false;
   public SharedPreferences sharedPreferences;
   public String mail;
    String Mobile1;
    String Unique;
    int count=0;

    String Mobile2,UID2;
    String Mobile4,fetchmail;
    String Mobile3,UID1;
    double lat,lng;
    String fetchid,ServiceMobile;
    FirebaseStorage mstorage1;
    StorageReference ref1,mainref;
    String Image="Picture.jpg";
    String filename="Image.jpg";
    Bitmap bm,bm1;
    String Name;
    public SharedPreferences sharedPreferences1,sharedPreferences2;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);

         buildGoogleApiClient();
        mGoogleApiClient.connect();
         setContentView(R.layout.activity_maps);

         Firebase.setAndroidContext(this);

         FireBaseRef=new Firebase(FIREBASE_URL);
         FireBaseRef1=new Firebase(FIREBASE_URL1);
         FireBaseRef2=new Firebase(FIREBASE_URL2);
         mstorage1=FirebaseStorage.getInstance();
         ref1=mstorage1.getReferenceFromUrl("gs://mapapplication-141609.appspot.com");
         sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
         sharedPreferences2= getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
         mail=sharedPreferences1.getString("mail", "");
         UID2=sharedPreferences2.getString("UID", "");
         ServiceMobile=sharedPreferences2.getString("Mobile", "");
         System.out.println("Shared Preference UID is:"+UID2);
         System.out.println("Service Mobile:" + ServiceMobile);
         Intent intService =  new Intent(this, MyService.class);
         intService.putExtra("Mobile",ServiceMobile);
         registerAlarm(context);
         startService(intService);

         setUpMapIfNeeded();

         toolbar = (Toolbar) findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);







         setUpMapIfNeeded();



     }
        public static void registerAlarm(Context context) {
            Calendar cal=Calendar.getInstance();
            Intent intent = new Intent(context, MyService.class);
            PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);
            AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30*1000, pintent);

    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.SearchFriend:

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        final LinearLayout layout = new LinearLayout(context);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        final EditText input = new EditText(context);
                        final EditText UID = new EditText(context);

                        layout.addView(input);
                        layout.addView(UID);

                        builder.setView(layout);
                        builder.setTitle("Please enter Mobile-no and UID ");
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        UID.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        builder.setView(input);
                        builder.setView(UID);
                        builder.setView(layout);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MailId = input.getText().toString();
                                UID1=UID.getText().toString();
                                System.out.println(MailId);
                                System.out.println(UID1);
                                Mobile4=MailId;
                                sendRequest(Mobile4);
                                getRequest(Mobile4);
                               fetchfirebaselocation();


                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();



                        break;
                    case R.id.settings:
                        intent = new Intent(MapsActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.Tracking:
                        intent=new Intent(MapsActivity.this,TrackerActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:

                        auth = FirebaseAuth.getInstance();
                        System.out.println(auth);
                        if (auth.getCurrentUser() != null) {
                            auth.signOut();
                            intent = new Intent(MapsActivity.this, LoginActivity.class);
                            startActivity(intent);
                            break;
                        }

                }
                return true;
            }
        });






        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
        TextView UID=(TextView)header.findViewById(R.id.tv_UID);
        tv_email.setText("Mail:"+mail);
        UID.setText("UID:"+UID2);
        tv_email.setTextSize(16);
        UID.setTextSize(16);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }




        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }




    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }



    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mapfragment.newInstance();
             mapfragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mapfragment.getMapAsync(this);

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                //onMapReady(mMap);

                setUpMap();
            }
        }
    }
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {


        initNavigationDrawer();



        File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File filelocation=new File(sdcard + "/FriendLocator/ProfileImages/" + mail + "/Image.jpg");

        if(!filelocation.exists()){loadimage(mail);}


        location = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        LocationManager locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            isGpsEnabled = locationmanager.isProviderEnabled(locationmanager.GPS_PROVIDER);
        } catch (Exception e) {
            System.out.println("GPS Provider issue\n ");
        }
        try {
            isNetworkEnabled = locationmanager.isProviderEnabled(locationmanager.NETWORK_PROVIDER);
        } catch (Exception e) {
            System.out.println("Network Provider issue\n ");
        }

        if(!isGpsEnabled && !isNetworkEnabled)
        {

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Warning");
            builder.setMessage("GPS or Network Location services are not enabled,Please enable Location services.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                    startActivity(intent);
                }


            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();
        }


        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, this);


            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);

        } else if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

        {
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);


            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);


        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && (isGpsEnabled||isNetworkEnabled)) {

            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);

            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();

                System.out.println("Database Firebase fetching \n");
                Firebase ref1=new Firebase(FIREBASE_URL1).child(ServiceMobile);

                ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap HashMap1 = new HashMap();
                        HashMap1 = (HashMap) dataSnapshot.getValue();
                        System.out.println(HashMap1);
                         Name = (String) HashMap1.get("Name");
                        System.out.println("Name is:"+Name);
                    }


                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });



                FireBaseRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postsnapshot1 : dataSnapshot.getChildren()) {
                            keymap = new HashMap();
                            keymap.put("key", postsnapshot1.getKey());
                            System.out.println("Keymap1 Setupmap value:" + postsnapshot1.getKey());
                            String unique1 = (String) keymap.get("key");
                            FireBaseRef1.child(unique1).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot2, String s) {

                                    Map actualData2 = (Map) dataSnapshot2.getValue();
                                    String mai12 = (String) actualData2.get("Email");
                                    System.out.println("Actual Data:" + actualData2);
                                    System.out.println("mail2:" + mai12);
                                    if (actualData2.containsValue(mail)) {
                                        Map actualData3 = actualData2;
                                        System.out.println("ActualData3" + actualData3);
                                        Mobile3 = (String) actualData3.get("Mobile");
                                        System.out.println("Mobile3:" + Mobile3);
                                        Map mcoordinate = new HashMap<>();
                                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                        dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
                                        Date date = new Date();
                                        mLastUpdateTime = dateFormat.format(date);


                                        mcoordinate.put("latitude", lat);
                                        mcoordinate.put("longitude", lng);
                                        mcoordinate.put("DateTime", mLastUpdateTime);
                                        mcoordinate.put("Email-Id", mail);
                                        mcoordinate.put("UniqueID",UID2);
                                        System.out.println("Mobile3:" + Mobile3);
                                        FireBaseRef.child(Mobile3).removeValue();
                                        FireBaseRef.child(Mobile3).push().setValue(mcoordinate);
                                        Unique = FireBaseRef.getKey();
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

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                        System.out.println("Firebase connection error\n");
                    }
                });

                System.out.println("Inside mail");
                System.out.println(mail);
                System.out.println(sdcard);

                Unique = FireBaseRef.getKey();
                System.out.println("Unique:" + Unique);
                System.out.println("Database Firebase Uploading\n");
                ContentValues contentValues = new ContentValues();
                contentValues.put(LocationsDB.FIELD_lat, lat);
                contentValues.put(LocationsDB.FIELD_lon, lng);

                contentValues.put(LocationsDB.FIELD_zoom, mMap.getCameraPosition().zoom);



                   BitmapFactory.Options opt = new BitmapFactory.Options();
                   opt.inDither = true;
                   opt.inJustDecodeBounds=false;
                   opt.inPreferredConfig = Bitmap.Config.ARGB_8888;




                  bm =BitmapFactory.decodeFile(sdcard + "/FriendLocator/ProfileImages/" + mail + "/Image.jpg",opt);
                 if(bm==null)
                 {System.out.println("bitmap object null");
                  //   Bitmap bt=getResizedBitmap(bm,200);
                     LatLng loc = new LatLng(lat, lng);
                     mMap.addMarker(new MarkerOptions().position(loc).title("New Marker")).setDraggable(true);
                     getCompleteAddressString(lat, lng);
                     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));
                 }
                else if(bm!=null)
                 {System.out.println("bitmap object  present");
                     Bitmap bt=getResizedBitmap(bm,200);
                     LatLng loc = new LatLng(lat, lng);
                     mMap.addMarker(new MarkerOptions().position(loc).title("New Marker").icon(BitmapDescriptorFactory.fromBitmap(bt))).setDraggable(true);
                     getCompleteAddressString(lat, lng);
                     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));

                 }


                  //     Bitmap bt = getResizedBitmap(bm, 400);
                       LatLng loc = new LatLng(lat, lng);
                       mMap.addMarker(new MarkerOptions().position(loc).title("New Marker")).setDraggable(true);
                       getCompleteAddressString(lat, lng);
                       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));




                    Uri uri = getContentResolver().insert(LocationContentProvider.CONTENT_URI, contentValues);


                    Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
                }

            }
        }



   private void loadimage(String mail) {
       System.out.println("Inside Load Image\n");
       File pathExt = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
       System.out.println(pathExt);
       String m = mail;
       mainref = ref1.child(m).child("Images/Picture.jpg");
       String filename = "Image.jpg";

       File localFile = null;

       File sdcard = Environment.getDataDirectory();
       File dir = new File(pathExt + "/FriendLocator/ProfileImages/" + m);
       localFile = new File(dir, filename);

           localFile.getParentFile().mkdirs();


           String path = localFile.getAbsolutePath();
           System.out.println(pathExt);
           System.out.println(path);

           imageuri = Uri.fromFile(localFile);
           System.out.println(pathExt);
           System.out.println(path);


           mainref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                   // Local temp file has been created
                   Toast.makeText(MapsActivity.this, "Image downloaded successfully", Toast.LENGTH_LONG).show();
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception exception) {
                   Toast.makeText(MapsActivity.this, "Image not  downloaded ", Toast.LENGTH_LONG).show();
               }
           });
       }


    private void sendRequest(String Mobileno)
    {
        FireBaseRef2.child(ServiceMobile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(Mobile4).exists())
                {   Map RequestMap=new HashMap();
                    String Mobile =Mobile4;
                    boolean track=true;
                    RequestMap.put("Mobile", Mobile);
                    RequestMap.put("Track1", track);
                    FireBaseRef2.child(ServiceMobile).child(Mobile).push().setValue(RequestMap);}

                else if(dataSnapshot.child(Mobile4).exists()){
                    Map RequestMap=new HashMap();
                    String Mobile =Mobile4;
                    boolean track=true;
                    RequestMap.put("Mobile", Mobile);
                    RequestMap.put("Track1", track);
                    FireBaseRef2.child(ServiceMobile).child(Mobile).updateChildren(RequestMap);}

                }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Map RequestMap=new HashMap();
        //String Mobile =Mobileno;
        //boolean track=true;
      //  RequestMap.put("Mobile", Mobile);
    //    RequestMap.put("Track", track);
     //   RequestMap.put("Being_tracked", beingtracked);


        System.out.println("Inside SendRequest");


    }
    private void getRequest(String Mobileno)
    {
        FireBaseRef2.child(Mobile4).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(ServiceMobile).exists())
                {   Map RequestMap=new HashMap();
                    String Mobile =Mobile4;
                    boolean beingtracked=true;
                    RequestMap.put("Mobile",ServiceMobile);
                    RequestMap.put("BeingTracked", beingtracked);
                    FireBaseRef2.child(Mobile4).child(ServiceMobile).push().setValue(RequestMap);}

                else if(dataSnapshot.child(ServiceMobile).exists()){
                    Map RequestMap=new HashMap();
                    String Mobile =Mobile4;
                    boolean beingtracked=true;
                    RequestMap.put("Mobile", ServiceMobile);
                    RequestMap.put("BeingTracked", beingtracked);
                    FireBaseRef2.child(Mobile4).child(ServiceMobile).updateChildren(RequestMap);}

            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        System.out.println("Inside GetRequest");


    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


        private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Toast.makeText(MapsActivity.this, "\"Your Current  address:\", \"\"" + strReturnedAddress.toString(), Toast.LENGTH_LONG).show();
            } else {
                Log.w("Your Current  address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Your Current address", "Cannot get Address!");
        }
        return strAdd;
    }





    private void fetchfirebaselocation()
    {
        Firebase ref=new Firebase(FIREBASE_URL).child(MailId);

        ref.addChildEventListener(new ChildEventListener() {
            LatLngBounds bounds;
            LatLngBounds.Builder builder=new LatLngBounds.Builder();

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map Data1 = (Map) dataSnapshot.getValue();
                System.out.println(Data1);
                fetchmail=(String)Data1.get("Email-Id");
                fetchid=(String)Data1.get("UniqueID");
                System.out.println(fetchmail);
                System.out.println("fetchid:"+fetchid);
                System.out.println("UID1:"+UID1);

                System.out.println("Got Unique:"+Unique);
                MarkerOptions mp1=new MarkerOptions();
               if(!UID1.equals(fetchid))
                {Toast.makeText(context,"Invalid Mobile number or Unique ID",Toast.LENGTH_LONG).show();}
                else {
                   double latitude = (double) (Data1.get("latitude"));
                   double longitude = (double) (Data1.get("longitude"));

                   System.out.println("Database fetching");
                   System.out.println(latitude);
                   System.out.println(longitude);
                   BitmapFactory.Options opt = new BitmapFactory.Options();
                   opt.inDither = true;
                   opt.inJustDecodeBounds = false;
                   opt.inPreferredConfig = Bitmap.Config.ARGB_8888;

                   File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                   File filelocation = new File(sdcard + "/FriendLocator/ProfileImages/" + fetchmail + "/Image.jpg");

                   if (!filelocation.exists()) {
                       loadimage(fetchmail);
                   }

                   BitmapFactory.Options opt1 = new BitmapFactory.Options();
                   opt.inDither = true;
                   opt.inJustDecodeBounds = false;
                   opt.inPreferredConfig = Bitmap.Config.ARGB_8888;


                   bm1 = BitmapFactory.decodeFile(sdcard + "/FriendLocator/ProfileImages/" + fetchmail + "/Image.jpg", opt1);
                   if (bm1 == null) {
                       System.out.println("bitmap object null");

                       LatLng mLatlng = new LatLng(latitude, longitude);
                       builder.include(mLatlng);
                       bounds = builder.build();
                       //  mp1.position(mLatlng);
                       getCompleteAddressString(latitude, longitude);
                       mMap.addMarker(new MarkerOptions().position(mLatlng)).setDraggable(true);
                       mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 16));
                   } else if (bm1 != null) {
                       System.out.println("bitmap object present");
                       Bitmap bt1 = getResizedBitmap(bm1, 200);
                       LatLng mLatlng = new LatLng(latitude, longitude);
                       builder.include(mLatlng);
                       bounds = builder.build();
                       //mp1.position(mLatlng);
                       getCompleteAddressString(latitude, longitude);
                       mMap.addMarker(new MarkerOptions().position(mLatlng).icon(BitmapDescriptorFactory.fromBitmap(bt1))).setDraggable(true);
                       mMap.addMarker(new MarkerOptions().position(mLatlng)).setDraggable(true);
                       //   mp1.icon(BitmapDescriptorFactory.fromBitmap(bt1));
                       mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 16));

                   }


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

            }
        });



    }


//Location change googlemaps code//
    @Override
    public void onLocationChanged(Location location) {
       System.out.println("OnLocationChanged");
        mMap.clear();

        MarkerOptions mp = new MarkerOptions();

        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
        Date date = new Date();
        mLastUpdateTime = dateFormat.format(date);

if(isGpsEnabled || isNetworkEnabled){
if(count==0 ) {
    FireBaseRef1.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                keymap = new HashMap();
                keymap.put("key", postsnapshot.getKey());
                System.out.println(keymap);
                String unique1 = (String) (keymap.get("key"));
                FireBaseRef1.child(unique1).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot2, String s) {
                        Map ActualData = (Map) dataSnapshot2.getValue();
                        String Mail1 = (String) ActualData.get("Email");
                        System.out.println("Mail1:" + Mail1);
                        System.out.println("Mail:" + mail);
                        if (ActualData.containsValue(mail)) {
                            Map ActualData1 = ActualData;
                            System.out.println(ActualData1);
                            Mobile1 = (String) (ActualData1.get("Mobile"));
                            System.out.println("Mobile1:" + Mobile1);
                            FireBaseRef.child(Mobile1).child(Unique);
                            System.out.println(Unique);
                            sharedPreferences =PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("SharedMobile", Mobile1);
                            editor.commit();
                            count++;
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

                    }
                });


            }


        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    });

}}
          else{

    sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
     Mobile2=sharedPreferences.getString("SharedMobile","");

    FireBaseRef.child(Mobile2).child(Unique);
    System.out.println(Unique);
    System.out.println(Mobile2);
}
  //       FirebaseDatabase Database=FirebaseDatabase.getInstance();
//        DatabaseReference Ref=Database.getReference();

        Map mcoordinate=new HashMap<>();
        mcoordinate.put("latitude", location.getLatitude());
        mcoordinate.put("longitude", location.getLongitude());
        mcoordinate.put("DateTime", mLastUpdateTime);
        FireBaseRef.updateChildren(mcoordinate);






            //Firebase objectRef=FireBaseRef.child("Locations").child();




        System.out.println(" Firebase\n");

        mp.title("my position");

        mMap.addMarker(mp).setDraggable(true);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
 System.out.println("Connection Enabled\n");
    }

    @Override
    public void onProviderDisabled(String provider) {
        System.out.println("Please enable connection \n");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        setUpMap();

    }

    @Override
    public void onConnected(Bundle bundle) {

        setUpMap();

    }

    @Override
    public void onConnectionSuspended(int i) {
        FireBaseRef.unauth();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}
