package com.example.ashutosh.mapapplication;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Ashutosh on 21-09-2016.
 */
public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    private static final String TAG = "MyAndroidFCMIIDService";

    @Override
    public void onTokenRefresh() {
        //Get hold of the registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log the token
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }
    private void sendRegistrationToServer(String token) {
        //Implement this method if you want to store the token on your server
    }


}
